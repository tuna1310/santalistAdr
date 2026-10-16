package com.santalist.app.data.model

enum class GiftStatus(val rawValue: Int, val title: String) {
    IDEA(0, "Idea"),
    ORDERED(1, "Ordered"),
    RECEIVED(2, "Received"),
    WRAPPED(3, "Wrapped");

    val isSpent: Boolean
        get() = this >= ORDERED

    val isCompleted: Boolean
        get() = this == RECEIVED || this == WRAPPED

    fun next(): GiftStatus {
        return when (this) {
            IDEA -> ORDERED
            ORDERED -> RECEIVED
            RECEIVED -> WRAPPED
            WRAPPED -> IDEA
        }
    }

    companion object {
        fun fromRaw(raw: Int): GiftStatus {
            return entries.firstOrNull { it.rawValue == raw } ?: IDEA
        }
    }
}
