from django.db import models
from django.utils.crypto import get_random_string


class Table(models.Model):
    tenant = models.ForeignKey("tenants.Tenant", on_delete=models.CASCADE, related_name="tables")
    name = models.CharField(max_length=50)
    qr_code_token = models.CharField(max_length=255, unique=True, null=True, blank=True)
    is_active = models.BooleanField(default=True)
    created_at = models.DateTimeField(auto_now_add=True)

    def save(self, *args, **kwargs):
        if not self.qr_code_token:
            while True:
                token = get_random_string(32)
                if not Table.objects.filter(qr_code_token=token).exists():
                    self.qr_code_token = token
                    break
        super().save(*args, **kwargs)

    def __str__(self):
        return f"{self.tenant.name} - Table: {self.name}"


class Catalog(models.Model):
    tenant = models.OneToOneField(
        "tenants.Tenant", on_delete=models.CASCADE, related_name="catalog"
    )
    is_active = models.BooleanField(default=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return f"Catalog for {self.tenant.name}"
