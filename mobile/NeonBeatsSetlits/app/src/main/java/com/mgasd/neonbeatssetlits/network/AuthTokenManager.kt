package com.mgasd.neonbeatssetlits.network

object AuthTokenManager {
    @Volatile
    private var accessToken: String? = null

    fun setAccessToken(token: String?) {
        accessToken = token
    }

    fun clear() {
        accessToken = null
    }

    fun getAccessToken(): String? = accessToken
}