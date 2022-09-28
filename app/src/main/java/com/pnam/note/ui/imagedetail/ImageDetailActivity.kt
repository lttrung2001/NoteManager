package com.pnam.note.ui.imagedetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import com.pnam.note.R
import com.pnam.note.databinding.ActivityImageDetailBinding
import com.pnam.note.ui.adapters.imagedetail.ImageDetailAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageDetailBinding
    private lateinit var fragmentAdapter: ImageDetailAdapter
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val imagePaths = intent.extras!!.getStringArrayList("imagePaths")

        fragmentAdapter = ImageDetailAdapter(this, imagePaths!!.toList())
        binding.imgPager.adapter = fragmentAdapter
        binding.imgPager.currentItem = intent.extras!!.getInt("position")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_image_detail, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAfterTransition()
    }
}