package com.pnam.note.ui.dashboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pnam.note.R
import com.pnam.note.database.data.models.Note
import com.pnam.note.databinding.FragmentDashboardBinding
import com.pnam.note.ui.adapters.NoteAdapter
import com.pnam.note.ui.adapters.NoteItemClickListener
import com.pnam.note.ui.addnote.AddNoteActivity
import com.pnam.note.ui.editnote.EditNoteActivity
import com.pnam.note.utils.AppUtils
import com.pnam.note.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class DashboardFragment : Fragment() {
    private lateinit var binding: FragmentDashboardBinding
    private var notesAdapter: NoteAdapter? = null
    private val viewModel: DashboardViewModel by viewModels()

    private val addNoteListener: View.OnClickListener by lazy {
        View.OnClickListener {
            val intent = Intent(activity, AddNoteActivity::class.java)
            startActivityForResult(intent, 1)
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
                    activity,
                    EditNoteActivity::class.java
                )
                val bundle = Bundle()
                bundle.putSerializable("note", note)
                bundle.putInt(AppUtils.NOTE_POSITION, position)
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
                    activity!!,
                    pairTitle,
                    pairDesc
                )
                startActivityForResult(intent, 2, options.toBundle())
            }

            override fun onDeleteClick(note: Note, position: Int) {
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.deleteNote(note)
                }
                notesAdapter?.removeAt(position)
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(layoutInflater)
        binding.btnTryAgain.setOnClickListener(tryAgainListener)
        binding.btnStartAddNote.setOnClickListener(addNoteListener)
        binding.edtSearch.addTextChangedListener {
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.searchNotes(binding.edtSearch.text.toString())
            }
        }
        initRecycleView()
        initObservers()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if ((notesAdapter?.itemCount ?: 0) == 0) {
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.getNotes()
            }
        }
    }

    private fun initRecycleView() {
        if (notesAdapter == null) {
            val notes = mutableListOf<Note>()
            notesAdapter = NoteAdapter(notes, noteClickListener)
        }
        binding.rcvNotes.adapter = notesAdapter
        binding.rcvNotes.layoutManager = LinearLayoutManager(context)
        binding.rcvNotes.addOnScrollListener(onScrollListener)
    }

    private fun initObservers() {
        viewModel.dashboard.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.loadMore.visibility = View.VISIBLE
                    binding.btnTryAgain.visibility = View.INVISIBLE
                }
                is Resource.Success -> {
                    notesAdapter?.let { adapter ->
                        val start = adapter.list.size
                        adapter.list.addAll(it.data.data)
                        adapter.notifyItemRangeInserted(start, adapter.itemCount)
                    }
                    binding.loadMore.visibility = View.INVISIBLE
                    binding.btnTryAgain.visibility = View.INVISIBLE
                }
                is Resource.Error -> {
                    binding.loadMore.visibility = View.INVISIBLE
                    binding.btnTryAgain.visibility = View.VISIBLE
                }
            }
        }

        viewModel.deleteNote.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.loadMore.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.loadMore.visibility = View.GONE
                }
                is Resource.Error -> {

                }
            }
        }

        viewModel.searchNotes.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.loadMore.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    notesAdapter!!.list.clear()
                    notesAdapter!!.list.addAll(it.data)
                    notesAdapter!!.notifyDataSetChanged()
                    binding.loadMore.visibility = View.INVISIBLE
                }
                is Resource.Error -> {

                }
            }
        }

        viewModel.internetError.observe(viewLifecycleOwner) {
            binding.loadMore.visibility = View.INVISIBLE
            binding.btnTryAgain.visibility = View.VISIBLE
            Toast.makeText(activity, "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            data!!.extras.let {
                val note = it!!.getSerializable(AppUtils.NOTE_CHANGE) as Note
                if (requestCode == 1) {
                    notesAdapter!!.insertAt(note, 0)
                } else {
                    val position = it.getInt(AppUtils.NOTE_POSITION)
                    notesAdapter!!.editAt(note, position)
                }
            }
        }
    }
}
