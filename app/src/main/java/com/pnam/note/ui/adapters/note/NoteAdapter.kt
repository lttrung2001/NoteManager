package com.pnam.note.ui.adapters.note

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pnam.note.R
import com.pnam.note.database.data.locals.entities.Note
import java.text.SimpleDateFormat
import java.util.*


class NoteAdapter constructor(
    private val listener: NoteItemClickListener
) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(object : DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem == newItem
    }

}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.container_note,
            parent,
            false
        )
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
        with(holder.title) {
            text = note.title
        }
        with (holder.desc) {
            text = note.description
        }
        holder.editAt.text = simpleDateFormat.format(Date(note.editAt))
        holder.itemView.setOnClickListener {
            listener.onClick(note, holder.itemView, position)
        }
        holder.btnDelete.setOnClickListener {
            listener.onDeleteClick(note, position)
        }
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.note_title)
        val desc: TextView = itemView.findViewById(R.id.note_desc)
        val editAt: TextView = itemView.findViewById(R.id.edit_at)
        val btnDelete: ImageView = itemView.findViewById(R.id.btn_delete)
    }
}
