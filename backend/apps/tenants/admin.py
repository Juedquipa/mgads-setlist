from django.contrib import admin

from .models import Tenant


@admin.register(Tenant)
class TenantAdmin(admin.ModelAdmin):
    list_display = ("name", "tenant_type", "is_active", "created_at")
    list_filter = ("tenant_type", "is_active")
    search_fields = ("name",)
    readonly_fields = ("created_at", "updated_at")
    fieldsets = (
        (None, {"fields": ("name", "tenant_type", "is_active")}),
        (
            "Spotify Credentials",
            {"fields": ("spotify_client_id", "spotify_client_secret")},
        ),
        ("Timestamps", {"fields": ("created_at", "updated_at")}),
    )
