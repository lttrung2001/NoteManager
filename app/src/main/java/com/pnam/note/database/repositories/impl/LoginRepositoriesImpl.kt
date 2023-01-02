package com.pnam.note.database.repositories.impl

import com.pnam.note.database.data.locals.LoginLocals
import com.pnam.note.database.data.locals.entities.EmailPassword
import com.pnam.note.database.data.models.Login
import com.pnam.note.database.data.networks.LoginNetworks
import com.pnam.note.database.repositories.LoginRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class LoginRepositoriesImpl @Inject constructor(
    override val locals: LoginLocals,
    override val networks: LoginNetworks,
) : LoginRepositories {
    override fun login(email: String, password: String): Single<Login> {
        return networks.login(email, password).doOnSuccess {
            locals.login(EmailPassword(email, password))
        }
    }

    override fun register(email: String, password: String): Single<Login> {
        return networks.register(email, password)
    }

    override fun forgotPassword(email: String): Single<Unit> {
        return networks.forgotPassword(email)
    }

    override fun changePassword(
        email: String,
        oldPassword: String,
        newPassword: String
    ): Single<Login> {
        return networks.changePassword(email, oldPassword, newPassword).doOnSuccess {
            locals.changePassword(email, newPassword)
        }
    }

    override fun deleteLogin(email: String) {
        return locals.deleteLogin(email)
    }

    override fun getSavedLogins(): Single<MutableList<EmailPassword>> {
        return locals.getSavedLogins()
    }
}