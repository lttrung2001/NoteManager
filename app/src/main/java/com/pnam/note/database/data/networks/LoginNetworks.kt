package com.pnam.note.database.data.networks

import com.pnam.note.database.data.models.Login
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface LoginNetworks {
    fun login(email: String, password: String): Single<Login>
    fun register(email: String, password: String): Single<Login>
    fun forgotPassword(email: String): Single<Unit>
    fun changePassword(email: String, oldPassword: String, newPassword: String): Single<Login>
}