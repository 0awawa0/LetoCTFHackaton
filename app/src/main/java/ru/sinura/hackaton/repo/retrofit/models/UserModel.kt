package ru.sinura.hackaton.repo.retrofit.models

import com.squareup.moshi.Json

data class UserModel (
    @field:Json("name") val name: String,
    @field:Json("surname") val surname: String,
    @field:Json("num_oms") val omsNumber: String,
    @field:Json("passport") val passport: String,
    @field:Json("city") val city: String,
    @field:Json("street") val street: String,
    @field:Json("email") val email: String,
    @field:Json("phone") val phone: String,
    @field:Json("date_birth") val birthDate: String
)