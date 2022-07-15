package com.pnam.note.database.data.networks.impl

import android.content.Context
import android.net.ConnectivityManager
import com.pnam.note.throwable.NoConnectivityException
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class NetworkConnectionInterceptor @Inject constructor(@ApplicationContext private val context: Context)
    : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isConnected) {
            throw NoConnectivityException()
        } else {
            val builder: Request.Builder = chain.request().newBuilder()
            return chain.proceed(builder.build())
        }
    }

    private val isConnected: Boolean get() {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
            as ConnectivityManager
        val netInfo = connectivityManager.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }
}