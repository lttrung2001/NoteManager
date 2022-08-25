package com.pnam.note.ui.addnote

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pnam.note.database.data.models.Note
import com.pnam.note.databinding.ActivityAddNoteBinding
import com.pnam.note.ui.adapters.image.ImageAdapter
import com.pnam.note.utils.AppUtils.Companion.ADD_NOTE_REQUEST
import com.pnam.note.utils.AppUtils.Companion.NOTE_CHANGE
import com.pnam.note.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AddNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var imageAdapter: ImageAdapter
    private val viewModel: AddNoteViewModel by viewModels()
    private val addListener: View.OnClickListener by lazy {
        View.OnClickListener {
            val title = binding.inputNoteTitle.text.trim().toString()
            val desc = binding.inputNoteDesc.text.trim().toString()
            if (title.isEmpty() && desc.isEmpty()) {
                Toast.makeText(
                    this@AddNoteActivity,
                    "Please write somethings to save",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                lifecycleScope.launch(Dispatchers.IO) {
                    val note = Note(
                        System.currentTimeMillis().toString(),
                        title,
                        desc,
                        System.currentTimeMillis(),
                        System.currentTimeMillis()
                    )
                    viewModel.addNote(note)
                }
            }
        }
    }

    private val openBottomSheet: View.OnClickListener by lazy {
        View.OnClickListener {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initObservers()

        binding.btnAdd.setOnClickListener(addListener)
        binding.btnOpenBottomSheet.setOnClickListener(openBottomSheet)
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

//    private fun testImage() {
//        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//
//            // Should we show an explanation?
//            if (shouldShowRequestPermissionRationale(
//                    Manifest.permission.READ_EXTERNAL_STORAGE
//                )
//            ) {
//                // Explain to the user why we need to read the contacts
//            }
//
//            requestPermissions(
//                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                123
//            )
//
//            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
//            // app-defined int constant
//
//        }
//
//        val imageList: ArrayList<String> = ArrayList()
//        val projection =
//            arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
//        val orderBy: String = MediaStore.Video.Media.DATE_TAKEN
//        val imageCursor: Cursor = this.managedQuery(
//            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
//            null, ""
//        )
//        for (i in 0 until imageCursor.count) {
//            imageCursor.moveToPosition(i)
//            val dataColumnIndex =
//                imageCursor.getColumnIndex(MediaStore.Images.Media.DATA)
//            imageList.add(imageCursor.getString(dataColumnIndex))
//        }
//        imageAdapter = ImageAdapter(imageList, addListener)
//        binding.rcvNoteImages.adapter = imageAdapter
//        binding.rcvNoteImages.layoutManager =
//            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//    }

    private fun initObservers() {
        viewModel.addNote.observe(this) {
            when (it) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    val intent = Intent()
                    val bundle = Bundle()
                    bundle.putSerializable(NOTE_CHANGE, it.data)
                    intent.putExtras(bundle)
                    setResult(Activity.RESULT_OK, intent)
                    finishActivity(ADD_NOTE_REQUEST)
                    supportFinishAfterTransition()
                }
                is Resource.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.error.observe(this) {
            Toast.makeText(this, viewModel.error.value, Toast.LENGTH_SHORT).show()
        }
    }
}