package com.pnam.note.database.data.locals.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.pnam.note.database.data.locals.LoginLocals
import com.pnam.note.database.data.models.EmailPassword
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface LoginDao : LoginLocals {
    @Insert(onConflict = REPLACE)
    override fun login(emailPassword: EmailPassword): Completable

    @Query("DELETE FROM EmailPassword WHERE email = :email")
    override fun deleteLogin(email: String): Completable

    @Query("UPDATE EmailPassword SET password = :newPassword WHERE email = :email")
    override fun changePassword(email: String, newPassword: String): Completable

    @Query("SELECT * FROM EmailPassword ORDER BY email")
    fun getAllLogin(): Single<MutableList<EmailPassword>>
}