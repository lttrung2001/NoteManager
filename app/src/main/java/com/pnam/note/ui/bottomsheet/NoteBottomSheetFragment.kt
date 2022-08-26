package com.pnam.note.ui.bottomsheet

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pnam.note.databinding.FragmentNoteBottomSheetBinding
import com.pnam.note.ui.addimage.AddImageActivity
import com.pnam.note.utils.AppUtils
import com.pnam.note.utils.AppUtils.Companion.CHOOSE_IMAGE_REQUEST
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentNoteBottomSheetBinding
    private val openCameraListener: View.OnClickListener by lazy {
        View.OnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, AppUtils.CAMERA_PIC_REQUEST)
        }
    }

    private val chooseImageListener: View.OnClickListener by lazy {
        View.OnClickListener {
            val intent = Intent(context, AddImageActivity::class.java)
            startActivityForResult(intent, CHOOSE_IMAGE_REQUEST)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteBottomSheetBinding.inflate(layoutInflater)
        binding.bsTakeFromCamera.setOnClickListener(openCameraListener)
        binding.bsChooseImage.setOnClickListener(chooseImageListener)
        return binding.root
    }

    companion object {
        const val TAG = "note_bottom_sheet_fragment"
    }
}