package com.pnam.note.database.data.networks.impl

import android.content.SharedPreferences
import com.auth0.android.jwt.JWT
import com.pnam.note.database.data.models.APIResult
import com.pnam.note.database.data.models.Login
import com.pnam.note.database.data.networks.LoginNetworks
import com.pnam.note.throwable.NotFoundException
import com.pnam.note.utils.AppConstants.LOGIN_TOKEN
import com.pnam.note.utils.RetrofitUtils.SUCCESS
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import javax.inject.Inject

class LoginRetrofitServiceImpl @Inject constructor(
    private val service: Service,
    private val sharedPreferences: SharedPreferences
) : LoginNetworks {

    interface Service {
        @POST("/login")
        fun login(@Body map: Map<String, String>)
                : Single<Response<APIResult<String>>>

        @POST("/register")
        fun register(
            @Body map: Map<String, String>
        ): Single<Response<APIResult<Login>>>

        @POST("/change-password")
        fun changePassword(
            @Body map: Map<String, String>
        ): Single<Response<APIResult<Login>>>

        @POST("/forgot-password")
        fun forgotPassword(@Body email: String): Single<APIResult<Any>>
    }

    override fun login(email: String, password: String): Single<Login> {
        val body = HashMap<String, String>()
        body["email"] = email
        body["password"] = password
        return service.login(body).map {
            if (it.code() == SUCCESS) {
                val loginToken = it.body()!!.data
                // Save token into local
                sharedPreferences.edit().putString(LOGIN_TOKEN, loginToken).apply()
                // Decode token get login info
                val jwtToken = JWT(loginToken)
                val login = Login(
                    jwtToken.getClaim("id").asObject(String::class.java)!!,
                    jwtToken.getClaim("email").asObject(String::class.java)!!,
                    jwtToken.getClaim("password").asObject(String::class.java)!!
                )
                // Save login info to locals
                login
            } else {
                throw NotFoundException("UNAUTHORIZED")
            }
        }
    }

    override fun register(email: String, password: String): Single<Login> {
        val body = HashMap<String, String>()
        body["email"] = email
        body["password"] = password
        return service.register(body).map {
            if (it.code() == SUCCESS) {
                it.body()!!.data
            } else {
                throw Exception(it.message())
            }
        }
    }

    override fun forgotPassword(email: String): Single<Unit> {
        return service.forgotPassword(email).map {
            if (it.code == SUCCESS) {

            } else {
                throw Exception(it.message)
            }
        }
    }

    override fun changePassword(
        email: String,
        oldPassword: String,
        newPassword: String
    ): Single<Login> {
        val body = HashMap<String, String>()
        body["email"] = email
        body["oldPassword"] = oldPassword
        body["newPassword"] = newPassword
        return service.changePassword(body).map {
            if (it.code() == SUCCESS) {
                it.body()!!.data
            } else {
                throw Exception(it.message())
            }
        }
    }
}
