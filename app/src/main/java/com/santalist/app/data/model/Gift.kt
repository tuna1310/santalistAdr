package com.santalist.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Calendar
import java.util.UUID

@Entity(
    tableName = "gifts",
    foreignKeys = [
        ForeignKey(
            entity = Person::class,
            parentColumns = ["id"],
            childColumns = ["personId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["personId"])]
)
data class Gift(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val price: Int = 0,
    val status: GiftStatus = GiftStatus.IDEA,
    val dueDate: Long? = null,
    val occasion: Occasion = Occasion.CHRISTMAS,
    val note: String = "",
    val url: String = "",
    val personId: String
) {
    fun isOverdue(referenceTimeMillis: Long = System.currentTimeMillis()): Boolean {
        if (dueDate == null) return false
        if (status.isCompleted) return false

        val calDue = Calendar.getInstance().apply {
            timeInMillis = dueDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val calRef = Calendar.getInstance().apply {
            timeInMillis = referenceTimeMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return calDue.timeInMillis < calRef.timeInMillis
    }
}
