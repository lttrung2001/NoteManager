package com.pnam.note.ui.adapters.image

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.pnam.note.R

class ImageAdapter(
    private val list: MutableList<String>,
    private val listener: ImageItemClickListener
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.img_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.image_container, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.image.setImageDrawable(Drawable.createFromPath(list[position]))
        holder.image.setOnClickListener {
            listener.onClick(list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}