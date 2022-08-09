package com.pnam.note.database.data.locals

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pnam.note.database.data.locals.dao.LoginDao
import com.pnam.note.database.data.locals.dao.NoteDao
import com.pnam.note.database.data.models.EmailPassword
import com.pnam.note.database.data.models.Note
import com.pnam.note.database.data.models.NoteStatus
import com.pnam.note.utils.RoomUtils.Companion.DB_VER
import javax.inject.Singleton

@Singleton
@Database(entities = [EmailPassword::class, Note::class, NoteStatus::class], version = DB_VER)
abstract class AppDatabase : RoomDatabase() {
    abstract fun loginDao(): LoginDao
    abstract fun noteDao(): NoteDao
}