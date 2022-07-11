package com.pnam.note.database.repositories.impl

import android.util.Log
import com.pnam.note.database.data.locals.CurrentUser
import com.pnam.note.database.data.locals.LoginLocals
import com.pnam.note.database.data.models.Login
import com.pnam.note.database.data.networks.LoginNetworks
import com.pnam.note.database.repositories.LoginRepositories
import com.pnam.note.throwable.NotFoundException
import com.pnam.note.utils.RetrofitUtils.CONFLICT
import com.pnam.note.utils.RetrofitUtils.NOT_FOUND
import com.pnam.note.utils.RetrofitUtils.SUCCESS
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import javax.inject.Inject

class LoginRepositoriesImpl @Inject constructor(
    override val locals: LoginLocals,
    override val networks: LoginNetworks,
//    override val currentUser: CurrentUser
) : LoginRepositories {
    override fun login(email: String, password: String): Single<Login> =
        networks.login(email, password)

    override fun register(email: String, password: String): Single<Login> {
        TODO("Not yet implemented")
    }

    override fun forgotPassword(email: String): Single<Unit> {
        TODO("Not yet implemented")
    }

    override fun changePassword(
        email: String,
        oldPassword: String,
        newPassword: String
    ): Single<Login> {
        TODO("Not yet implemented")
    }
}