package ru.sinura.hackaton.repo

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.sinura.hackaton.login.LoginActivity
import ru.sinura.hackaton.repo.retrofit.apis.UserApi
import java.net.ProtocolException
import java.net.SocketTimeoutException

class Repo private constructor() {


    companion object {
        private var instance: Repo? = null

        fun getInstance(): Repo {
            if (instance == null) {
                instance = Repo()
            }
            return instance!!
        }
    }

    private val retrofit = Retrofit.Builder().baseUrl("http://192.168.88.48")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    private val userApi: UserApi = retrofit.create(UserApi::class.java)

    var loginResponse: LoginActivity.LoginResponse? = null

    fun doRegister(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        medCard: String,
        birth: String,
        phone: String,
        passport: String,
        city: String,
        street: String
    ) {

        GlobalScope.launch(Dispatchers.IO) {
            val call = userApi.userRegister(
                firstName,
                lastName,
                email,
                password,
                medCard,
                birth,
                phone,
                passport,
                city,
                street
            )

            try {
                val response = call.execute()
                if (response.isSuccessful) {
                    Log.e("Repo", response.body()?.status)
                } else {
                    Log.e("Repo", "Error: " + response.raw().message())
                }
            } catch (exception: ProtocolException) {
                Log.e("Repo", "Caught protocol exception")
            } catch (exception: SocketTimeoutException) {
                Log.e("Repo", "Caught socket timeout exception")
            }
        }
    }

    fun loginUser(
        email: String,
        password: String
    ) {
        GlobalScope.launch(Dispatchers.IO) {

            val call = userApi.loginUser(email, password)

            try {
                val response = call.execute()
                if (response.isSuccessful) {
                    GlobalScope.launch(Dispatchers.Main) {
                        Log.e("Repo", "Status: ${response.body()?.status}\nToken: ${response.body()?.token}")
                        loginResponse?.onSuccess(response.body()!!.token)
                    }
                } else {
                    GlobalScope.launch {
                        Log.e("Repo", "Error: " + response.raw().message())
                        loginResponse?.onError()
                    }
                }
            } catch (exception: ProtocolException) {
                Log.e("Repo", "Caught protocol exception")
                loginUser(email, password)
                return@launch
            } catch (exception: SocketTimeoutException) {
                Log.e("Repo", "Caught socket timeout exception")
            }
        }
    }
}