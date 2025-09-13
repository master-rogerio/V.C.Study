package com.example.study.data.converter

import androidx.room.TypeConverter
import com.example.study.data.FlashcardType

class FlashcardTypeConverter {
    @TypeConverter
    fun fromFlashcardType(value: FlashcardType): String {
        return value.name
    }

    @TypeConverter
    fun toFlashcardType(value: String): FlashcardType {
        return FlashcardType.valueOf(value)
    }
} 