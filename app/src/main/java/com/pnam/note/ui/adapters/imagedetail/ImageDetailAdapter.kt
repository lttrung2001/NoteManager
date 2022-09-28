package com.pnam.note.ui.adapters.imagedetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pnam.note.ui.imagedetail.ImageFragment

class ImageDetailAdapter(fragmentActivity: FragmentActivity,
    private val imagePaths: List<String>) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return imagePaths.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = ImageFragment()
        fragment.arguments = Bundle().apply {
            putInt(ARG_OBJECT, position + 1)
            putString(IMAGE, imagePaths[position])
        }
        return fragment
    }

    companion object {
        private const val ARG_OBJECT = "object"
        private const val IMAGE = "image"
    }
}