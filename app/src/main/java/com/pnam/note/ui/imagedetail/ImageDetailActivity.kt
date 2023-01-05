package com.pnam.note.ui.imagedetail

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.URLUtil
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.pnam.note.R
import com.pnam.note.database.data.locals.entities.Note
import com.pnam.note.databinding.ActivityImageDetailBinding
import com.pnam.note.ui.adapters.imagedetail.ImageDetailAdapter
import com.pnam.note.utils.AppConstants
import com.pnam.note.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ImageDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageDetailBinding
    private lateinit var fragmentAdapter: ImageDetailAdapter
    private val viewModel: ImageDetailViewModel by viewModels()

    private val pageChangedCallback: ViewPager2.OnPageChangeCallback by lazy {
        object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val size = fragmentAdapter.itemCount
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
        val note = intent.extras!!.getSerializable("note") as Note
        val imagesPath = ArrayList(note.images!!)

        supportActionBar?.let {
            title = "${position + 1} of ${imagesPath.size} images"
            it.setDisplayHomeAsUpEnabled(true)
        }

        fragmentAdapter = ImageDetailAdapter(
            this,
            (imagesPath)
        )
        binding.imgPager.adapter = fragmentAdapter
        binding.imgPager.currentItem = position
        binding.imgPager.registerOnPageChangeCallback(pageChangedCallback)

        initObservers()
    }

    private fun initObservers() {
        viewModel.deleteImageLiveData.observe(this@ImageDetailActivity) {
            when (it) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    val position = binding.imgPager.currentItem
                    val size = fragmentAdapter.itemCount
                    fragmentAdapter.removeAt(position)
                    supportActionBar?.title = "${position + 1} of ${size - 1} images"
                    if (size == 1) {
                        finish()
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(this@ImageDetailActivity, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
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
                val position = binding.imgPager.currentItem
                val url = fragmentAdapter.getList()[position]
                if (URLUtil.isNetworkUrl(url)) {
                    val note = intent.extras!!.getSerializable("note") as Note
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.deleteImage(note.id, url)
                    }
                } else {
                    val size = fragmentAdapter.itemCount
                    fragmentAdapter.removeAt(position)
                    supportActionBar?.title = "${position + 1} of ${size - 1} images"
                    if (size == 1) {
                        finish()
                    }
                }
                true
            }
            R.id.download_img -> {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        AppConstants.WRITE_EXTERNAL_STORAGE_REQUEST
                    )
                } else {
                    val note = intent.extras!!.getSerializable("note") as Note
                    val imagesPath = note.images
                    imagesPath?.let { arr ->
                        val position = intent.extras!!.getInt(POSITION)
                        val downloadId = viewModel.download(arr[position])
                        val downloadInfo = viewModel.getDownloadStatus(downloadId)
                        viewModel.downloadProgress(downloadId) { bytesDownloaded, bytesTotal ->
                            val title = "Download image from firebase storage"
                            val text = if (bytesDownloaded == bytesTotal) {
                                "Download finished"
                            } else {
                                "Progress: $bytesDownloaded/$bytesTotal bytes"
                            }
                            val icon = if (bytesDownloaded == bytesTotal) {
                                R.drawable.ic_download_done
                            } else {
                                R.drawable.ic_downloading
                            }
                            val builder = NotificationCompat.Builder(
                                this,
                                AppConstants.DOWNLOAD_CHANNEL_ID
                            )
                                .setSmallIcon(icon)
                                .setContentTitle(title)
                                .setContentText(text)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            with(NotificationManagerCompat.from(this)) {
                                // notificationId is a unique int for each notification that you must define
                                notify(AppConstants.DOWNLOAD_NOTIFICATION_ID, builder.build())
                            }
                        }
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
    }
}