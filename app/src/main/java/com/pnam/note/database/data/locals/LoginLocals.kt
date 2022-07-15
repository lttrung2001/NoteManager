package com.pnam.note.database.data.locals

import com.pnam.note.database.data.models.Login
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface LoginLocals {
    fun login(email: String, password: String): Single<Login>
    fun deleteLogin(id: String): Single<Login>
    fun changePassword(id: String, newPassword: String): Single<Login>
}