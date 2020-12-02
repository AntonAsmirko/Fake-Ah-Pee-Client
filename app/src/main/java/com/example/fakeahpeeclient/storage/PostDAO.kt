package com.example.fakeahpeeclient.storage

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.fakeahpeeclient.model.Post

@Dao
interface PostDAO {

    @Query("SELECT * FROM post")
    suspend fun getAll(): List<Post>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg post: Post)

    @Delete
    suspend fun deletePost(post: Post)

    @Query("DELETE FROM post")
    suspend fun deleteAll()
}