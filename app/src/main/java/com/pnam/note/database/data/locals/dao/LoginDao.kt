package com.pnam.note.database.data.locals.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.pnam.note.database.data.locals.LoginLocals
import com.pnam.note.database.data.locals.entities.EmailPassword
import io.reactivex.rxjava3.core.Single

@Dao
interface LoginDao : LoginLocals {
    @Insert(onConflict = REPLACE)
    override fun login(emailPassword: EmailPassword)

    @Query("DELETE FROM EmailPassword WHERE email = :email")
    override fun deleteLogin(email: String)

    @Query("UPDATE EmailPassword SET password = :newPassword WHERE email = :email")
    override fun changePassword(email: String, newPassword: String)

    @Query("SELECT * FROM EmailPassword ORDER BY email")
    override fun getSavedLogins(): Single<MutableList<EmailPassword>>
}