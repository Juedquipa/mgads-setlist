from django.contrib.auth import get_user_model
from rest_framework import serializers
from rest_framework_simplejwt.serializers import TokenObtainPairSerializer

User = get_user_model()


class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = [
            "id",
            "username",
            "email",
            "first_name",
            "last_name",
            "role",
            "tenant",
        ]
        read_only_fields = ["id", "tenant", "role"]


class ErrorResponseSerializer(serializers.Serializer):
    detail = serializers.CharField()


class LoginRequestSerializer(serializers.Serializer):
    username = serializers.CharField(help_text="Account username.")
    password = serializers.CharField(help_text="Account password.", write_only=True)


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
