package com.example.tsans_room.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Book::class],
    version = 1
)
abstract class BookDb : RoomDatabase(){

    abstract fun bookDao() : BookDao

    companion object {
        @Volatile private var instance : BookDb? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            BookDb::class.java,
            "book12345.db"
        ).build()

    }
}