package ru.sinura.hackaton.repo.retrofit.models

import com.squareup.moshi.Json


data class RegisterModel (
    @field:Json("status") val status: String
)