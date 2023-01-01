package com.pnam.note.database.data.locals.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EmailPassword (
    @PrimaryKey @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "password") val password: String
)