package com.pnam.note.ui.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.pnam.note.R
import com.pnam.note.database.data.models.Note
import com.pnam.note.databinding.ActivityScrollingBinding
import com.pnam.note.ui.addnote.AddNoteActivity
import com.pnam.note.ui.editnote.EditNoteActivity
import com.pnam.note.ui.login.LoginActivity
import com.pnam.note.utils.AppUtils.Companion.ACCESS_TOKEN
import com.pnam.note.utils.AppUtils.Companion.APP_NAME
import com.pnam.note.utils.AppUtils.Companion.LOGIN_TOKEN
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
    private val listener = object : NoteItemClickListener {
        override fun onClick(note: Note, view: View) {
            val intent = Intent(this@DashboardActivity, EditNoteActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("note", note)
            intent.putExtras(bundle)
            val pairTitle: Pair<View, String> = Pair(
                view.findViewById(R.id.note_title),
                "noteTitle"
            )
            val pairDesc: Pair<View, String> = Pair(
                view.findViewById(R.id.note_desc),
                "noteDescription"
            )
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this@DashboardActivity,
                pairTitle,
                pairDesc
            )
            startActivity(intent, options.toBundle())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecycleView()
        initObservers()

        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = title
        binding.btnLogout.setOnClickListener { logout() }
        binding.btnStartAddNote.setOnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))
        }
        Thread {
            dashboardViewModel.getNotes(0, 0)
        }.start()
    }

    private fun logout() {
        val sp = applicationContext.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
        sp.edit().remove(ACCESS_TOKEN).apply()
        sp.edit().remove(LOGIN_TOKEN).apply()
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    private fun initRecycleView() {
        rcvNotes = findViewById(R.id.rcv_notes)
        rcvNotes.layoutManager = StaggeredGridLayoutManager(
            2,
            StaggeredGridLayoutManager.VERTICAL
        )

    }

    private fun initObservers() {
        dashboardViewModel.dashboard.observe(this) {
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    notesAdapter = NoteAdapter(it.data.data, listener)
                    rcvNotes.adapter = notesAdapter
                }
                is Resource.Error -> {
                    notesAdapter = NoteAdapter(arrayListOf(), listener)
                    rcvNotes.adapter = notesAdapter
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dashboardViewModel.internetError.observe(this) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("test", "Resume")
    }
}