package com.pnam.note.ui.base

import androidx.appcompat.app.AppCompatActivity

abstract class ImageBottomSheetActivity : AppCompatActivity() {
    abstract fun addImagesToNote (images: List<String>)
}