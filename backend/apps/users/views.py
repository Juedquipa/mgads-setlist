from django.contrib.auth import get_user_model
from drf_spectacular.utils import extend_schema, extend_schema_view
from rest_framework import permissions, viewsets
from rest_framework_simplejwt.views import TokenObtainPairView, TokenRefreshView

from .serializers import (
    CustomTokenObtainPairSerializer,
    ErrorResponseSerializer,
    LoginRequestSerializer,
    TokenPairResponseSerializer,
    TokenRefreshRequestSerializer,
    TokenRefreshResponseSerializer,
    UserSerializer,
)

User = get_user_model()


class CustomTokenObtainPairView(TokenObtainPairView):
    serializer_class = CustomTokenObtainPairSerializer

    @extend_schema(
        summary="Log in and get JWT tokens",
        description=(
            "Authenticates a user with username and password and returns a refresh token "
            "plus a short-lived access token. The access token includes custom role and "
            "tenant claims."
        ),
        request=LoginRequestSerializer,
        responses={200: TokenPairResponseSerializer, 401: ErrorResponseSerializer},
    )
    def post(self, request, *args, **kwargs):
        return super().post(request, *args, **kwargs)


class CustomTokenRefreshView(TokenRefreshView):
    @extend_schema(
        summary="Refresh an access token",
        description="Exchanges a valid refresh token for a new access token.",
        request=TokenRefreshRequestSerializer,
        responses={200: TokenRefreshResponseSerializer, 401: ErrorResponseSerializer},
    )
    def post(self, request, *args, **kwargs):
        return super().post(request, *args, **kwargs)


@extend_schema_view(
    list=extend_schema(
        summary="List waiters",
        description="Returns the waiters that belong to the authenticated user's tenant.",
        responses=UserSerializer(many=True),
    ),
    retrieve=extend_schema(
        summary="Get a waiter",
        description="Returns a single waiter from the authenticated user's tenant.",
        responses=UserSerializer,
    ),
    create=extend_schema(
        summary="Create a waiter",
        description="Creates a waiter user in the authenticated user's tenant.",
        request=UserSerializer,
        responses=UserSerializer,
    ),
    update=extend_schema(
        summary="Update a waiter",
        description="Updates a waiter that belongs to the authenticated user's tenant.",
        request=UserSerializer,
        responses=UserSerializer,
    ),
    partial_update=extend_schema(
        summary="Partially update a waiter",
        description="Updates one or more waiter fields in the authenticated user's tenant.",
        request=UserSerializer,
        responses=UserSerializer,
    ),
    destroy=extend_schema(
        summary="Delete a waiter",
        description="Deletes a waiter that belongs to the authenticated user's tenant.",
        responses={204: None},
    ),
)
class WaiterViewSet(viewsets.ModelViewSet):
    serializer_class = UserSerializer
    permission_classes = [permissions.IsAuthenticated]

    def get_queryset(self):
        # Admins can only see waiters from their own tenant
        user = self.request.user
        if not user.tenant:
            return User.objects.none()
        return User.objects.filter(tenant=user.tenant, role=User.RoleChoices.WAITER)

    def perform_create(self, serializer):
        serializer.save(tenant=self.request.user.tenant, role=User.RoleChoices.WAITER)
