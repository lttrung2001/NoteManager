package com.pnam.note.ui.editnote

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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.pnam.note.base.ImageBottomSheetActivity
import com.pnam.note.database.data.models.Note
import com.pnam.note.databinding.ActivityEditNoteBinding
import com.pnam.note.ui.adapters.image.ImageAdapter
import com.pnam.note.ui.adapters.image.ImageItemClickListener
import com.pnam.note.ui.addnoteimages.AddNoteImagesFragment
import com.pnam.note.utils.AppUtils
import com.pnam.note.utils.AppUtils.Companion.EDIT_NOTE_REQUEST
import com.pnam.note.utils.AppUtils.Companion.NOTE_CHANGE
import com.pnam.note.utils.AppUtils.Companion.NOTE_POSITION
import com.pnam.note.utils.Resource
import com.pnam.note.utils.RetrofitUtils.BASE_URL
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.json.JSONObject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class EditNoteActivity : ImageBottomSheetActivity() {
    private lateinit var binding: ActivityEditNoteBinding
    private lateinit var imageAdapter: ImageAdapter
    private val viewModel: EditNoteViewModel by viewModels()
    private val editListener: View.OnClickListener by lazy {
        View.OnClickListener {
            val title = binding.inputNoteTitle.text.trim().toString()
            val desc = binding.inputNoteDesc.text.trim().toString()
            if (title.isEmpty() && desc.isEmpty()) {
                Toast.makeText(
                    this@EditNoteActivity,
                    "Please write somethings to save",
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
                        System.currentTimeMillis()
                    )
                    viewModel.editNote(note)
                }
            }
        }
    }

    private val openBottomSheet: View.OnClickListener by lazy {
        View.OnClickListener {
            if(ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    AppUtils.READ_EXTERNAL_STORAGE_REQUEST
                );
            }
            else {
                val bottomSheet = AddNoteImagesFragment()
                bottomSheet.show(supportFragmentManager, AddNoteImagesFragment.TAG)
            }
        }
    }

    private val imageListener: ImageItemClickListener by lazy {
        object: ImageItemClickListener {
            override fun onClick(path: String) {
                TODO("Not yet implemented")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initRecyclerView()
        initObservers()

        binding.btnEdit.setOnClickListener(editListener)
        binding.btnOpenBottomSheet.setOnClickListener(openBottomSheet)
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initView() {
        val note = intent.extras?.getSerializable("note")
        note?.let {
            note as Note
            binding.inputNoteTitle.setText(note.title)
            binding.inputNoteDesc.setText(note.description)
        }
    }

    private fun initRecyclerView() {
        imageAdapter = ImageAdapter(imageListener)
        binding.rcvNoteImages.adapter = imageAdapter
    }

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

    private fun hideKeyboard(element: IBinder) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(element, 0)
    }

    private val emitterListener: Emitter.Listener by lazy {
        Emitter.Listener {
            Toast.makeText(this, it.size, Toast.LENGTH_SHORT).show()
            runOnUiThread {
                val data: JSONObject = it[0] as JSONObject
                Toast.makeText(this, data.optString("data"), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun addImagesToNote (images: List<String>) {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.addImages(images)
        }
    }
}