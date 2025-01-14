package com.pnam.note.utils

object RetrofitUtils {
    private const val PORT: Int = 3000
    private const val STATIC_IP: String = "192.168.39.118"
    private const val IP_URL: String = "$STATIC_IP:$PORT"
    const val BASE_URL: String = "http://$IP_URL/"
    const val SUCCESS: Int = 200
}