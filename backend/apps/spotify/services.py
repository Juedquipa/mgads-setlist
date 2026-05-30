import base64

import requests
from django.core.cache import cache


class SpotifyService:
    @staticmethod
    def get_access_token(tenant):
        if not tenant.spotify_client_id or not tenant.spotify_client_secret:
            raise ValueError("Tenant missing Spotify credentials")

        cache_key = f"spotify_token_{tenant.id}"
        token = cache.get(cache_key)
        if token:
            return token

        auth_string = f"{tenant.spotify_client_id}:{tenant.spotify_client_secret}"
        auth_bytes = auth_string.encode("utf-8")
        auth_base64 = str(base64.b64encode(auth_bytes), "utf-8")

        url = "https://accounts.spotify.com/api/token"
        headers = {
            "Authorization": f"Basic {auth_base64}",
            "Content-Type": "application/x-www-form-urlencoded",
        }
        data = {"grant_type": "client_credentials"}

        response = requests.post(url, headers=headers, data=data)
        response.raise_for_status()

        response_data = response.json()
        token = response_data["access_token"]
        expires_in = response_data["expires_in"]

        # Cache the token slightly shorter than the actual expiration (which is usually 3600s)
        cache.set(cache_key, token, timeout=expires_in - 60)
        return token

    @staticmethod
    def search_tracks(tenant, query, limit=10):
        token = SpotifyService.get_access_token(tenant)
        url = "https://api.spotify.com/v1/search"
        headers = {"Authorization": f"Bearer {token}"}
        params = {"q": query, "type": "track", "limit": limit}

        response = requests.get(url, headers=headers, params=params)
        response.raise_for_status()
        return response.json()

    @staticmethod
    def get_track(tenant, track_id):
        token = SpotifyService.get_access_token(tenant)
        url = f"https://api.spotify.com/v1/tracks/{track_id}"
        headers = {"Authorization": f"Bearer {token}"}

        response = requests.get(url, headers=headers)
        response.raise_for_status()
        return response.json()
