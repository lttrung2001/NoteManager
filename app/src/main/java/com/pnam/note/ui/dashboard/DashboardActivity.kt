package com.pnam.note.ui.dashboard

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.pnam.note.R
import com.pnam.note.database.data.models.Note
import com.pnam.note.databinding.ActivityScrollingBinding
import com.pnam.note.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScrollingBinding
    private lateinit var rcvNotes: RecyclerView
    private lateinit var notesAdapter: NoteAdapter
    private val dashboardViewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        rcvNotes = findViewById(R.id.rcv_notes)
        rcvNotes.layoutManager = StaggeredGridLayoutManager(
            2,
            StaggeredGridLayoutManager.VERTICAL
        )

        dashboardViewModel.dashboard.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
                }
                is Resource.Success -> {
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                    notesAdapter = NoteAdapter(it.data.data)
                    rcvNotes.adapter = notesAdapter
                }
                is Resource.Error -> {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dashboardViewModel.internetError.observe(this) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
        }

        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = title

        Thread {
            dashboardViewModel.getNotes(0, 0)
        }.start()
    }
}