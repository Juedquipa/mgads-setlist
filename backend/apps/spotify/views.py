import requests
from drf_spectacular.utils import OpenApiParameter, extend_schema
from rest_framework import permissions, status, views
from rest_framework.response import Response

from .services import SpotifyService


class SpotifySearchView(views.APIView):
    permission_classes = [permissions.IsAuthenticated]

    @extend_schema(
        parameters=[
            OpenApiParameter(name="q", description="Search query", required=True, type=str),
        ],
        responses={200: dict, 400: dict, 502: dict},
    )
    def get(self, request):
        user = request.user
        if not hasattr(user, "tenant") or not user.tenant:
            return Response(
                {"detail": "No tenant associated context."},
                status=status.HTTP_400_BAD_REQUEST,
            )

        query = request.query_params.get("q")
        if not query:
            return Response(
                {"detail": "Query parameter 'q' is required."},
                status=status.HTTP_400_BAD_REQUEST,
            )

        try:
            data = SpotifyService.search_tracks(user.tenant, query)
            return Response(data)
        except ValueError as e:
            return Response({"detail": str(e)}, status=status.HTTP_400_BAD_REQUEST)
        except requests.RequestException:
            return Response({"detail": "Spotify API error."}, status=status.HTTP_502_BAD_GATEWAY)


class SpotifyTrackView(views.APIView):
    permission_classes = [permissions.IsAuthenticated]

    @extend_schema(responses={200: dict, 400: dict, 502: dict})
    def get(self, request, track_id):
        user = request.user
        if not hasattr(user, "tenant") or not user.tenant:
            return Response(
                {"detail": "No tenant associated context."},
                status=status.HTTP_400_BAD_REQUEST,
            )

        try:
            data = SpotifyService.get_track(user.tenant, track_id)
            return Response(data)
        except ValueError as e:
            return Response({"detail": str(e)}, status=status.HTTP_400_BAD_REQUEST)
        except requests.RequestException:
            return Response({"detail": "Spotify API error."}, status=status.HTTP_502_BAD_GATEWAY)
