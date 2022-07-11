package com.pnam.note.database.data.locals.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.pnam.note.database.data.locals.LoginLocals
import com.pnam.note.database.data.models.Login
import io.reactivex.rxjava3.core.Single

@Dao
interface LoginDao : LoginLocals {
    @Insert
    override fun login(email: String, password: String): Single<Login> {
        TODO("Not yet implemented")
    }

    @Delete
    override fun deleteLogin(id: String): Single<Login> {
        TODO("Not yet implemented")
    }

    @Update
    override fun changePassword(id: String, newPassword: String): Single<Login> {
        TODO("Not yet implemented")
    }
}