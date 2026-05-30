from apps.venues.models import Table
from asgiref.sync import async_to_sync
from channels.layers import get_channel_layer
from django.utils.crypto import get_random_string
from rest_framework import permissions, status, views, viewsets
from rest_framework.decorators import action
from rest_framework.response import Response

from .models import PinCode, Request, Session
from .permissions import HasSessionToken
from .serializers import (
    PinCodeSerializer,
    RequestSerializer,
    SessionSerializer,
)


def broadcast_queue_update(tenant_id):
    channel_layer = get_channel_layer()
    async_to_sync(channel_layer.group_send)(
        f"queue_{tenant_id}",
        {"type": "queue_update", "message": "The queue has been updated"},
    )


class PinCodeViewSet(viewsets.ModelViewSet):
    serializer_class = PinCodeSerializer
    permission_classes = [permissions.IsAuthenticated]

    def get_queryset(self):
        user = self.request.user
        if not hasattr(user, "tenant") or not user.tenant:
            return PinCode.objects.none()
        return PinCode.objects.filter(tenant=user.tenant)

    def perform_create(self, serializer):
        code = get_random_string(6, allowed_chars="0123456789")
        serializer.save(tenant=self.request.user.tenant, created_by=self.request.user, code=code)


class QueueViewSet(viewsets.ViewSet):
    permission_classes = [permissions.IsAuthenticated]

    def list(self, request):
        user = request.user
        if not user.tenant:
            return Response([])

        # In a real app we might paginate the requests queue
        requests = Request.objects.filter(
            tenant=user.tenant, status=Request.StatusChoices.PENDING
        ).order_add("requested_at")
        serializer = RequestSerializer(requests, many=True)
        return Response(serializer.data)

    @action(detail=False, methods=["delete"])
    def clear(self, request):
        user = request.user
        if getattr(user, "role", None) != "ADMIN":
            return Response(status=status.HTTP_403_FORBIDDEN)

        if user.tenant:
            Request.objects.filter(
                tenant=user.tenant, status=Request.StatusChoices.PENDING
            ).delete()
            broadcast_queue_update(user.tenant.id)
        return Response(status=status.HTTP_204_NO_CONTENT)


class ApprovalViewSet(viewsets.ViewSet):
    permission_classes = [permissions.IsAuthenticated]

    def list(self, request):
        user = request.user
        if not user.tenant:
            return Response([])
        # Similar to queue, returning pending requests
        requests = Request.objects.filter(tenant=user.tenant, status=Request.StatusChoices.PENDING)
        return Response(RequestSerializer(requests, many=True).data)

    @action(detail=True, methods=["put"])
    def approve(self, request, pk=None):
        try:
            req = Request.objects.get(pk=pk, tenant=request.user.tenant)
            req.status = Request.StatusChoices.PLAYING
            req.save()
            broadcast_queue_update(request.user.tenant.id)
            return Response(RequestSerializer(req).data)
        except Request.DoesNotExist:
            return Response(status=status.HTTP_404_NOT_FOUND)

    @action(detail=True, methods=["put"])
    def reject(self, request, pk=None):
        try:
            req = Request.objects.get(pk=pk, tenant=request.user.tenant)
            req.status = Request.StatusChoices.SKIPPED
            req.save()
            # Refund logic here
            broadcast_queue_update(request.user.tenant.id)
            return Response(RequestSerializer(req).data)
        except Request.DoesNotExist:
            return Response(status=status.HTTP_404_NOT_FOUND)


class ClientSessionView(views.APIView):
    permission_classes = [permissions.AllowAny]

    def post(self, request):
        qr_token = request.data.get("qr_code")
        if not qr_token:
            return Response({"detail": "qr_code is required"}, status=status.HTTP_400_BAD_REQUEST)

        table = Table.objects.filter(qr_code_token=qr_token, is_active=True).first()
        if not table:
            return Response(
                {"detail": "Invalid or inactive QR code"},
                status=status.HTTP_404_NOT_FOUND,
            )

        session_token = get_random_string(64)
        session = Session.objects.create(
            tenant=table.tenant, table=table, token=session_token, credits_balance=0
        )
        return Response(SessionSerializer(session).data)


class ClientPinCodeValidateView(views.APIView):
    permission_classes = [HasSessionToken]

    def post(self, request):
        code = request.data.get("code")
        if not code:
            return Response({"detail": "Code is required"}, status=status.HTTP_400_BAD_REQUEST)

        session = request.client_session
        pin = PinCode.objects.filter(code=code, tenant=session.tenant, is_used=False).first()
        if not pin:
            return Response({"detail": "Invalid or used PIN"}, status=status.HTTP_404_NOT_FOUND)

        pin.is_used = True
        pin.save()

        session.credits_balance += pin.credits
        session.save()

        return Response(
            {
                "detail": "PIN applied successfully",
                "credits_added": pin.credits,
                "new_balance": session.credits_balance,
            }
        )


class ClientRequestSongView(views.APIView):
    permission_classes = [HasSessionToken]

    def post(self, request):
        session = request.client_session
        if session.credits_balance <= 0:
            return Response({"detail": "Insufficient credits"}, status=status.HTTP_400_BAD_REQUEST)

        spotify_id = request.data.get("spotify_id")
        title = request.data.get("title")
        artist = request.data.get("artist")
        duration_ms = request.data.get("duration_ms", 0)
        album_art_url = request.data.get("album_art_url", "")

        if not spotify_id or not title or not artist:
            return Response(
                {"detail": "spotify_id, title, and artist are required"},
                status=status.HTTP_400_BAD_REQUEST,
            )

        from .models import Track

        track, created = Track.objects.get_or_create(
            spotify_id=spotify_id,
            defaults={
                "title": title,
                "artist": artist,
                "duration_ms": duration_ms,
                "album_art_url": album_art_url,
            },
        )

        session.credits_balance -= 1
        session.save()

        song_req = Request.objects.create(
            tenant=session.tenant,
            session=session,
            track=track,
            status=Request.StatusChoices.PENDING,
        )

        broadcast_queue_update(session.tenant.id)

        return Response(RequestSerializer(song_req).data, status=status.HTTP_201_CREATED)
