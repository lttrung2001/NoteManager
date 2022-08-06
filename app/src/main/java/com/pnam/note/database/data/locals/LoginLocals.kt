package com.pnam.note.database.data.locals

import com.pnam.note.database.data.models.EmailPassword
import javax.inject.Singleton

@Singleton
interface LoginLocals {
    fun login(emailPassword: EmailPassword)
    fun deleteLogin(email: String)
    fun changePassword(email: String, newPassword: String)
}