from apps.music_queue.views import ClientSessionView, _extract_qr_code_token
from apps.tenants.models import Tenant
from apps.venues.models import Table
from django.test import TestCase
from rest_framework.test import APIRequestFactory


class ClientSessionViewTests(TestCase):
    def setUp(self):
        self.factory = APIRequestFactory()
        self.tenant = Tenant.objects.create(name="Test Tenant")
        self.table = Table.objects.create(
            tenant=self.tenant,
            name="Table 1",
            qr_code_token="TABLE-QR-123",
            is_active=True,
        )

    def test_extract_qr_code_token_accepts_raw_token(self):
        self.assertEqual(_extract_qr_code_token("TABLE-QR-123"), "TABLE-QR-123")

    def test_extract_qr_code_token_trims_whitespace(self):
        self.assertEqual(_extract_qr_code_token("  TABLE-QR-123  "), "TABLE-QR-123")

    def test_extract_qr_code_token_accepts_wrapped_url(self):
        url = "https://example.com/client/session/?qr_code=TABLE-QR-123"
        self.assertEqual(_extract_qr_code_token(url), "TABLE-QR-123")

    def test_post_creates_session_from_wrapped_url(self):
        request = self.factory.post(
            "/api/client/session/",
            {"qr_code": "https://example.com/client/session/?qr_code=TABLE-QR-123"},
            format="json",
        )

        response = ClientSessionView.as_view()(request)

        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.data["table"], self.table.id)
        self.assertEqual(response.data["credits_balance"], 0)
