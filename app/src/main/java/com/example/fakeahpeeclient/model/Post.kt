package com.example.fakeahpeeclient.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "post")
class Post(
        @ColumnInfo(name = "body") val body: String,
        @PrimaryKey val id: Int,
        @ColumnInfo(name = "title") val title: String,
        @ColumnInfo(name = "user_id") val userId: Int
)