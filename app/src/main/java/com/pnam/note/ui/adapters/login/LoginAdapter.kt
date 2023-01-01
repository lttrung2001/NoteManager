package com.pnam.note.ui.adapters.login

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pnam.note.R
import com.pnam.note.database.data.locals.entities.EmailPassword

class LoginAdapter(
    private val listener: LoginItemClickListener
) : ListAdapter<EmailPassword, LoginAdapter.LoginViewHolder>(object :
    DiffUtil.ItemCallback<EmailPassword>() {
    override fun areItemsTheSame(oldItem: EmailPassword, newItem: EmailPassword): Boolean {
        return oldItem.email == newItem.email
    }

    override fun areContentsTheSame(oldItem: EmailPassword, newItem: EmailPassword): Boolean {
        return oldItem == newItem
    }

}) {
    class LoginViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvEmail: TextView = itemView.findViewById(R.id.tv_email)
        val btnDelete: ImageView = itemView.findViewById(R.id.btn_delete_login)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoginViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.container_login, parent, false)
        return LoginViewHolder(view)
    }

    override fun onBindViewHolder(holder: LoginViewHolder, position: Int) {
        holder.tvEmail.text = getItem(position).email
        holder.itemView.setOnClickListener {
            listener.onClick(getItem(position))
        }
        holder.btnDelete.setOnClickListener {
            listener.onDeleteClick(getItem(position).email, position)
        }
    }
}