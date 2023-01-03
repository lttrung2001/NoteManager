package com.pnam.note.ui.editnote

import android.Manifest
import android.annotation.SuppressLint
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
import com.pnam.note.databinding.ActivityEditNoteBinding
import com.pnam.note.ui.adapters.savedimage.SavedImageAdapter
import com.pnam.note.ui.adapters.savedimage.SavedImageItemClickListener
import com.pnam.note.ui.addimages.AddNoteImagesFragment
import com.pnam.note.ui.base.BaseActivity
import com.pnam.note.ui.imagedetail.ImageDetailActivity
import com.pnam.note.utils.AppConstants.EDIT_NOTE_REQUEST
import com.pnam.note.utils.AppConstants.NOTE_CHANGE
import com.pnam.note.utils.AppConstants.NOTE_ID
import com.pnam.note.utils.AppConstants.NOTE_POSITION
import com.pnam.note.utils.AppConstants.READ_EXTERNAL_STORAGE_REQUEST
import com.pnam.note.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class EditNoteActivity : BaseActivity() {
    private lateinit var binding: ActivityEditNoteBinding
    private lateinit var imageAdapter: SavedImageAdapter
    private val viewModel: EditNoteViewModel by viewModels()
    private val editListener: View.OnClickListener by lazy {
        View.OnClickListener {
            val title = binding.inputNoteTitle.text.trim().toString()
            val desc = binding.inputNoteDesc.text.trim().toString()
            if (title.isEmpty() && desc.isEmpty() || imageAdapter.currentList.isEmpty()) {
                Toast.makeText(
                    this@EditNoteActivity,
                    "Note must not be empty",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                hideKeyboard(binding.btnEdit.windowToken)
                lifecycleScope.launch(Dispatchers.IO) {
                    val data = intent.extras?.getSerializable("note") as Note
                    val note = Note(
                        data.id,
                        title,
                        desc,
                        data.createAt,
                        System.currentTimeMillis(),
                        imageAdapter.currentList
                    )
                    viewModel.editNote(note)
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
                val intent = Intent(this@EditNoteActivity, ImageDetailActivity::class.java)
                val bundle = Bundle()

                bundle.putString(NOTE_ID, binding.tvNoteId.text as String?)
                bundle.putStringArrayList(
                    "imagesPath",
                    arrayListOf<String>().apply {
                        this.addAll(imageAdapter.currentList)
                    }
                )
                bundle.putInt("position", imageAdapter.currentList.indexOf(path))
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
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.let {
            title = resources.getText(R.string.edit_note)
            it.setDisplayHomeAsUpEnabled(true)
        }

        initObservers()
        initView()

        binding.btnEdit.setOnClickListener(editListener)
        binding.btnOpenBottomSheet.setOnClickListener(openBottomSheet)
    }

    @SuppressLint("SimpleDateFormat")
    private fun initView() {
        imageAdapter = SavedImageAdapter(imageListener)
        binding.rcvNoteImages.adapter = imageAdapter

        val note = intent.extras?.getSerializable("note")
        note?.let {
            note as Note
            with(binding) {
                tvNoteId.text = note.id
                inputNoteTitle.setText(note.title)
                inputNoteDesc.setText(note.description)
                editAt.text = SimpleDateFormat("dd/MM/yyyy HH:mm").format(Date(note.editAt))
                imageAdapter.submitList(note.images)
            }
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.getNoteDetail(note.id)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun initObservers() {
        viewModel.editNote.observe(this) {
            when (it) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    val newIntent = Intent()
                    val bundle = Bundle()
                    bundle.putSerializable(NOTE_CHANGE, it.data)
                    bundle.putInt(
                        NOTE_POSITION,
                        intent.extras!!.getInt(NOTE_POSITION)
                    )
                    newIntent.putExtras(bundle)
                    setResult(Activity.RESULT_OK, newIntent)
                    finishActivity(EDIT_NOTE_REQUEST)
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
                    imageAdapter.let { adapter ->
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

        viewModel.getNoteDetailLiveData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    binding.inputNoteTitle.setText(resource.data.title)
                    binding.inputNoteDesc.setText(resource.data.description)
                    binding.editAt.text =
                        SimpleDateFormat("dd/MM/yyyy HH:mm").format(Date(resource.data.editAt))
                    imageAdapter.submitList(resource.data.images)
                    resource.data.images?.let {
                        if (it.isNotEmpty()) {
                            binding.btnViewAllImages.visibility = View.VISIBLE
                        }
                    }
                }
                is Resource.Error -> {

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