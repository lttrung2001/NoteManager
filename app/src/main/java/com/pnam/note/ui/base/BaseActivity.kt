package com.pnam.note.ui.base

import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.IBinder
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.pnam.note.receiver.NetworkReceiver

open class BaseActivity : AppCompatActivity() {
    private val receiver: NetworkReceiver by lazy {
        NetworkReceiver()
    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(receiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
    }

    fun TextInputLayout.showError(message: String) {
        isErrorEnabled = true
        error = message
    }

    fun hideKeyboard(element: IBinder) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(element, 0)
    }
}