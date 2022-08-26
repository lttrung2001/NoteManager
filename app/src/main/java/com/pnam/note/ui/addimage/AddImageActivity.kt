package com.pnam.note.ui.addimage

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.pnam.note.databinding.ActivityAddImageBinding
import com.pnam.note.ui.adapters.image.ImageAdapter
import com.pnam.note.ui.adapters.image.ImageItemClickListener

class AddImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddImageBinding
    private lateinit var imagesAdapter: ImageAdapter

    private val imageListener: ImageItemClickListener by lazy {
        object : ImageItemClickListener {
            override fun onClick(path: String) {
                Toast.makeText(this@AddImageActivity, path, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        testImage()
    }

        private fun testImage() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                123
            )

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant

        }

        val imageList: ArrayList<String> = ArrayList()
        val projection =
            arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        val orderBy: String = MediaStore.Video.Media.DATE_TAKEN
        val imageCursor: Cursor = this.managedQuery(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
            null, ""
        )
        for (i in 0 until imageCursor.count) {
            imageCursor.moveToPosition(i)
            val dataColumnIndex =
                imageCursor.getColumnIndex(MediaStore.Images.Media.DATA)
            imageList.add(imageCursor.getString(dataColumnIndex))
        }
        imagesAdapter = ImageAdapter(imageList, imageListener)
        binding.rcvNoteImages.adapter = imagesAdapter
    }
}