package com.mgasd.neonbeatssetlits.network

import com.mgasd.neonbeatssetlits.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // Auth
    @POST("api/auth/login/")
    suspend fun login(@Body request: LoginRequest): Response<TokenPairResponse>

    @POST("api/auth/refresh/")
    suspend fun refresh(@Body request: TokenRefreshRequest): Response<TokenRefreshResponse>

    // Client
    @POST("api/client/session/")
    suspend fun createClientSession(@Body request: ClientSessionRequest): Response<Session>

    @POST("api/client/pin-login/")
    suspend fun clientPinLogin(@Body request: ClientPinCodeValidateRequest): Response<Session>

    @POST("api/client/pin-validate/")
    suspend fun validatePin(
        @Header("X-Session-Token") sessionToken: String,
        @Body request: ClientPinCodeValidateRequest
    ): Response<ClientPinCodeValidateResponse>

    @POST("api/client/request-song/")
    suspend fun requestSong(
        @Header("X-Session-Token") sessionToken: String,
        @Body request: ClientRequestSongRequest
    ): Response<Request>

    // Approvals (Waiters/Admin)
    @GET("api/approvals/pending/")
    suspend fun listPendingApprovals(): Response<List<Request>>

    @PUT("api/approvals/{id}/approve/")
    suspend fun approveRequest(@Path("id") id: Int): Response<Request>

    @PUT("api/approvals/{id}/reject/")
    suspend fun rejectRequest(@Path("id") id: Int): Response<Request>

    // Spotify
    @GET("api/spotify/search/")
    suspend fun searchSpotify(@Query("q") query: String): Response<SpotifySearchResponse>

    @GET("api/spotify/track/{track_id}/")
    suspend fun getSpotifyTrack(@Path("track_id") trackId: String): Response<SpotifyTrack>

    // Tables
    @GET("api/tables/")
    suspend fun listTables(): Response<List<Table>>

    @POST("api/tables/")
    suspend fun createTable(@Body table: Table): Response<Table>

    @DELETE("api/tables/{id}/")
    suspend fun deleteTable(@Path("id") id: Int): Response<Unit>

    @GET("api/tables/{id}/session/")
    suspend fun getTableActiveSession(@Path("id") tableId: String): Response<Session>

    // Pin Codes
    @GET("api/pin-codes/")
    suspend fun listPinCodes(): Response<List<PinCode>>

    @POST("api/pin-codes/")
    suspend fun createPinCode(@Body pinCode: PinCode): Response<PinCode>

    // Waiters
    @GET("api/waiters/")
    suspend fun listWaiters(): Response<List<User>>

    // Queue
    @GET("api/queue/")
    suspend fun getQueue(): Response<List<Request>>

    @DELETE("api/queue/clear/")
    suspend fun clearQueue(): Response<Unit>
}
