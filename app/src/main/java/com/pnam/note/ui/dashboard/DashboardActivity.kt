package com.pnam.note.ui.dashboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.get
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pnam.note.R
import com.pnam.note.database.data.models.Note
import com.pnam.note.databinding.ActivityScrollingBinding
import com.pnam.note.ui.adapters.NoteAdapter
import com.pnam.note.ui.adapters.NoteItemClickListener
import com.pnam.note.ui.addnote.AddNoteActivity
import com.pnam.note.ui.editnote.EditNoteActivity
import com.pnam.note.ui.login.LoginActivity
import com.pnam.note.utils.AppUtils.Companion.ACCESS_TOKEN
import com.pnam.note.utils.AppUtils.Companion.APP_NAME
import com.pnam.note.utils.AppUtils.Companion.LOGIN_TOKEN
import com.pnam.note.utils.AppUtils.Companion.NOTE_CHANGE
import com.pnam.note.utils.AppUtils.Companion.NOTE_POSITION
import com.pnam.note.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlin.math.log

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScrollingBinding
    private lateinit var notesAdapter: NoteAdapter
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val viewModel: DashboardViewModel by viewModels()
    private val addNoteListener: View.OnClickListener by lazy {
        View.OnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }
    private val logoutListener: View.OnClickListener by lazy {
        View.OnClickListener {
            logout()
        }
    }
    private val onScrollListener: RecyclerView.OnScrollListener by lazy {
        object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1) && dy > 0) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.getNotes()
                    }
                }
            }
        }
    }
    private val noteClickListener: NoteItemClickListener by lazy {
        object : NoteItemClickListener {
            override fun onClick(note: Note, view: View, position: Int) {
                val intent = Intent(
                    this@DashboardActivity,
                    EditNoteActivity::class.java
                )
                val bundle = Bundle()
                bundle.putSerializable("note", note)
                bundle.putInt(NOTE_POSITION, position)
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
                startActivityForResult(intent, 2, options.toBundle())
            }

            override fun onDeleteClick(note: Note, position: Int) {
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.deleteNote(note)
                }
                notesAdapter.removeAt(position)
            }
        }
    }

    private val tryAgainListener: View.OnClickListener by lazy {
        View.OnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.getNotes()
            }
        }
    }

    private val menuClickListener: View.OnClickListener by lazy {
        View.OnClickListener {
            binding.drawerLayout.openDrawer(binding.navView, true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecycleView()
        initObservers()

        setSupportActionBar(findViewById(R.id.toolbar))

        initNavigationView()
        initFragmentController()

        binding.toolbarLayout.title = title
        binding.btnLogout.setOnClickListener(logoutListener)
        binding.btnTryAgain.setOnClickListener(tryAgainListener)
        binding.btnStartAddNote.setOnClickListener(addNoteListener)
        binding.btnMenu.setOnClickListener(menuClickListener)
        binding.edtSearch.addTextChangedListener {
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.searchNotes(binding.edtSearch.text.toString())
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getNotes()
        }
    }

    private fun initFragmentController() {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_change_password
            ), binding.drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }

    private fun initNavigationView() {
        val sp = applicationContext.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
        val headerView = binding.navView.getHeaderView(0)
        val txtEmail = headerView
            .findViewById<TextView>(R.id.navigation_header_email)
        txtEmail.text = sp.getString(LoginActivity.EMAIL, "")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            data!!.extras.let {
                val note = it!!.getSerializable(NOTE_CHANGE) as Note
                if (requestCode == 1) {
                    notesAdapter.insertAt(note, 0)
                } else {
                    val position = it.getInt(NOTE_POSITION)
                    notesAdapter.editAt(note, position)
                }
            }
        }
    }

    private fun logout() {
        lifecycleScope.launch(Dispatchers.IO) {
            val sp = applicationContext.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
            sp.edit().remove(ACCESS_TOKEN).apply()
            sp.edit().remove(LOGIN_TOKEN).apply()
            sp.edit().remove(LoginActivity.EMAIL).apply()
            viewModel.noteDao.deleteAllNote()
        }
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    private fun initRecycleView() {
        val notes = mutableListOf<Note>()
        notesAdapter = NoteAdapter(notes, noteClickListener)
        binding.rcvNotes.adapter = notesAdapter
        binding.rcvNotes.layoutManager = LinearLayoutManager(this)
        binding.rcvNotes.addOnScrollListener(onScrollListener)
    }

    private fun initObservers() {
        viewModel.dashboard.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    binding.loadMore.visibility = View.VISIBLE
                    binding.btnTryAgain.visibility = View.INVISIBLE
                }
                is Resource.Success -> {
                    val start = notesAdapter.list.size
                    notesAdapter.list.addAll(it.data.data)
                    notesAdapter.notifyItemRangeInserted(start, notesAdapter.itemCount)
                    binding.loadMore.visibility = View.INVISIBLE
                    binding.btnTryAgain.visibility = View.INVISIBLE
                }
                is Resource.Error -> {
                    binding.loadMore.visibility = View.INVISIBLE
                    binding.btnTryAgain.visibility = View.VISIBLE
                }
            }
        }

        viewModel.deleteNote.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    binding.loadMore.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.loadMore.visibility = View.GONE
                }
                is Resource.Error -> {
                    binding.loadMore.visibility = View.GONE
                    Toast.makeText(
                        this@DashboardActivity,
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        viewModel.searchNotes.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    binding.loadMore.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    notesAdapter.list.clear()
                    notesAdapter.list.addAll(it.data)
                    notesAdapter.notifyDataSetChanged()
                    binding.loadMore.visibility = View.INVISIBLE
                }
                is Resource.Error -> {
                    binding.loadMore.visibility = View.INVISIBLE
                    Toast.makeText(
                        this@DashboardActivity,
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        viewModel.internetError.observe(this) {
            binding.loadMore.visibility = View.INVISIBLE
            binding.btnTryAgain.visibility = View.VISIBLE
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}