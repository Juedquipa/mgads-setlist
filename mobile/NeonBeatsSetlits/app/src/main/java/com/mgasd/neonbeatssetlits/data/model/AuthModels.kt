package com.mgasd.neonbeatssetlits.data.model

data class LoginRequest(
    val username: String,
    val password: String
)

data class TokenPairResponse(
    val refresh: String,
    val access: String
)

data class TokenRefreshRequest(
    val refresh: String
)

data class TokenRefreshResponse(
    val access: String
)
