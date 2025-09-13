package com.example.study.data

import androidx.room.TypeConverter
import com.example.study.data.converter.FlashcardTypeConverter
import com.example.study.data.converter.ListConverter
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    
    private val flashcardTypeConverter = FlashcardTypeConverter()
    
    @TypeConverter
    fun fromFlashcardType(value: FlashcardType): String {
        return flashcardTypeConverter.fromFlashcardType(value)
    }

    @TypeConverter
    fun toFlashcardType(value: String): FlashcardType {
        return flashcardTypeConverter.toFlashcardType(value)
    }
    
    private val listConverter = ListConverter()
    
    @TypeConverter
    fun fromStringList(value: String?): List<String>? {
        return listConverter.fromString(value)
    }

    @TypeConverter
    fun toStringList(list: List<String>?): String? {
        return listConverter.fromList(list)
    }
} 