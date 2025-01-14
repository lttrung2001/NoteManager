package com.pnam.note.ui.addimages

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pnam.note.databinding.FragmentNoteBottomSheetDialogBinding
import com.pnam.note.ui.adapters.chooseimage.ChooseImageAdapter
import com.pnam.note.ui.adapters.chooseimage.ChooseImageItemClickListener
import com.pnam.note.ui.base.BaseActivity
import com.pnam.note.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AddNoteImagesFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentNoteBottomSheetDialogBinding
    private val viewModel: AddNoteImagesViewModel by viewModels()
    private var imageAdapter: ChooseImageAdapter? = null
    val imageChoose = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
//            val selectedImageUri: Uri? = result.data!!.data
//            binding.image.setImageURI(selectedImageUri)
//            binding.imageLayout.setDisplayedChild(1)
//            image = selectedImageUri
//            isChange = true
        }
    }

    private val scrollListener: RecyclerView.OnScrollListener by lazy {
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.findImages(requireContext())
                    }
                }
            }
        }
    }

    private val imageListener: ChooseImageItemClickListener by lazy {
        object : ChooseImageItemClickListener {
            override fun onClick(path: String, position: Int) {
                (activity as BaseActivity).addImagesToNote(listOf(path))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.findImages(requireContext())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteBottomSheetDialogBinding.inflate(layoutInflater)
        initObservers()
        initRecyclerView()
        return binding.root
    }

    private fun initObservers() {
        viewModel.imageListLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.rcvNoteImages.removeOnScrollListener(scrollListener)
                }
                is Resource.Success -> {
                    imageAdapter?.let { adapter ->
                        val currentList = adapter.currentList.toMutableList()
                        currentList.removeAll(resource.data.data)
                        currentList.addAll(resource.data.data)
                        adapter.submitList(currentList)
                        if (resource.data.hasNextPage) {
                            binding.rcvNoteImages.addOnScrollListener(scrollListener)
                        } else {
                            binding.rcvNoteImages.removeOnScrollListener(scrollListener)
                        }
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(activity, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { message ->
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun initRecyclerView() {
        imageAdapter = imageAdapter ?: ChooseImageAdapter(imageListener)
        binding.rcvNoteImages.adapter = imageAdapter
    }

    companion object {
        const val TAG = "note_bottom_sheet_fragment"
    }
}