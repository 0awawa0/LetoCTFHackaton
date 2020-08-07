package ru.sinura.hackaton.repo.retrofit.apis

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST
import ru.sinura.hackaton.repo.retrofit.models.LoginModel
import ru.sinura.hackaton.repo.retrofit.models.RegisterModel

interface UserApi {

    @FormUrlEncoded
    @Headers("Connection: keep-alive")
    @POST("/user/registration")
    fun userRegister(
        @Field("name") firstName: String,
        @Field("surname") lastName: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("num_oms") medCard: String,
        @Field("date_birth") birth: String,
        @Field("phone") phone: String,
        @Field("passport") passport: String,
        @Field("city") city: String,
        @Field("street") street: String
    ): Call<RegisterModel>

    @FormUrlEncoded
    @Headers("Connection: keep-alive")
    @POST("/user/login")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginModel>

}