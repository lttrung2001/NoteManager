package com.pnam.note.ui.changepassword

import com.pnam.note.database.data.models.Login
import com.pnam.note.database.repositories.LoginRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ChangePasswordUseCaseImpl @Inject constructor(
    private val repositories: LoginRepositories
    ) : ChangePasswordUseCase {
    override fun changePassword(
        email: String,
        oldPassword: String,
        newPassword: String
    ): Single<Login> {
        return repositories.changePassword(email, oldPassword, newPassword)
    }
}