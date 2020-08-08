package ru.sinura.hackaton.repo.retrofit.models

import com.squareup.moshi.Json


data class UserResponseModel(
    @field:Json("status") val status: String,
    @field:Json("user") val user: UserModel
)