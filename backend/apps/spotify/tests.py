from unittest.mock import patch

from apps.music_queue.models import Session
from apps.tenants.models import Tenant
from django.test import TestCase
from rest_framework.test import APIRequestFactory

from .views import SpotifySearchView


class SpotifySearchViewTests(TestCase):
    def setUp(self):
        self.factory = APIRequestFactory()
        self.tenant = Tenant.objects.create(name="Test Tenant")
        self.session = Session.objects.create(
            tenant=self.tenant,
            table=None,
            token="session-token-123",
            credits_balance=0,
            is_active=True,
        )

    @patch("apps.spotify.views.SpotifyService.search_tracks")
    def test_search_accepts_session_token(self, mock_search_tracks):
        mock_search_tracks.return_value = {"tracks": {"items": []}}

        request = self.factory.get(
            "/api/spotify/search/",
            {"q": "abba"},
            HTTP_X_SESSION_TOKEN=self.session.token,
        )

        response = SpotifySearchView.as_view()(request)

        self.assertEqual(response.status_code, 200)
        mock_search_tracks.assert_called_once_with(self.tenant, "abba")

    def test_search_rejects_missing_auth(self):
        request = self.factory.get("/api/spotify/search/", {"q": "abba"})

        response = SpotifySearchView.as_view()(request)

        self.assertEqual(response.status_code, 401)
