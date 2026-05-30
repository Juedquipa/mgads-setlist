from rest_framework import serializers

from .models import PinCode, PlaybackQueue, Request, Session, Track


class PinCodeSerializer(serializers.ModelSerializer):
    class Meta:
        model = PinCode
        fields = [
            "id",
            "code",
            "credits",
            "is_used",
            "created_by",
            "table",
            "created_at",
            "used_at",
        ]
        read_only_fields = [
            "id",
            "code",
            "is_used",
            "created_by",
            "created_at",
            "used_at",
        ]


class SessionSerializer(serializers.ModelSerializer):
    class Meta:
        model = Session
        fields = [
            "id",
            "token",
            "credits_balance",
            "is_active",
            "table",
            "created_at",
            "expires_at",
        ]
        read_only_fields = ["id", "token", "credits_balance", "table", "created_at"]


class TrackSerializer(serializers.ModelSerializer):
    class Meta:
        model = Track
        fields = ["id", "spotify_id", "title", "artist", "album_art_url", "duration_ms"]


class RequestSerializer(serializers.ModelSerializer):
    track = TrackSerializer(read_only=True)
    track_id = serializers.PrimaryKeyRelatedField(
        queryset=Track.objects.all(), source="track", write_only=True
    )

    class Meta:
        model = Request
        fields = ["id", "track", "track_id", "status", "requested_at"]
        read_only_fields = ["id", "status", "requested_at"]


class PlaybackQueueSerializer(serializers.ModelSerializer):
    current_request = RequestSerializer(read_only=True)

    class Meta:
        model = PlaybackQueue
        fields = ["id", "current_request", "updated_at"]
        read_only_fields = ["id", "updated_at"]
