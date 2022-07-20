package com.pnam.note.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pnam.note.R
import com.pnam.note.database.data.models.Note
import java.text.SimpleDateFormat
import java.util.*

class NoteAdapter constructor(
    private val list: List<Note>,
    private val listener: NoteItemClickListener
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.note_title)
        val desc: TextView = itemView.findViewById(R.id.note_desc)
        val editAt: TextView = itemView.findViewById(R.id.edit_at)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.note_container,
            parent,
            false
        )
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = list[position]
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        holder.title.text = note.title
        holder.desc.text = note.description
        holder.editAt.text = simpleDateFormat.format(Date(note.editAt))
        holder.itemView.setOnClickListener {
            listener.onClick(note, holder.itemView)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


}