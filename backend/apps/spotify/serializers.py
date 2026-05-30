from rest_framework import serializers


class SpotifyExternalUrlsSerializer(serializers.Serializer):
    spotify = serializers.URLField()


class SpotifyImageSerializer(serializers.Serializer):
    height = serializers.IntegerField(required=False, allow_null=True)
    url = serializers.URLField()
    width = serializers.IntegerField(required=False, allow_null=True)


class SpotifyArtistSerializer(serializers.Serializer):
    id = serializers.CharField()
    name = serializers.CharField()
    href = serializers.URLField(required=False, allow_blank=True)
    type = serializers.CharField(required=False)
    uri = serializers.CharField(required=False)
    external_urls = SpotifyExternalUrlsSerializer(required=False)


class SpotifyAlbumSerializer(serializers.Serializer):
    id = serializers.CharField()
    name = serializers.CharField()
    album_type = serializers.CharField(required=False)
    href = serializers.URLField(required=False, allow_blank=True)
    release_date = serializers.CharField(required=False)
    release_date_precision = serializers.CharField(required=False)
    uri = serializers.CharField(required=False)
    images = SpotifyImageSerializer(many=True, required=False)
    external_urls = SpotifyExternalUrlsSerializer(required=False)


class SpotifyTrackSerializer(serializers.Serializer):
    id = serializers.CharField()
    name = serializers.CharField()
    uri = serializers.CharField()
    href = serializers.URLField(required=False, allow_blank=True)
    duration_ms = serializers.IntegerField()
    explicit = serializers.BooleanField(required=False)
    preview_url = serializers.URLField(required=False, allow_null=True)
    track_number = serializers.IntegerField(required=False)
    artists = SpotifyArtistSerializer(many=True)
    album = SpotifyAlbumSerializer(required=False)


class SpotifyTracksPagingSerializer(serializers.Serializer):
    href = serializers.URLField(required=False, allow_blank=True)
    limit = serializers.IntegerField()
    next = serializers.URLField(required=False, allow_null=True)
    offset = serializers.IntegerField()
    previous = serializers.URLField(required=False, allow_null=True)
    total = serializers.IntegerField()
    items = SpotifyTrackSerializer(many=True)


class SpotifySearchResponseSerializer(serializers.Serializer):
    tracks = SpotifyTracksPagingSerializer()
