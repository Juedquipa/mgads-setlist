from apps.music_queue.serializers import ErrorResponseSerializer, SessionSerializer
from drf_spectacular.utils import extend_schema, extend_schema_view
from rest_framework import permissions, status, viewsets
from rest_framework.decorators import action
from rest_framework.response import Response

from .models import Table
from .serializers import TableSerializer


@extend_schema_view(
    list=extend_schema(
        summary="List tables",
        description="Returns the tables that belong to the authenticated user's tenant.",
        responses=TableSerializer(many=True),
    ),
    retrieve=extend_schema(
        summary="Get a table",
        description="Returns a single table from the authenticated user's tenant.",
        responses=TableSerializer,
    ),
    create=extend_schema(
        summary="Create a table",
        description="Creates a new table under the authenticated user's tenant.",
        request=TableSerializer,
        responses=TableSerializer,
    ),
    update=extend_schema(
        summary="Update a table",
        description="Updates a table that belongs to the authenticated user's tenant.",
        request=TableSerializer,
        responses=TableSerializer,
    ),
    partial_update=extend_schema(
        summary="Partially update a table",
        description=("Updates one or more fields on a table in the authenticated user's tenant."),
        request=TableSerializer,
        responses=TableSerializer,
    ),
    destroy=extend_schema(
        summary="Delete a table",
        description="Deletes a table from the authenticated user's tenant.",
        responses={204: None},
    ),
)
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

    @extend_schema(
        summary="Get the active session for a table",
        description=("Returns the active client session for the requested table, if one exists."),
        responses={200: SessionSerializer, 404: ErrorResponseSerializer},
    )
    @action(detail=True, methods=["get"])
    def session(self, request, pk=None):
        table = self.get_object()
        active_session = table.sessions.filter(is_active=True).first()
        if not active_session:
            return Response({"detail": "No active session"}, status=status.HTTP_404_NOT_FOUND)

        # We would serialize the session here. For now returning a placeholder
        from apps.music_queue.serializers import SessionSerializer

        return Response(SessionSerializer(active_session).data)
