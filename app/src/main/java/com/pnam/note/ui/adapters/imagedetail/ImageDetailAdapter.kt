package com.pnam.note.ui.adapters.imagedetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pnam.note.ui.imagedetail.ImageDetailFragment

class ImageDetailAdapter(
    fragmentActivity: FragmentActivity,
    private val imagesPath: MutableList<String>
) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return imagesPath.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = ImageDetailFragment()
        fragment.arguments = Bundle().apply {
            putString(IMAGE, imagesPath[position])
        }
        return fragment
    }

    internal fun removeAt(position: Int) {
        imagesPath.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    internal fun getList(): List<String> {
        return imagesPath
    }

    override fun getItemId(position: Int): Long {
        return imagesPath[position].hashCode().toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        val pageIds = imagesPath.map { it.hashCode().toLong() }
        return pageIds.contains(itemId)
    }

    companion object {
        const val IMAGE = "image"
    }
}