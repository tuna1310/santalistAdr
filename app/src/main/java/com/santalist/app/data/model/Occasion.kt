package com.santalist.app.data.model

enum class Occasion(val title: String) {
    CHRISTMAS("Christmas"),
    BIRTHDAY("Birthday"),
    ANNIVERSARY("Anniversary"),
    OTHER("Other");

    companion object {
        fun fromTitle(title: String): Occasion {
            return entries.firstOrNull { it.title.equals(title, ignoreCase = true) } ?: CHRISTMAS
        }
    }
}
