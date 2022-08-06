package com.pnam.note.ui.editnote

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pnam.note.database.data.models.Note
import com.pnam.note.databinding.ActivityEditNoteBinding
import com.pnam.note.utils.AppUtils
import com.pnam.note.utils.AppUtils.Companion.NOTE_CHANGE
import com.pnam.note.utils.AppUtils.Companion.NOTE_POSITION
import com.pnam.note.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class EditNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditNoteBinding
    private val viewModel: EditNoteViewModel by viewModels()
    private val editListener: View.OnClickListener by lazy {
        View.OnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val data = intent.extras?.getSerializable("note") as Note
                val note = Note(
                    data.id,
                    binding.inputNoteTitle.text.toString().trim(),
                    binding.inputNoteDesc.text.toString().trim(),
                    data.createAt,
                    data.editAt
                )
                viewModel.editNote(note)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initObservers()

        binding.btnEdit.setOnClickListener(editListener)
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

    private fun initObservers() {
        viewModel.editNote.observe(this) {
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    lifecycleScope.launch(Dispatchers.Main) {
                        val newIntent = Intent()
                        val bundle = Bundle()
                        bundle.putSerializable(NOTE_CHANGE, it.data)
                        bundle.putInt(
                            NOTE_POSITION,
                            intent.extras!!.getInt(NOTE_POSITION)
                        )
                        newIntent.putExtras(bundle)
                        setResult(Activity.RESULT_OK, newIntent)
                        finishActivity(2)
                        supportFinishAfterTransition()
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(this, "Error when saving note", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.internetError.observe(this) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }
}