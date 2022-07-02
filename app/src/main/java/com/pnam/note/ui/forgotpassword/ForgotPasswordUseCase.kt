package com.pnam.note.ui.forgotpassword

import io.reactivex.rxjava3.core.Single

interface ForgotPasswordUseCase {
    fun forgotPassword(email: String): Single<Unit>
}