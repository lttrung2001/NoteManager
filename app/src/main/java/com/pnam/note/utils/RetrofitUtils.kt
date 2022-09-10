package com.pnam.note.utils

object RetrofitUtils {
    private const val PORT: Int = 3000
    private const val STATIC_IP: String = "192.168.39.117"
    private const val IP_URL: String = "$STATIC_IP:$PORT"
//    const val BASE_URL: String = "http://$IP_URL/"
//    const val BASE_URL: String = "https://e7d2-115-76-49-73.ngrok.io/"
    const val BASE_URL: String = "https://note-manager-trung.herokuapp.com/"
    const val SUCCESS: Int = 200
}