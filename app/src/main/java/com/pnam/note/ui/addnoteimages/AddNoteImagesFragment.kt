package com.pnam.note.ui.addnoteimages

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
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
import com.pnam.note.base.ImageBottomSheetActivity
import com.pnam.note.databinding.FragmentNoteBottomSheetBinding
import com.pnam.note.ui.adapters.image.ImageAdapter
import com.pnam.note.ui.adapters.image.ImageItemClickListener
import com.pnam.note.ui.addnote.AddNoteActivity
import com.pnam.note.utils.AppUtils
import com.pnam.note.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AddNoteImagesFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentNoteBottomSheetBinding
    private val viewModel: AddNoteImagesViewModel by viewModels()
    private var imageAdapter: ImageAdapter? = null
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
                        viewModel.getLocalImages()
                    }
                }
            }
        }
    }

    private val openCameraListener: View.OnClickListener by lazy {
        View.OnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, AppUtils.CAMERA_PIC_REQUEST)
        }
    }

    private val imageListener: ImageItemClickListener by lazy {
        object : ImageItemClickListener {
            override fun onClick(path: String) {
                (activity as ImageBottomSheetActivity).addImagesToNote(listOf(path))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getLocalImages()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteBottomSheetBinding.inflate(layoutInflater)
        binding.bsTakeFromCamera.setOnClickListener(openCameraListener)
        initRecyclerView()
        initObservers()
        return binding.root
    }

    private fun initObservers() {
        viewModel.imageListLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {

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

        viewModel.imageLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    Toast.makeText(activity, resource.data, Toast.LENGTH_SHORT).show()
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
        imageAdapter = imageAdapter ?: ImageAdapter(imageListener)
        binding.rcvNoteImages.adapter = imageAdapter
    }

    companion object {
        const val TAG = "note_bottom_sheet_fragment"
    }
}