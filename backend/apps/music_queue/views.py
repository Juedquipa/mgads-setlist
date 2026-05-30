from apps.venues.models import Table
from asgiref.sync import async_to_sync
from channels.layers import get_channel_layer
from django.utils.crypto import get_random_string
from drf_spectacular.utils import OpenApiParameter, extend_schema, extend_schema_view
from rest_framework import permissions, status, views, viewsets
from rest_framework.decorators import action
from rest_framework.response import Response

from .models import PinCode, Request, Session
from .permissions import HasSessionToken
from .serializers import (
    ClientPinCodeValidateRequestSerializer,
    ClientPinCodeValidateResponseSerializer,
    ClientRequestSongRequestSerializer,
    ClientSessionRequestSerializer,
    ErrorResponseSerializer,
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


@extend_schema_view(
    list=extend_schema(
        summary="List PIN codes",
        description=("Returns the PIN codes that belong to the authenticated user's tenant."),
        responses=PinCodeSerializer(many=True),
    ),
    retrieve=extend_schema(
        summary="Get a PIN code",
        description="Returns a single PIN code from the authenticated user's tenant.",
        responses=PinCodeSerializer,
    ),
    create=extend_schema(
        summary="Create a PIN code",
        description=(
            "Creates a new tenant-bound PIN code and generates the code value automatically."
        ),
        request=PinCodeSerializer,
        responses=PinCodeSerializer,
    ),
    update=extend_schema(
        summary="Update a PIN code",
        description="Updates an existing PIN code in the authenticated user's tenant.",
        request=PinCodeSerializer,
        responses=PinCodeSerializer,
    ),
    partial_update=extend_schema(
        summary="Partially update a PIN code",
        description=(
            "Updates one or more fields on a PIN code in the authenticated user's tenant."
        ),
        request=PinCodeSerializer,
        responses=PinCodeSerializer,
    ),
    destroy=extend_schema(
        summary="Delete a PIN code",
        description="Deletes a PIN code from the authenticated user's tenant.",
        responses={204: None},
    ),
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


@extend_schema_view(
    list=extend_schema(
        summary="List pending queue items",
        description=(
            "Returns the pending song requests for the authenticated user's tenant, "
            "ordered by request time."
        ),
        responses=RequestSerializer(many=True),
    ),
    clear=extend_schema(
        summary="Clear the pending queue",
        description=(
            "Deletes all pending song requests for the authenticated user's tenant. "
            "This action is restricted to ADMIN users."
        ),
        responses={204: None, 403: ErrorResponseSerializer},
    ),
)
class QueueViewSet(viewsets.ViewSet):
    permission_classes = [permissions.IsAuthenticated]

    def list(self, request):
        user = request.user
        if not user.tenant:
            return Response([])

        # In a real app we might paginate the requests queue
        requests = Request.objects.filter(
            tenant=user.tenant, status=Request.StatusChoices.PENDING
        ).order_by("requested_at")
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


@extend_schema_view(
    list=extend_schema(
        summary="List pending approvals",
        description=(
            "Returns the pending song requests that can be approved or rejected for the "
            "authenticated user's tenant."
        ),
        responses=RequestSerializer(many=True),
    ),
)
class ApprovalViewSet(viewsets.ViewSet):
    permission_classes = [permissions.IsAuthenticated]

    def list(self, request):
        user = request.user
        if not user.tenant:
            return Response([])
        # Similar to queue, returning pending requests
        requests = Request.objects.filter(tenant=user.tenant, status=Request.StatusChoices.PENDING)
        return Response(RequestSerializer(requests, many=True).data)

    @extend_schema(
        summary="Approve a request",
        description=(
            "Marks a pending request as playing and broadcasts the queue update for the "
            "current tenant."
        ),
        responses={200: RequestSerializer, 404: ErrorResponseSerializer},
    )
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

    @extend_schema(
        summary="Reject a request",
        description=(
            "Marks a pending request as skipped and broadcasts the queue update for the "
            "current tenant."
        ),
        responses={200: RequestSerializer, 404: ErrorResponseSerializer},
    )
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

    @extend_schema(
        operation_id="client_session_create",
        summary="Create a client session",
        description=(
            "Validates an active table QR code and creates a temporary client session "
            "for the table. The returned token is required by the client-only endpoints."
        ),
        request=ClientSessionRequestSerializer,
        responses={
            200: SessionSerializer,
            400: ErrorResponseSerializer,
            404: ErrorResponseSerializer,
        },
    )
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

    @extend_schema(
        summary="Validate a PIN code",
        description=(
            "Applies an unused venue PIN code to the current client session and adds its "
            "credits to the session balance. Requires the X-Session-Token header."
        ),
        parameters=[
            OpenApiParameter(
                name="X-Session-Token",
                location=OpenApiParameter.HEADER,
                required=True,
                type=str,
                description="Session token returned by the client session creation endpoint.",
            )
        ],
        request=ClientPinCodeValidateRequestSerializer,
        responses={
            200: ClientPinCodeValidateResponseSerializer,
            400: ErrorResponseSerializer,
            403: ErrorResponseSerializer,
            404: ErrorResponseSerializer,
        },
    )
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

    @extend_schema(
        summary="Request a song",
        description=(
            "Creates a pending song request for the current client session and consumes one "
            "credit from the session balance. Requires the X-Session-Token header."
        ),
        parameters=[
            OpenApiParameter(
                name="X-Session-Token",
                location=OpenApiParameter.HEADER,
                required=True,
                type=str,
                description="Session token returned by the client session creation endpoint.",
            )
        ],
        request=ClientRequestSongRequestSerializer,
        responses={
            201: RequestSerializer,
            400: ErrorResponseSerializer,
            403: ErrorResponseSerializer,
        },
    )
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
