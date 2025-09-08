package com.example.topmovers.data.model


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlists")
data class WatchList(
    @PrimaryKey(autoGenerate = true)
    val watchlistId: Long = 0L,
    val name: String
)
