package com.pnam.note.ui.addnote

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.pnam.note.base.BaseActivity
import com.pnam.note.database.data.models.Note
import com.pnam.note.databinding.ActivityAddNoteBinding
import com.pnam.note.utils.AppUtils.Companion.NOTE_CHANGE
import com.pnam.note.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AddNoteActivity : BaseActivity() {
    private lateinit var binding: ActivityAddNoteBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initObservers()

        binding.btnAdd.setOnClickListener(addListener)
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
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
                    finishActivity(1)
                    supportFinishAfterTransition()
                }
                is Resource.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.internetError.observe(this) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }
}