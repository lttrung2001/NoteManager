package com.pnam.note

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.pnam.note.utils.AppUtils.Companion.APP_NAME
import com.pnam.note.utils.AppUtils.Companion.LOGIN_TOKEN

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.tv_main).text = getSharedPreferences(
            APP_NAME,
            Context.MODE_PRIVATE
        ).getString(LOGIN_TOKEN, "").toString()
    }
}