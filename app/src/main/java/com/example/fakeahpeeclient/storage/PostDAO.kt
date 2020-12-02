package com.example.fakeahpeeclient.storage

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fakeahpeeclient.model.Post

@Dao
interface PostDAO {

    @Query("SELECT * FROM post")
    suspend fun getAll(): List<Post>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg post: Post)
}