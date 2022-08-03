package com.pnam.note.ui.changepassword

import com.pnam.note.database.data.models.Login
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface ChangePasswordUseCase {
    fun changePassword(email: String, oldPassword: String, newPassword: String): Single<Login>
}