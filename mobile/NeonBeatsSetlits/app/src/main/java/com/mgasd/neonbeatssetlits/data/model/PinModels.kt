package com.mgasd.neonbeatssetlits.data.model

data class PinCode(
    val id: Int,
    val code: String,
    val credits: Int,
    val is_used: Boolean,
    val created_by: Int?,
    val table: Int?,
    val created_at: String,
    val used_at: String?
)

data class ClientPinCodeValidateRequest(
    val code: String
)

data class ClientPinCodeValidateResponse(
    val detail: String,
    val credits_added: Int,
    val new_balance: Int
)
