package com.pnam.note.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pnam.note.R
import com.pnam.note.database.data.models.EmailPassword
import com.pnam.note.database.data.models.Note

class LoginAdapter(
    val list: MutableList<EmailPassword>,
    private val listener: LoginItemClickListener) : RecyclerView.Adapter<LoginAdapter.LoginViewHolder>() {
    class LoginViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvEmail: TextView = itemView.findViewById(R.id.tv_email)
        val btnDelete: ImageView = itemView.findViewById(R.id.btn_delete_login)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoginViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.login_container, parent, false)
        return LoginViewHolder(view)
    }

    override fun onBindViewHolder(holder: LoginViewHolder, position: Int) {
        holder.tvEmail.text = list[position].email
        holder.itemView.setOnClickListener {
            listener.onClick(list[position])
        }
        holder.btnDelete.setOnClickListener {
            listener.onDeleteClick(list[position].email, position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun removeAt(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }
}