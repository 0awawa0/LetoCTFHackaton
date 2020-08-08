package ru.sinura.hackaton.repo

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.sinura.hackaton.login.LoginActivity
import ru.sinura.hackaton.main.ui.news.NewsFragment
import ru.sinura.hackaton.main.ui.news.NewsModel
import ru.sinura.hackaton.main.ui.profile.ProfileFragment
import ru.sinura.hackaton.main.ui.vakcinaciya.VakcinaciyaFragment
import ru.sinura.hackaton.register.RegisterActivity
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

    private val retrofit = Retrofit.Builder().baseUrl("http://192.168.1.244")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    private val userApi: UserApi = retrofit.create(UserApi::class.java)

    var loginResponse: LoginActivity.LoginResponse? = null
    var registerResponse: RegisterActivity.RegisterResponse? = null
    var newsResponse: NewsFragment.NewsResponse? = null
    var recepResponse: VakcinaciyaFragment.RecepResponse? = null
    var profileResponse: ProfileFragment.ProfileResponse? = null

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
                    GlobalScope.launch(Dispatchers.Main) {
                        Log.e("Repo", response.body()?.status)
                        registerResponse?.onSuccess()
                    }
                } else {
                    GlobalScope.launch(Dispatchers.Main) {
                        Log.e("Repo", "Error: " + response.raw().message())
                        registerResponse?.onError()
                    }
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
                    GlobalScope.launch(Dispatchers.Main) {
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

    fun getNews() {
        GlobalScope.launch(Dispatchers.IO) {
            val call = userApi.getNews()

            try {
                val response = call.execute()
                if (response.isSuccessful) {
                    GlobalScope.launch(Dispatchers.Main) {
                        val array = ArrayList<NewsModel>()
                        if (response.body() != null) {
                            val news = response.body()
                            for (i in (news as ru.sinura.hackaton.repo.retrofit.models.NewsModel).data[0].indices) {
                                array.add(ru.sinura.hackaton.main.ui.news.NewsModel(news.data[1][i], news.data[0][i]))
                            }
                        }
                        Log.e("Repo", "Status: ${response.body()?.status}")
                        newsResponse?.onSuccess(array.toTypedArray())
                    }
                } else {
                    GlobalScope.launch(Dispatchers.Main) {
                        newsResponse?.onError()
                    }
                }
            } catch (exception: ProtocolException) {
                getNews()
                return@launch
            }
        }
    }

    fun getRecep(currentTime: Long) {
        GlobalScope.launch(Dispatchers.IO) {
            val call = userApi.getRecep(currentTime)

            try {
                val response = call.execute()
                if (response.isSuccessful) {
                    GlobalScope.launch (Dispatchers.Main) {
                        recepResponse?.onSuccess(response.body()?.receps)
                    }
                } else {
                    GlobalScope.launch(Dispatchers.Main) {
                        recepResponse?.onError()
                    }
                }
            } catch (exception: ProtocolException) {
                getRecep(currentTime)
                return@launch
            }
        }
    }

    fun getUserData(token: String) {
        GlobalScope.launch(Dispatchers.IO) {

            val call = userApi.getUserData(token)

            try {
                val response = call.execute()

                if (response.isSuccessful) {
                    GlobalScope.launch (Dispatchers.Main) {
                        profileResponse?.onSuccess(response.body()!!.user)
                    }
                } else {
                    GlobalScope.launch(Dispatchers.Main) {
                        profileResponse?.onError()
                    }
                }
            } catch (exception: ProtocolException) {

            }
        }

    }
}