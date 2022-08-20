package com.pnam.note.ui.forgotpassword

import com.pnam.note.database.repositories.LoginRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ForgotPasswordUseCaseImpl @Inject constructor(
    private val repositories: LoginRepositories
) : ForgotPasswordUseCase {
    override fun forgotPassword(email: String): Single<Unit> {
        return repositories.forgotPassword(email)
    }
}