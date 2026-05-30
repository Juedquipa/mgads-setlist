from django.contrib import admin

from .models import PinCode, PlaybackQueue, Request, Session, Track


@admin.register(PinCode)
class PinCodeAdmin(admin.ModelAdmin):
    list_display = ("code", "tenant", "credits", "is_used", "created_by", "table")
    list_filter = ("is_used", "tenant")
    search_fields = ("code",)


@admin.register(Session)
class SessionAdmin(admin.ModelAdmin):
    list_display = ("token", "tenant", "table", "credits_balance", "is_active")
    list_filter = ("is_active", "tenant")
    search_fields = ("token",)


@admin.register(Track)
class TrackAdmin(admin.ModelAdmin):
    list_display = ("title", "artist", "spotify_id", "duration_ms")
    search_fields = ("title", "artist", "spotify_id")


@admin.register(Request)
class RequestAdmin(admin.ModelAdmin):
    list_display = ("track", "session", "tenant", "status", "requested_at")
    list_filter = ("status", "tenant")
    search_fields = ("track__title", "session__token")


@admin.register(PlaybackQueue)
class PlaybackQueueAdmin(admin.ModelAdmin):
    list_display = ("tenant", "current_request", "updated_at")
    search_fields = ("tenant__name",)
