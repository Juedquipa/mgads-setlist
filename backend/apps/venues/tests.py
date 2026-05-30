from apps.tenants.models import Tenant
from django.test import TestCase

from .models import Table


class TableModelTests(TestCase):
    def test_table_generates_qr_token_when_missing(self):
        tenant = Tenant.objects.create(name="Test Tenant")

        table = Table.objects.create(tenant=tenant, name="Table 1")

        self.assertIsNotNone(table.qr_code_token)
        self.assertNotEqual(table.qr_code_token, "")
