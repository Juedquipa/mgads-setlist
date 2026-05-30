from rest_framework import viewsets, permissions, status
from rest_framework.decorators import action
from rest_framework.response import Response
from .models import Table
from .serializers import TableSerializer

class TableViewSet(viewsets.ModelViewSet):
    serializer_class = TableSerializer
    permission_classes = [permissions.IsAuthenticated]

    def get_queryset(self):
        user = self.request.user
        if not user.tenant:
            return Table.objects.none()
        return Table.objects.filter(tenant=user.tenant)

    def perform_create(self, serializer):
        # We might generate a unique qr_code_token here if needed, or in the model save()
        serializer.save(tenant=self.request.user.tenant)

    @action(detail=True, methods=["get"])
    def session(self, request, pk=None):
        table = self.get_object()
        active_session = table.sessions.filter(is_active=True).first()
        if not active_session:
            return Response({"detail": "No active session"}, status=status.HTTP_404_NOT_FOUND)
        
        # We would serialize the session here. For now returning a placeholder
        from apps.music_queue.serializers import SessionSerializer
        return Response(SessionSerializer(active_session).data)
