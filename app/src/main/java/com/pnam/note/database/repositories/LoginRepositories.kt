package com.pnam.note.database.repositories

import com.pnam.note.database.data.locals.CurrentUser
import com.pnam.note.database.data.locals.LoginLocals
import com.pnam.note.database.data.models.Login
import com.pnam.note.database.data.networks.LoginNetworks
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface LoginRepositories {
    val locals: LoginLocals
    val networks: LoginNetworks
//    val currentUser: CurrentUser
    fun login(email: String, password: String): Single<Login>
    fun register(email: String, password: String): Single<Login>
    fun forgotPassword(email: String): Single<Unit>
    fun changePassword(email: String, oldPassword: String, newPassword: String): Single<Login>
}