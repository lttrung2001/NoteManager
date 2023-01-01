package com.pnam.note.database.data.locals

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pnam.note.database.data.locals.dao.LoginDao
import com.pnam.note.database.data.locals.dao.NoteDao
import com.pnam.note.database.data.locals.entities.EmailPassword
import com.pnam.note.database.data.locals.entities.Note
import com.pnam.note.database.data.locals.entities.NoteStatus
import com.pnam.note.utils.Converters
import com.pnam.note.utils.RoomUtils.Companion.DB_VER
import javax.inject.Singleton

@Singleton
@Database(entities = [EmailPassword::class, Note::class, NoteStatus::class], version = DB_VER)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun loginDao(): LoginDao
    abstract fun noteDao(): NoteDao
}