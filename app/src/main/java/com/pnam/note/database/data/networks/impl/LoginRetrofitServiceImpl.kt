package com.pnam.note.database.data.networks.impl

import android.util.Log
import com.pnam.note.database.data.models.APIResult
import com.pnam.note.database.data.models.Login
import com.pnam.note.database.data.networks.LoginNetworks
import com.pnam.note.throwable.NotFoundException
import com.pnam.note.utils.RetrofitUtils.NOT_FOUND
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import javax.inject.Inject

class LoginRetrofitServiceImpl @Inject constructor(
    private val service: Service
) : LoginNetworks {

    interface Service {
        @POST("/login")
        fun login(@Body map: Map<String, String>)
        :Single<Response<APIResult>>

        @FormUrlEncoded
        @POST("/register")
        fun register(@Field("email") email: String, @Field("password") password: String
        ):Single<Response<APIResult>>
    }

    override fun login(email: String, password: String): Single<Login> {
        val body = HashMap<String, String>()
        body["email"] = email
        body["password"] = password
        return service.login(body).map {
            if (it.code() == NOT_FOUND) {
                throw NotFoundException()
            } else {
                // Save token into local
                Log.d("test", it.body().toString())
                it.body()!!.data as Login
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
