package com.pnam.note.ui.adapters.imagedetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pnam.note.ui.imagedetail.ImageDetailFragment

class ImageDetailAdapter(
    fragmentActivity: FragmentActivity,
    private val imagePaths: List<String>
) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return imagePaths.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = ImageDetailFragment()
        fragment.arguments = Bundle().apply {
            putString(IMAGE, imagePaths[position])
        }
        return fragment
    }

    companion object {
        const val IMAGE = "image"
    }
}