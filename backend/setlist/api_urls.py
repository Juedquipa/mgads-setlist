from django.urls import path, include
from rest_framework.routers import DefaultRouter
from drf_spectacular.views import SpectacularAPIView, SpectacularSwaggerView

from apps.users.views import WaiterViewSet, CustomTokenObtainPairView
from apps.venues.views import TableViewSet
from apps.music_queue.views import PinCodeViewSet, QueueViewSet, ApprovalViewSet
from rest_framework_simplejwt.views import TokenRefreshView

router = DefaultRouter()
router.register(r"waiters", WaiterViewSet, basename="waiter")
router.register(r"tables", TableViewSet, basename="table")
router.register(r"pin-codes", PinCodeViewSet, basename="pin-code")

queue_list = QueueViewSet.as_view({'get': 'list'})
queue_clear = QueueViewSet.as_view({'delete': 'clear'})
approval_list = ApprovalViewSet.as_view({"get": "list"})
approval_approve = ApprovalViewSet.as_view({"put": "approve"})
approval_reject = ApprovalViewSet.as_view({"put": "reject"})

urlpatterns = [
    # Auth
    path("auth/login/", CustomTokenObtainPairView.as_view(), name="token_obtain_pair"),
    path("auth/refresh/", TokenRefreshView.as_view(), name="token_refresh"),

    # REST Routers
    path("", include(router.urls)),

    # Custom viewsets mapping
    path("queue/", queue_list, name="queue-list"),
    path("queue/clear/", queue_clear, name="queue-clear"),
    
    path("approvals/pending/", approval_list, name="approval-list"),
    path("approvals/<int:pk>/approve/", approval_approve, name="approval-approve"),
    path("approvals/<int:pk>/reject/", approval_reject, name="approval-reject"),

    # API Documentation (drf-spectacular)
    path("schema/", SpectacularAPIView.as_view(), name="schema"),
    path("docs/", SpectacularSwaggerView.as_view(url_name="schema"), name="swagger-ui"),
]