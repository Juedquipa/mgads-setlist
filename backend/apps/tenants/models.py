from django.db import models
from django.utils.translation import gettext_lazy as _


class Tenant(models.Model):
    class TypeChoices(models.TextChoices):
        BAR = "BAR", _("Bar")
        NIGHTCLUB = "NIGHTCLUB", _("Nightclub")
        RESTAURANT = "RESTAURANT", _("Restaurant")

    name = models.CharField(max_length=255)
    tenant_type = models.CharField(
        max_length=50, choices=TypeChoices.choices, default=TypeChoices.BAR
    )
    spotify_client_id = models.CharField(max_length=255, blank=True, null=True)
    spotify_client_secret = models.CharField(max_length=255, blank=True, null=True)
    is_active = models.BooleanField(default=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return f"{self.name} ({self.tenant_type})"
