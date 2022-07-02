package com.pnam.note.ui.register

import com.pnam.note.database.data.models.Login
import io.reactivex.rxjava3.core.Single

interface RegisterUseCase {
    fun register(email: String, password: String): Single<Login>
}