from django.contrib.auth import get_user_model
from rest_framework import serializers
from rest_framework_simplejwt.serializers import TokenObtainPairSerializer

User = get_user_model()


class UserSerializer(serializers.ModelSerializer):
    staff_pin = serializers.CharField(read_only=True)

    class Meta:
        model = User
        fields = [
            "id",
            "username",
            "email",
            "first_name",
            "last_name",
            "staff_pin",
            "role",
            "tenant",
        ]
        read_only_fields = ["id", "tenant", "role", "staff_pin"]


class ErrorResponseSerializer(serializers.Serializer):
    detail = serializers.CharField()


class LoginRequestSerializer(serializers.Serializer):
    username = serializers.CharField(help_text="Account username.")
    password = serializers.CharField(help_text="Account password.", write_only=True)


class StaffPinLoginRequestSerializer(serializers.Serializer):
    username = serializers.CharField(help_text="Staff username.")
    pin = serializers.CharField(help_text="Persistent 4-digit staff PIN.")


class StaffPasswordLoginRequestSerializer(serializers.Serializer):
    username = serializers.CharField(help_text="Staff username.")
    password = serializers.CharField(
        help_text="Staff password.",
        write_only=True,
    )


class StaffLoginRequestSerializer(serializers.Serializer):
    username = serializers.CharField(
        help_text="Staff username. Required for both PIN and password login.",
    )
    password = serializers.CharField(
        required=False,
        help_text="Staff password. Provide this when logging in with username/password.",
        write_only=True,
    )
    pin = serializers.CharField(
        required=False,
        help_text="Persistent 4-digit staff PIN. Provide this when logging in with PIN.",
    )


class TokenPairResponseSerializer(serializers.Serializer):
    refresh = serializers.CharField(help_text="JWT refresh token.")
    access = serializers.CharField(help_text="JWT access token.")


class TokenRefreshRequestSerializer(serializers.Serializer):
    refresh = serializers.CharField(help_text="JWT refresh token.")


class TokenRefreshResponseSerializer(serializers.Serializer):
    access = serializers.CharField(help_text="New JWT access token.")


class CustomTokenObtainPairSerializer(TokenObtainPairSerializer):
    @classmethod
    def get_token(cls, user):
        token = super().get_token(user)

        # Add custom claims
        token["role"] = user.role
        token["tenant_id"] = user.tenant.id if user.tenant else None

        return token
