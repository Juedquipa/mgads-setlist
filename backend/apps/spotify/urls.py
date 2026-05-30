from django.urls import path

from .views import SpotifySearchView, SpotifyTrackView

urlpatterns = [
    path("search/", SpotifySearchView.as_view(), name="spotify-search"),
    path("track/<str:track_id>/", SpotifyTrackView.as_view(), name="spotify-track"),
]
