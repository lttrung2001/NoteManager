package com.pnam.note.base

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class ImageBottomSheetActivity : AppCompatActivity() {
    abstract fun addImagesToNote (images: List<String>)
}