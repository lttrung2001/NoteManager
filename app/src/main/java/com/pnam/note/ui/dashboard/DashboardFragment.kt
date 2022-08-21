package com.pnam.note.ui.dashboard

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
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

@Suppress("DEPRECATION")
@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class DashboardFragment : Fragment() {
    private lateinit var binding: FragmentDashboardBinding
    private var notesAdapter: NoteAdapter? = null
    private val viewModel: DashboardViewModel by viewModels()

    private val addNoteListener: View.OnClickListener by lazy {
        View.OnClickListener {
            val intent = Intent(activity, AddNoteActivity::class.java)
            val options = activity?.let { act ->
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    act, binding.btnStartAddNote, "transition_fab"
                )
            }
            startActivityForResult(intent, 1, options?.toBundle())
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

    private val textWatcher: TextWatcher by lazy {
        object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                return
            }

            override fun afterTextChanged(s: Editable?) {
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.searchNotes(binding.edtSearch.text.trim().toString())
                }
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getNotes()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(layoutInflater)
        binding.btnStartAddNote.setOnClickListener(addNoteListener)
        initRecycleView()
        initObservers()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.edtSearch.addTextChangedListener(textWatcher)
    }

    override fun onPause() {
        super.onPause()
        binding.edtSearch.removeTextChangedListener(textWatcher)
    }

    private fun initRecycleView() {
        notesAdapter = notesAdapter ?: NoteAdapter(mutableListOf(), noteClickListener)
        binding.rcvNotes.adapter = notesAdapter
        binding.rcvNotes.layoutManager = LinearLayoutManager(context)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initObservers() {
        viewModel.dashboard.observe(viewLifecycleOwner) { resource ->
            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                when (resource) {
                    is Resource.Loading -> {
                        binding.loadMore.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        notesAdapter?.let { adapter ->
                            val start = adapter.list.size
                            adapter.list.addAll(resource.data.data)
                            adapter.notifyItemRangeInserted(start, adapter.itemCount)
                        }
                        if (resource.data.hasNextPage) {
                            binding.rcvNotes.addOnScrollListener(onScrollListener)
                        } else {
                            binding.rcvNotes.clearOnScrollListeners()
                        }
                        binding.loadMore.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        binding.loadMore.visibility = View.GONE
                        Toast.makeText(activity, resource.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        viewModel.deleteNote.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.loadMore.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.loadMore.visibility = View.GONE
                }
                is Resource.Error -> {
                    binding.loadMore.visibility = View.GONE
                    Toast.makeText(activity, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.searchNotes.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.loadMore.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    notesAdapter?.let { adapter ->
                        adapter.list.clear()
                        adapter.list.addAll(resource.data)
                        adapter.notifyDataSetChanged()
                    }
                    binding.loadMore.visibility = View.GONE
                }
                is Resource.Error -> {
                    binding.loadMore.visibility = View.GONE
                    Toast.makeText(activity, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.error.observe(viewLifecycleOwner) {
            binding.loadMore.visibility = View.GONE
            Toast.makeText(activity, viewModel.error.value, Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding.edtSearch.removeTextChangedListener(textWatcher)
    }
}
