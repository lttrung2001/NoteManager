package com.pnam.note.ui.imagedetail

import android.Manifest
import android.app.DownloadManager
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewpager2.widget.ViewPager2
import com.pnam.note.R
import com.pnam.note.databinding.ActivityImageDetailBinding
import com.pnam.note.ui.adapters.imagedetail.ImageDetailAdapter
import com.pnam.note.utils.AppConstants
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ImageDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageDetailBinding
    private lateinit var fragmentAdapter: ImageDetailAdapter
    private val downloadViewModel: ImageDetailViewModel by viewModels()

    private val pageChangedCallback: ViewPager2.OnPageChangeCallback by lazy {
        object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                intent.extras?.putInt(POSITION, position)
                val size = intent.extras!!.getStringArrayList(IMAGEPATHS)?.size
                supportActionBar?.title = "${position + 1} of $size images"
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val position = intent.extras!!.getInt(POSITION)
        val imagePaths = intent.extras!!.getStringArrayList(IMAGEPATHS)

        supportActionBar?.let {
            title = "${position + 1} of ${imagePaths?.size} images"
            it.setDisplayHomeAsUpEnabled(true)
        }

        fragmentAdapter = ImageDetailAdapter(this, imagePaths!!.toList())
        binding.imgPager.adapter = fragmentAdapter
        binding.imgPager.currentItem = position
        binding.imgPager.registerOnPageChangeCallback(pageChangedCallback)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_image_detail, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAfterTransition()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_img -> {
                true
            }
            R.id.download_img -> {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        AppConstants.READ_EXTERNAL_STORAGE_REQUEST
                    )
                } else {
                    intent.extras?.let { bundle ->
                        val arr = bundle.getStringArrayList(IMAGEPATHS)!!
                        val position = bundle.getInt(POSITION)
//                        val manager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
//                        val uri =
//                            Uri.parse(arr[position])
//                        val request = DownloadManager.Request(uri)
//                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//                        val reference: Long = manager.enqueue(request)
                        downloadViewModel.download(arr[position])
                    }
                }
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.imgPager.unregisterOnPageChangeCallback(pageChangedCallback)
    }

    companion object {
        private const val POSITION = "position"
        private const val IMAGEPATHS = "imagePaths"
    }
}