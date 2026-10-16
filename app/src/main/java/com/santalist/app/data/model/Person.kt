package com.santalist.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "people")
data class Person(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val budget: Int = 0,
    val note: String = ""
)
