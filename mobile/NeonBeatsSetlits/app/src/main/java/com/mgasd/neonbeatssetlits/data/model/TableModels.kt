package com.mgasd.neonbeatssetlits.data.model

data class Table(
    val id: Int,
    val name: String,
    val qr_code_token: String?,
    val is_active: Boolean,
    val created_at: String
)
