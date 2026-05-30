package com.mgasd.neonbeatssetlits.data.model

import com.google.gson.annotations.SerializedName

enum class RoleEnum {
    @SerializedName("ADMIN") ADMIN,
    @SerializedName("WAITER") WAITER
}

data class User(
    val id: Int,
    val username: String,
    val email: String?,
    val first_name: String?,
    val last_name: String?,
    val role: RoleEnum,
    val tenant: Int?
)
