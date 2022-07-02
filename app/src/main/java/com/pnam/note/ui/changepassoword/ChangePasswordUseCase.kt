package com.pnam.note.ui.changepassoword

import com.pnam.note.database.data.models.Login
import io.reactivex.rxjava3.core.Single

interface ChangePasswordUseCase {
    fun changePassword(email: String, oldPassword: String, newPassword: String): Single<Login>
}