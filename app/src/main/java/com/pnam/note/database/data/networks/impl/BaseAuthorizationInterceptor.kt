package com.pnam.note.database.data.networks.impl

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.auth0.android.jwt.JWT
import com.google.gson.Gson
import com.pnam.note.database.data.models.APIResult
import com.pnam.note.throwable.LoginSessionExpiredException
import com.pnam.note.throwable.NotFoundException
import com.pnam.note.throwable.UnknownCodeException
import com.pnam.note.ui.login.LoginActivity
import com.pnam.note.utils.AppConstants.ACCESS_TOKEN
import com.pnam.note.utils.AppConstants.LOGIN_TOKEN
import com.pnam.note.utils.LocalTokenManager
import com.pnam.note.utils.RetrofitUtils.BASE_URL
import com.pnam.note.utils.RetrofitUtils.SUCCESS
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
interface BaseAuthorizationInterceptor : Interceptor {
    class AuthorizationInterceptor @Inject constructor(
        @ApplicationContext private val context: Context,
        private val sharedPreferences: SharedPreferences,
        private val httpLoggingInterceptor: HttpLoggingInterceptor,
        private val localTokenManager: LocalTokenManager
    ) : BaseAuthorizationInterceptor {

        @OptIn(ExperimentalCoroutinesApi::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            if ((request.url.encodedPath.equals("/login", true)
                        || request.url.encodedPath.equals("/register", true)
                        || request.url.encodedPath.equals("/forgot-password", true))
                && request.method.equals("post", true)
            ) {
                return chain.proceed(request)
            }
            val newRequestBuilder: Request.Builder = chain.request().newBuilder()
            val accessToken = sharedPreferences.getString(ACCESS_TOKEN, "") ?: ""
            val token = try {
                if (accessToken.isNotBlank()) {
                    val jwtToken = JWT(accessToken)
                    val exp = jwtToken.expiresAt
                    /* Access token was expired */
                    if ((exp?.time ?: 0) < System.currentTimeMillis()) {
                        fetchAccessToken().also { token ->
                            sharedPreferences.edit().putString(ACCESS_TOKEN, token).apply()
                        }
                    } else {
                        accessToken
                    }
                } else {
                    fetchAccessToken().also { token ->
                        sharedPreferences.edit().putString(ACCESS_TOKEN, token).apply()
                    }
                }
            } catch (e: Throwable) {
                localTokenManager.logout()
                context.startActivity(Intent(context, LoginActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or
                            Intent.FLAG_ACTIVITY_NEW_TASK
                })
                if (e is LoginSessionExpiredException) {
                    throw e
                } else {

                }
            }
            newRequestBuilder.addHeader("Authorization", "Bearer $token")
            return chain.proceed(newRequestBuilder.build())
        }

        private fun fetchAccessToken(): String {
            val loginToken = sharedPreferences.getString(LOGIN_TOKEN, "") ?: ""
            return if (loginToken.isNotBlank()) {
                val jwtToken = JWT(loginToken)
                val exp = jwtToken.expiresAt
                /* Login token has expired */
                if ((exp?.time ?: 0) < System.currentTimeMillis()) {
                    throw LoginSessionExpiredException()
                } else {
                    fetchAccessToken(loginToken)
                }
            } else {
                throw NotFoundException("Login token is required")
            }
        }

        private fun fetchAccessToken(loginToken: String): String {
            val url = "${BASE_URL}get-access-token"
            val request = Request.Builder()
                .addHeader("Authorization", "Bearer $loginToken")
                .url(url)
                .build()
            val client = OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(httpLoggingInterceptor)
                .build()
            val response = client.newCall(request).execute()
            return if (response.code == SUCCESS) {
                val body = Gson().fromJson(response.body?.string(), APIResult::class.java)
                body.data.toString()
            } else {
                throw UnknownCodeException()
            }
        }
    }
}