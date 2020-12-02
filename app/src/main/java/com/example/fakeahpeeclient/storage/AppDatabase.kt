package com.example.fakeahpeeclient.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fakeahpeeclient.model.Post

@Database(entities = [Post::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDAO(): PostDAO
}