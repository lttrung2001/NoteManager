package com.pnam.note.ui.addnote

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.pnam.note.R
import com.pnam.note.database.data.locals.entities.Note
import com.pnam.note.databinding.ActivityAddNoteBinding
import com.pnam.note.ui.adapters.savedimage.SavedImageAdapter
import com.pnam.note.ui.adapters.savedimage.SavedImageItemClickListener
import com.pnam.note.ui.addimages.AddNoteImagesFragment
import com.pnam.note.ui.base.BaseActivity
import com.pnam.note.ui.imagedetail.ImageDetailActivity
import com.pnam.note.utils.AppConstants.ADD_NOTE_REQUEST
import com.pnam.note.utils.AppConstants.NOTE_CHANGE
import com.pnam.note.utils.AppConstants.READ_EXTERNAL_STORAGE_REQUEST
import com.pnam.note.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AddNoteActivity : BaseActivity() {
    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var imageAdapter: SavedImageAdapter
    private val viewModel: AddNoteViewModel by viewModels()
    private val addListener: View.OnClickListener by lazy {
        View.OnClickListener {
            val title = binding.inputNoteTitle.text.trim().toString()
            val desc = binding.inputNoteDesc.text.trim().toString()
            if (title.isEmpty() && desc.isEmpty() && imageAdapter.currentList.isEmpty()) {
                Toast.makeText(
                    this@AddNoteActivity,
                    "Note must not be empty",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                hideKeyboard(binding.btnAdd.windowToken)
                lifecycleScope.launch(Dispatchers.IO) {
                    val note = Note(
                        System.currentTimeMillis().toString(),
                        title,
                        desc,
                        System.currentTimeMillis(),
                        System.currentTimeMillis(),
                        imageAdapter.currentList
                    )
                    viewModel.addNote(note)
                }
            }
        }
    }

    private val openBottomSheet: View.OnClickListener by lazy {
        View.OnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_EXTERNAL_STORAGE_REQUEST
                )
            } else {
                val bottomSheet = AddNoteImagesFragment()
                bottomSheet.show(supportFragmentManager, AddNoteImagesFragment.TAG)
            }
        }
    }

    private val imageListener: SavedImageItemClickListener by lazy {
        object : SavedImageItemClickListener {
            override fun onClick(path: String) {
                val intent = Intent(this@AddNoteActivity, ImageDetailActivity::class.java)
                val bundle = Bundle()
                val note = Note("", "", "", 0, 0, imageAdapter.currentList)
                bundle.putSerializable("note", note)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.let {
            title = resources.getText(R.string.add_note)
            it.setDisplayHomeAsUpEnabled(true)
        }

        initObservers()
        initRecyclerView()

        binding.btnAdd.setOnClickListener(addListener)
        binding.btnOpenBottomSheet.setOnClickListener(openBottomSheet)
    }

    private fun initRecyclerView() {
        imageAdapter = SavedImageAdapter(imageListener)
        binding.rcvNoteImages.adapter = imageAdapter
    }

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
        viewModel.imagesLiveData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    val currentList = imageAdapter.currentList.toMutableList()
                    currentList.removeAll(resource.data)
                    currentList.addAll(resource.data)
                    imageAdapter.submitList(currentList)
                }
                is Resource.Error -> {
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.error.observe(this) {
            Toast.makeText(this, viewModel.error.value, Toast.LENGTH_SHORT).show()
        }
    }

    override fun addImagesToNote(images: List<String>) {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.addImages(images)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAfterTransition()
    }
}