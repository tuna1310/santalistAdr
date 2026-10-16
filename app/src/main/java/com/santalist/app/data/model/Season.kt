package com.santalist.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "seasons")
data class Season(
    @PrimaryKey
    val year: Int,
    val occasion: String = "Christmas"
)
