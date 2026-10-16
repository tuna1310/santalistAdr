package com.santalist.app.data.local

import androidx.room.TypeConverter
import com.santalist.app.data.model.GiftStatus
import com.santalist.app.data.model.Occasion

class Converters {
    @TypeConverter
    fun fromGiftStatus(status: GiftStatus): Int = status.rawValue

    @TypeConverter
    fun toGiftStatus(raw: Int): GiftStatus = GiftStatus.fromRaw(raw)

    @TypeConverter
    fun fromOccasion(occasion: Occasion): String = occasion.name

    @TypeConverter
    fun toOccasion(name: String): Occasion = try {
        Occasion.valueOf(name)
    } catch (_: Exception) {
        Occasion.CHRISTMAS
    }
}
