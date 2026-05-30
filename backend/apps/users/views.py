from django.contrib.auth import get_user_model
from rest_framework import permissions, viewsets
from rest_framework_simplejwt.views import TokenObtainPairView

from .serializers import CustomTokenObtainPairSerializer, UserSerializer

User = get_user_model()


class CustomTokenObtainPairView(TokenObtainPairView):
    serializer_class = CustomTokenObtainPairSerializer


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
