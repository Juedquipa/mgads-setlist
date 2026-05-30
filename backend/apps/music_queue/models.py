from django.db import models
from django.utils.translation import gettext_lazy as _


class PinCode(models.Model):
    tenant = models.ForeignKey("tenants.Tenant", on_delete=models.CASCADE, related_name="pin_codes")
    code = models.CharField(max_length=10)
    credits = models.PositiveIntegerField(default=1)
    is_used = models.BooleanField(default=False)
    created_by = models.ForeignKey(
        "users.User",
        on_delete=models.SET_NULL,
        null=True,
        related_name="generated_pins",
    )
    table = models.ForeignKey(
        "venues.Table",
        on_delete=models.SET_NULL,
        null=True,
        blank=True,
        related_name="pin_codes",
    )
    created_at = models.DateTimeField(auto_now_add=True)
    used_at = models.DateTimeField(null=True, blank=True)

    def __str__(self):
        return f"PIN: {self.code} - Credits: {self.credits}"


class Session(models.Model):
    tenant = models.ForeignKey("tenants.Tenant", on_delete=models.CASCADE, related_name="sessions")
    table = models.ForeignKey(
        "venues.Table", on_delete=models.SET_NULL, null=True, related_name="sessions"
    )
    token = models.CharField(max_length=255, unique=True)
    credits_balance = models.PositiveIntegerField(default=0)
    is_active = models.BooleanField(default=True)
    created_at = models.DateTimeField(auto_now_add=True)
    expires_at = models.DateTimeField(null=True, blank=True)

    def __str__(self):
        return f"Session {self.token[:8]}... Table {self.table.name if self.table else 'N/A'}"


class Track(models.Model):
    spotify_id = models.CharField(max_length=255, unique=True)
    title = models.CharField(max_length=255)
    artist = models.CharField(max_length=255)
    album_art_url = models.URLField(blank=True, null=True)
    duration_ms = models.PositiveIntegerField()

    def __str__(self):
        return f"{self.title} - {self.artist}"


class Request(models.Model):
    class StatusChoices(models.TextChoices):
        PENDING = "PENDING", _("Pending")
        PLAYING = "PLAYING", _("Playing")
        PLAYED = "PLAYED", _("Played")
        SKIPPED = "SKIPPED", _("Skipped")

    tenant = models.ForeignKey("tenants.Tenant", on_delete=models.CASCADE, related_name="requests")
    session = models.ForeignKey(Session, on_delete=models.CASCADE, related_name="requests")
    track = models.ForeignKey(Track, on_delete=models.CASCADE, related_name="requests")
    status = models.CharField(
        max_length=50, choices=StatusChoices.choices, default=StatusChoices.PENDING
    )
    requested_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"Request {self.track.title} by Session {self.session.token[:8]}..."


class PlaybackQueue(models.Model):
    tenant = models.OneToOneField(
        "tenants.Tenant", on_delete=models.CASCADE, related_name="playback_queue"
    )
    current_request = models.ForeignKey(
        Request, on_delete=models.SET_NULL, null=True, blank=True, related_name="+"
    )
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return f"Queue for {self.tenant.name}"
