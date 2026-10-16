package com.santalist.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "activities")
data class ActivityRecord(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val actionType: String, // e.g. "ADD_PERSON", "UPDATE_PERSON", "DELETE_PERSON", "ADD_GIFT", "UPDATE_GIFT_STATUS", "DELETE_GIFT", "SET_BUDGET"
    val title: String,
    val details: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val year: Int = 2026
)
