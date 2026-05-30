package com.mgasd.neonbeatssetlits.data.model

data class SpotifyExternalUrls(
    val spotify: String
)

data class SpotifyImage(
    val url: String,
    val height: Int?,
    val width: Int?
)

data class SpotifyArtist(
    val id: String,
    val name: String,
    val href: String?,
    val type: String?,
    val uri: String?,
    val external_urls: SpotifyExternalUrls?
)

data class SpotifyAlbum(
    val id: String,
    val name: String,
    val album_type: String?,
    val href: String?,
    val release_date: String?,
    val release_date_precision: String?,
    val uri: String?,
    val images: List<SpotifyImage>?,
    val external_urls: SpotifyExternalUrls?
)

data class SpotifyTrack(
    val id: String,
    val name: String,
    val uri: String,
    val href: String?,
    val duration_ms: Int,
    val explicit: Boolean?,
    val preview_url: String?,
    val track_number: Int?,
    val artists: List<SpotifyArtist>,
    val album: SpotifyAlbum?
)

data class SpotifyTracksPaging(
    val href: String?,
    val limit: Int,
    val next: String?,
    val offset: Int,
    val previous: String?,
    val total: Int,
    val items: List<SpotifyTrack>
)

data class SpotifySearchResponse(
    val tracks: SpotifyTracksPaging
)
