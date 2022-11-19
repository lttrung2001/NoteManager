package com.pnam.note.ui.addnote

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.pnam.note.R
import com.pnam.note.ui.base.ImageBottomSheetActivity
import com.pnam.note.database.data.models.Note
import com.pnam.note.databinding.ActivityAddNoteBinding
import com.pnam.note.ui.adapters.savedimage.SavedImageAdapter
import com.pnam.note.ui.adapters.savedimage.SavedImageItemClickListener
import com.pnam.note.ui.addnoteimages.AddNoteImagesFragment
import com.pnam.note.ui.imagedetail.ImageDetailActivity
import com.pnam.note.utils.AppConstants.ADD_NOTE_REQUEST
import com.pnam.note.utils.AppConstants.NOTE_CHANGE
import com.pnam.note.utils.AppConstants.READ_EXTERNAL_STORAGE_REQUEST
import com.pnam.note.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.util.ArrayList

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AddNoteActivity : ImageBottomSheetActivity() {
    private lateinit var binding: ActivityAddNoteBinding
    private var imageAdapter: SavedImageAdapter? = null
    private val viewModel: AddNoteViewModel by viewModels()
    private val addListener: View.OnClickListener by lazy {
        View.OnClickListener {
            val title = binding.inputNoteTitle.text.trim().toString()
            val desc = binding.inputNoteDesc.text.trim().toString()
            if (title.isEmpty() && desc.isEmpty() && (imageAdapter?.currentList?.size ?: 0) == 0) {
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
                        imageAdapter?.currentList ?: listOf()
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
                bundle.putStringArrayList(
                    "imagePaths",
                    imageAdapter!!.currentList.toList() as ArrayList<String>
                )
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
        initRecyclerView()
        initObservers()

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
                    imageAdapter?.let { adapter ->
                        val currentList = adapter.currentList.toMutableList()
                        currentList.removeAll(resource.data)
                        currentList.addAll(resource.data)
                        adapter.submitList(currentList)
                    }
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

    private fun hideKeyboard(element: IBinder) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(element, 0)
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