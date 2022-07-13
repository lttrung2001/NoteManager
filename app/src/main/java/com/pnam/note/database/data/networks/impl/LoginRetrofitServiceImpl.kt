package com.pnam.note.database.data.networks.impl

import android.content.SharedPreferences
import com.auth0.android.jwt.JWT
import com.pnam.note.database.data.models.APIResult
import com.pnam.note.database.data.models.Login
import com.pnam.note.database.data.networks.LoginNetworks
import com.pnam.note.throwable.NotFoundException
import com.pnam.note.utils.AppUtils.Companion.LOGIN_TOKEN
import com.pnam.note.utils.RetrofitUtils.INTERNAL_SERVER_ERROR
import com.pnam.note.utils.RetrofitUtils.NOT_FOUND
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import javax.inject.Inject

class LoginRetrofitServiceImpl @Inject constructor(
    private val service: Service,
    private val sharedPreferences: SharedPreferences
) : LoginNetworks {

    interface Service {
        @POST("/login")
        fun login(@Body map: Map<String, String>)
                : Single<Response<APIResult>>

        @FormUrlEncoded
        @POST("/register")
        fun register(
            @Field("email") email: String, @Field("password") password: String
        ): Single<Response<APIResult>>
    }

    override fun login(email: String, password: String): Single<Login> {
        val body = HashMap<String, String>()
        body["email"] = email
        body["password"] = password
        return service.login(body).map {
            // Đưa lỗi 500 tạm vào đây, tách ra sau
            if (it.code() == NOT_FOUND || it.code() == INTERNAL_SERVER_ERROR) {
                throw NotFoundException()
            } else {
                val loginToken = it.body()!!.data.toString()
                // Save token into local
                sharedPreferences.edit().putString(LOGIN_TOKEN, loginToken).apply()

                // Decode token get login info
                val jwtToken =  JWT(loginToken)
                val login = Login(
                    jwtToken.getClaim("id").asObject(String::class.java)!!,
                    jwtToken.getClaim("email").asObject(String::class.java)!!,
                    jwtToken.getClaim("password").asObject(String::class.java)!!
                )
                login
            }
        }
    }

    override fun register(email: String, password: String): Single<Login> {
        TODO("Not yet implemented")
    }

    override fun forgotPassword(email: String): Single<Unit> {
        TODO("Not yet implemented")
    }

    override fun changePassword(
        email: String,
        oldPassword: String,
        newPassword: String
    ): Single<Login> {
        TODO("Not yet implemented")
    }
}
