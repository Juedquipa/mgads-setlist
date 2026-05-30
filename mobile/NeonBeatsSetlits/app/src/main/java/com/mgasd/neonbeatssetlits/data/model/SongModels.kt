package com.mgasd.neonbeatssetlits.data.model

import com.google.gson.annotations.SerializedName

enum class StatusEnum {
    @SerializedName("PENDING") PENDING,
    @SerializedName("PLAYING") PLAYING,
    @SerializedName("PLAYED") PLAYED,
    @SerializedName("SKIPPED") SKIPPED
}

data class Track(
    val id: Int,
    val spotify_id: String,
    val title: String,
    val artist: String,
    val album_art_url: String?,
    val duration_ms: Int
)

data class Request(
    val id: Int,
    val track: Track,
    val status: StatusEnum,
    val requested_at: String
)

data class ClientRequestSongRequest(
    val spotify_id: String,
    val title: String,
    val artist: String,
    val duration_ms: Int = 0,
    val album_art_url: String = ""
)
