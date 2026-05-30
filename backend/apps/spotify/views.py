import requests
from apps.music_queue.models import Session
from apps.users.serializers import ErrorResponseSerializer
from drf_spectacular.utils import OpenApiParameter, extend_schema
from rest_framework import permissions, status, views
from rest_framework.response import Response

from .serializers import SpotifySearchResponseSerializer, SpotifyTrackSerializer
from .services import SpotifyService


class SpotifySearchView(views.APIView):
    permission_classes = [permissions.AllowAny]

    def _resolve_tenant(self, request):
        user = request.user
        if getattr(user, "is_authenticated", False) and getattr(user, "tenant", None):
            return user.tenant

        token = request.headers.get("X-Session-Token")
        if not token:
            return None

        session = (
            Session.objects.select_related("tenant").filter(token=token, is_active=True).first()
        )
        if session:
            request.client_session = session
            return session.tenant

        return None

    @extend_schema(
        summary="Search Spotify tracks",
        description=(
            "Searches the tenant's configured Spotify catalog for tracks that match "
            "the given query."
        ),
        parameters=[
            OpenApiParameter(
                name="q",
                description="Free-text search query.",
                required=True,
                type=str,
            ),
        ],
        responses={
            200: SpotifySearchResponseSerializer,
            400: ErrorResponseSerializer,
            502: ErrorResponseSerializer,
        },
    )
    def get(self, request):
        tenant = self._resolve_tenant(request)
        if not tenant:
            return Response(
                {"detail": "Authentication required."},
                status=status.HTTP_401_UNAUTHORIZED,
            )

        query = request.query_params.get("q")
        if not query:
            return Response(
                {"detail": "Query parameter 'q' is required."},
                status=status.HTTP_400_BAD_REQUEST,
            )

        try:
            data = SpotifyService.search_tracks(tenant, query)
            return Response(data)
        except ValueError as e:
            return Response({"detail": str(e)}, status=status.HTTP_400_BAD_REQUEST)
        except requests.RequestException:
            return Response({"detail": "Spotify API error."}, status=status.HTTP_502_BAD_GATEWAY)


class SpotifyTrackView(views.APIView):
    permission_classes = [permissions.AllowAny]

    def _resolve_tenant(self, request):
        user = request.user
        if getattr(user, "is_authenticated", False) and getattr(user, "tenant", None):
            return user.tenant

        token = request.headers.get("X-Session-Token")
        if not token:
            return None

        session = (
            Session.objects.select_related("tenant").filter(token=token, is_active=True).first()
        )
        if session:
            request.client_session = session
            return session.tenant

        return None

    @extend_schema(
        summary="Get a Spotify track",
        description=(
            "Fetches a single track by Spotify ID from the tenant's configured Spotify account."
        ),
        parameters=[
            OpenApiParameter(
                name="track_id",
                location=OpenApiParameter.PATH,
                required=True,
                type=str,
                description="Spotify track ID to retrieve.",
            )
        ],
        responses={
            200: SpotifyTrackSerializer,
            400: ErrorResponseSerializer,
            502: ErrorResponseSerializer,
        },
    )
    def get(self, request, track_id):
        tenant = self._resolve_tenant(request)
        if not tenant:
            return Response(
                {"detail": "Authentication required."},
                status=status.HTTP_401_UNAUTHORIZED,
            )

        try:
            data = SpotifyService.get_track(tenant, track_id)
            return Response(data)
        except ValueError as e:
            return Response({"detail": str(e)}, status=status.HTTP_400_BAD_REQUEST)
        except requests.RequestException:
            return Response({"detail": "Spotify API error."}, status=status.HTTP_502_BAD_GATEWAY)
