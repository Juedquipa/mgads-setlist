package com.mgasd.neonbeatssetlits.data.model

data class ClientSessionRequest(
    val qr_code: String
)

data class Session(
    val id: Int,
    val token: String,
    val credits_balance: Int,
    val is_active: Boolean,
    val table: Int?,
    val created_at: String,
    val expires_at: String?
)
