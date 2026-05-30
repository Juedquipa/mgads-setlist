from rest_framework import serializers

from .models import PinCode, PlaybackQueue, Request, Session, Track


class ErrorResponseSerializer(serializers.Serializer):
    detail = serializers.CharField()


class ClientSessionRequestSerializer(serializers.Serializer):
    qr_code = serializers.CharField(help_text="QR code token printed on the table.")


class ClientPinCodeValidateRequestSerializer(serializers.Serializer):
    code = serializers.CharField(help_text="PIN code provided by the venue.")


class ClientPinCodeValidateResponseSerializer(serializers.Serializer):
    detail = serializers.CharField()
    credits_added = serializers.IntegerField()
    new_balance = serializers.IntegerField()


class ClientRequestSongRequestSerializer(serializers.Serializer):
    spotify_id = serializers.CharField(help_text="Spotify track identifier.")
    title = serializers.CharField(help_text="Track title.")
    artist = serializers.CharField(help_text="Track artist.")
    duration_ms = serializers.IntegerField(required=False, default=0)
    album_art_url = serializers.CharField(required=False, allow_blank=True, default="")


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
