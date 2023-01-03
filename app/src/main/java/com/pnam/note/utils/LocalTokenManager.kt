package com.pnam.note.utils

import android.content.SharedPreferences
import com.pnam.note.database.data.locals.LoginLocals
import com.pnam.note.database.data.locals.NoteLocals
import com.pnam.note.ui.login.LoginActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
interface LocalTokenManager {
    fun logout()

    class LocalTokenManagerImpl @Inject constructor(
        private val noteLocals: NoteLocals,
        private val sharedPreferences: SharedPreferences
    ) : LocalTokenManager {
        override fun logout() {
            sharedPreferences.edit().let { editor ->
                editor.remove(LoginActivity.EMAIL).apply()
                editor.remove(AppConstants.ACCESS_TOKEN).apply()
                editor.remove(AppConstants.LOGIN_TOKEN).apply()
            }
            noteLocals.deleteAllNote()
        }
    }
}

