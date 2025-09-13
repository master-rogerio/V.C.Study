package com.example.study.data

import androidx.room.TypeConverter
import com.example.study.data.converter.FlashcardTypeConverter
import com.example.study.data.converter.ListConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

    @TypeConverter
    fun fromFlashcardTypeList(value: String?): List<FlashcardType>? {
        if (value == null) {
            return null
        }
        val listType = object : TypeToken<List<FlashcardType>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun toFlashcardTypeList(list: List<FlashcardType>?): String? {
        return Gson().toJson(list)
    }
} 