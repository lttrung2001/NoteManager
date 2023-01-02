package com.pnam.note.ui.base

import android.content.Context
import android.os.IBinder
import android.view.MenuItem
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    abstract fun addImagesToNote(images: List<String>)

    fun hideKeyboard(element: IBinder) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(element, 0)
    }
}