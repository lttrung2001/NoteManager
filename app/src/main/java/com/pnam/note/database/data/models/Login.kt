package com.pnam.note.database.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "logins")
data class Login(
    @PrimaryKey @ColumnInfo(name = "login_id") val id: String,
    @ColumnInfo(name = "email") var email: String,
    @ColumnInfo(name = "password") val password: String
)
