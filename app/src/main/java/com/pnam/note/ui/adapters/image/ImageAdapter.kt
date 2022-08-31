package com.pnam.note.ui.adapters.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.bumptech.glide.Glide
import com.pnam.note.R


class ImageAdapter(
    private val listener: ImageItemClickListener,
    private val fragment: Fragment
) : ListAdapter<String, ImageAdapter.ImageViewHolder>(object : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem.contentEquals(newItem)
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem.contentEquals(newItem)
    }
}) {
    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.img_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.image_container, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
//        val bitmap = BitmapFactory.decodeFile(getItem(position))
//        Glide.with(fragment).load(bitmap).into(holder.image)
        holder.image.load(getItem(position)) {
            crossfade(true)
            placeholder(R.drawable.note_application)
        }
        holder.image.setOnClickListener {
            listener.onClick(getItem(position))
        }
    }
}