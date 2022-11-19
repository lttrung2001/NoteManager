package com.pnam.note.ui.adapters.login

import com.pnam.note.database.data.locals.entities.EmailPassword

interface LoginItemClickListener {
    fun onClick(emailPassword: EmailPassword)
    fun onDeleteClick(email: String, position: Int)
}