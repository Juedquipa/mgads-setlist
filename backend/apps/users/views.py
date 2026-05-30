from django.contrib.auth import authenticate, get_user_model
from drf_spectacular.utils import (
    OpenApiExample,
    PolymorphicProxySerializer,
    extend_schema,
    extend_schema_view,
)
from rest_framework import permissions, status, views, viewsets
from rest_framework.response import Response
from rest_framework_simplejwt.views import TokenObtainPairView, TokenRefreshView

from .serializers import (
    CustomTokenObtainPairSerializer,
    ErrorResponseSerializer,
    StaffPasswordLoginRequestSerializer,
    StaffPinLoginRequestSerializer,
    TokenPairResponseSerializer,
    TokenRefreshRequestSerializer,
    TokenRefreshResponseSerializer,
    UserSerializer,
)

User = get_user_model()


def _issue_staff_token_pair(user):
    refresh = CustomTokenObtainPairSerializer.get_token(user)
    return Response({"refresh": str(refresh), "access": str(refresh.access_token)})


def _authenticate_staff_login(request):
    username = request.data.get("username")
    pin = request.data.get("pin")
    password = request.data.get("password")

    if not username:
        return None, Response(
            {"detail": "Username is required"},
            status=status.HTTP_400_BAD_REQUEST,
        )

    has_pin = bool(pin)
    has_password = bool(password)

    if has_pin == has_password:
        return None, Response(
            {"detail": "Provide either pin or password, not both"},
            status=status.HTTP_400_BAD_REQUEST,
        )

    if pin:
        user = User.objects.filter(
            username=username,
            staff_pin=pin,
            is_active=True,
            role__in=[User.RoleChoices.ADMIN, User.RoleChoices.WAITER],
        ).first()
        if not user:
            return None, Response(
                {"detail": "Invalid PIN"},
                status=status.HTTP_401_UNAUTHORIZED,
            )
        return user, None

    user = authenticate(request, username=username, password=password)
    if (
        not user
        or not user.is_active
        or user.role
        not in [
            User.RoleChoices.ADMIN,
            User.RoleChoices.WAITER,
        ]
    ):
        return None, Response(
            {"detail": "Invalid credentials"},
            status=status.HTTP_401_UNAUTHORIZED,
        )

    return user, None


class CustomTokenObtainPairView(TokenObtainPairView):
    serializer_class = CustomTokenObtainPairSerializer

    @extend_schema(
        summary="Log in with username plus PIN or password",
        description=(
            "Authenticates a staff user with username plus either a persistent staff PIN "
            "or a password, then returns a refresh token plus a short-lived access token. "
            "The access token includes custom role and tenant claims."
        ),
        request=PolymorphicProxySerializer(
            component_name="StaffLoginRequest",
            serializers=[
                StaffPinLoginRequestSerializer,
                StaffPasswordLoginRequestSerializer,
            ],
            resource_type_field_name=None,
        ),
        examples=[
            OpenApiExample(
                "Staff PIN login",
                value={"username": "staff@example.com", "pin": "1234"},
                request_only=True,
            ),
            OpenApiExample(
                "Staff username/password login",
                value={"username": "staff@example.com", "password": "secret"},
                request_only=True,
            ),
        ],
        responses={200: TokenPairResponseSerializer, 401: ErrorResponseSerializer},
    )
    def post(self, request, *args, **kwargs):
        user, error_response = _authenticate_staff_login(request)
        if error_response:
            return error_response

        return _issue_staff_token_pair(user)


class StaffPinLoginView(views.APIView):
    permission_classes = [permissions.AllowAny]

    @extend_schema(
        summary="Staff login alias",
        description=(
            "Alias for the main login flow so the staff-facing route exposes the same "
            "request bodies and authentication behavior."
        ),
        request=PolymorphicProxySerializer(
            component_name="StaffLoginRequest",
            serializers=[
                StaffPinLoginRequestSerializer,
                StaffPasswordLoginRequestSerializer,
            ],
            resource_type_field_name=None,
        ),
        responses={200: TokenPairResponseSerializer, 401: ErrorResponseSerializer},
    )
    def post(self, request, *args, **kwargs):
        user, error_response = _authenticate_staff_login(request)
        if error_response:
            return error_response

        return _issue_staff_token_pair(user)


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
