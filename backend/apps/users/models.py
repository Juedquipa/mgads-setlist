from django.contrib.auth.models import AbstractUser
from django.db import models
from django.utils.crypto import get_random_string
from django.utils.translation import gettext_lazy as _


def _generate_staff_pin(user_model):
    while True:
        pin = get_random_string(6, allowed_chars="0123456789")
        if not user_model.objects.filter(staff_pin=pin).exists():
            return pin


class User(AbstractUser):
    class RoleChoices(models.TextChoices):
        ADMIN = "ADMIN", _("Admin")
        WAITER = "WAITER", _("Waiter")

    staff_pin = models.CharField(max_length=6, unique=True, blank=True, null=True)
    role = models.CharField(
        max_length=50, choices=RoleChoices.choices, default=RoleChoices.WAITER
    )
    tenant = models.ForeignKey(
        "tenants.Tenant",
        on_delete=models.CASCADE,
        related_name="users",
        null=True,
        blank=True,
    )
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def save(self, *args, **kwargs):
        if (
            self.role in {self.RoleChoices.ADMIN, self.RoleChoices.WAITER}
            and not self.staff_pin
        ):
            self.staff_pin = _generate_staff_pin(type(self))
        super().save(*args, **kwargs)

    def __str__(self):
        return f"{self.username} - {self.get_role_display()}"
