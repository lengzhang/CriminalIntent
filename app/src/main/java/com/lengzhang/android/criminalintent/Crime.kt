package com.lengzhang.android.criminalintent

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Crime(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var title: String = "",
    var date: Date = Date(),
    var isSolved: Boolean = false,
    var requiresPolice: Boolean = false
) {
    companion object {
        fun isEqual(a: Crime, b: Crime) = when {
            a.title != b.title -> false
            a.date != b.date -> false
            a.isSolved != b.isSolved -> false
            else -> true
        }
    }
}