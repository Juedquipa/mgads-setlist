import uuid

from asgiref.sync import async_to_sync
from channels.layers import get_channel_layer
from django.utils.crypto import get_random_string
from rest_framework import permissions, status, views, viewsets
from rest_framework.decorators import action
from rest_framework.response import Response

from .models import PinCode, PlaybackQueue, Request, Session
from .serializers import (PinCodeSerializer, PlaybackQueueSerializer,
                          RequestSerializer)


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
        if not user.tenant:
            return PinCode.objects.none()
        return PinCode.objects.filter(tenant=user.tenant)

    def perform_create(self, serializer):
        code = get_random_string(6, allowed_chars="0123456789")
        serializer.save(
            tenant=self.request.user.tenant, created_by=self.request.user, code=code
        )

    @action(detail=False, methods=["post"])
    def validate(self, request):
        code = request.data.get("code")
        if not code:
            return Response(
                {"detail": "Code is required"}, status=status.HTTP_400_BAD_REQUEST
            )

        pin = PinCode.objects.filter(code=code, is_used=False).first()
        if not pin:
            return Response(
                {"detail": "Invalid or used PIN"}, status=status.HTTP_404_NOT_FOUND
            )

        # Here we would lock the PIN and apply credits to the active session.
        # This requires the request to be from a valid Session context (JWT).
        # We'll stub this for later completion.
        return Response({"detail": "PIN validation logic stub", "credits": pin.credits})


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
        requests = Request.objects.filter(
            tenant=user.tenant, status=Request.StatusChoices.PENDING
        )
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
