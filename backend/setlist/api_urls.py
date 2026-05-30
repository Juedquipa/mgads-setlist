from django.urls import include, path
from drf_spectacular.views import SpectacularAPIView, SpectacularSwaggerView
from rest_framework.routers import DefaultRouter
from rest_framework_simplejwt.views import TokenRefreshView

from apps.music_queue.views import (ApprovalViewSet, ClientPinCodeValidateView,
                                    ClientRequestSongView, ClientSessionView,
                                    PinCodeViewSet, QueueViewSet)
from apps.users.views import CustomTokenObtainPairView, WaiterViewSet
from apps.venues.views import TableViewSet

router = DefaultRouter()
router.register(r"waiters", WaiterViewSet, basename="waiter")
router.register(r"tables", TableViewSet, basename="table")
router.register(r"pin-codes", PinCodeViewSet, basename="pin-code")

queue_list = QueueViewSet.as_view({"get": "list"})
queue_clear = QueueViewSet.as_view({"delete": "clear"})
approval_list = ApprovalViewSet.as_view({"get": "list"})
approval_approve = ApprovalViewSet.as_view({"put": "approve"})
approval_reject = ApprovalViewSet.as_view({"put": "reject"})

urlpatterns = [
    # Auth
    path("auth/login/", CustomTokenObtainPairView.as_view(), name="token_obtain_pair"),
    path("auth/refresh/", TokenRefreshView.as_view(), name="token_refresh"),
    # Client specific endpoints
    path("client/session/", ClientSessionView.as_view(), name="client-session"),
    path(
        "client/pin-validate/",
        ClientPinCodeValidateView.as_view(),
        name="client-pin-validate",
    ),
    path(
        "client/request-song/",
        ClientRequestSongView.as_view(),
        name="client-request-song",
    ),
    # REST Routers
    path("", include(router.urls)),
    # Custom viewsets mapping
    path("queue/", queue_list, name="queue-list"),
    path("queue/clear/", queue_clear, name="queue-clear"),
    path("approvals/pending/", approval_list, name="approval-list"),
    path("approvals/<int:pk>/approve/", approval_approve, name="approval-approve"),
    path("approvals/<int:pk>/reject/", approval_reject, name="approval-reject"),
    path("spotify/", include("apps.spotify.urls")),
    # API Documentation (drf-spectacular)
    path("schema/", SpectacularAPIView.as_view(), name="schema"),
    path("docs/", SpectacularSwaggerView.as_view(url_name="schema"), name="swagger-ui"),
]
