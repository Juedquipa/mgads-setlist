from django.contrib.auth import authenticate, get_user_model
from drf_spectacular.utils import extend_schema, extend_schema_view
from rest_framework import permissions, status, views, viewsets
from rest_framework.response import Response
from rest_framework_simplejwt.views import (TokenObtainPairView,
                                            TokenRefreshView)

from .serializers import (CustomTokenObtainPairSerializer,
                          ErrorResponseSerializer, LoginRequestSerializer,
                          StaffLoginRequestSerializer,
                          TokenPairResponseSerializer,
                          TokenRefreshRequestSerializer,
                          TokenRefreshResponseSerializer, UserSerializer)

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


class StaffPinLoginView(views.APIView):
    permission_classes = [permissions.AllowAny]

    @extend_schema(
        summary="Staff log in with a PIN or password",
        description=(
            "Authenticates a staff user either with a persistent staff PIN or with "
            "username and password, then returns a refresh token plus a short-lived access "
            "token. The access token includes custom role and tenant claims."
        ),
        request=StaffLoginRequestSerializer,
        responses={200: TokenPairResponseSerializer, 401: ErrorResponseSerializer},
    )
    def post(self, request, *args, **kwargs):
        pin = request.data.get("pin")
        if pin:
            user = User.objects.filter(
                staff_pin=pin,
                is_active=True,
                role__in=[User.RoleChoices.ADMIN, User.RoleChoices.WAITER],
            ).first()
            if not user:
                return Response(
                    {"detail": "Invalid PIN"},
                    status=status.HTTP_401_UNAUTHORIZED,
                )
        else:
            username = request.data.get("username")
            password = request.data.get("password")
            if not username or not password:
                return Response(
                    {"detail": "Provide either pin or username and password"},
                    status=status.HTTP_400_BAD_REQUEST,
                )

            user = authenticate(
                request,
                username=username,
                password=password,
            )
            if (
                not user
                or not user.is_active
                or user.role
                not in [
                    User.RoleChoices.ADMIN,
                    User.RoleChoices.WAITER,
                ]
            ):
                return Response(
                    {"detail": "Invalid credentials"},
                    status=status.HTTP_401_UNAUTHORIZED,
                )

        refresh = CustomTokenObtainPairSerializer.get_token(user)
        return Response({"refresh": str(refresh), "access": str(refresh.access_token)})


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
