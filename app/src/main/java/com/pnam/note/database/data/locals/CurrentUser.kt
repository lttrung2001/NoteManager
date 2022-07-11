package com.pnam.note.database.data.locals

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import java.util.prefs.Preferences
import javax.inject.Singleton

@Singleton
interface CurrentUser {
    val id: Flowable<String?>
    fun changeCurrentUser(id: String?): Single<Preferences>
    fun signOut(): Single<Preferences>
}