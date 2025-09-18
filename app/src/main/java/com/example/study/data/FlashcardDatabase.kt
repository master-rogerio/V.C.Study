package com.example.study.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Deck::class, Flashcard::class, UserLocation::class, FavoriteLocation::class, LocationAnalytics::class, LocationRotationMemory::class], version = 11, exportSchema = false)
@TypeConverters(Converters::class)
abstract class FlashcardDatabase : RoomDatabase() {
    abstract fun flashcardDao(): FlashcardDao
    abstract fun deckDao(): DeckDao
    abstract fun userLocationDao(): UserLocationDao
    abstract fun favoriteLocationDao(): FavoriteLocationDao
    abstract fun locationAnalyticsDao(): LocationAnalyticsDao
    abstract fun locationRotationMemoryDao(): LocationRotationMemoryDao

    companion object {
        @Volatile
        private var INSTANCE: FlashcardDatabase? = null

        fun getDatabase(context: Context): FlashcardDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FlashcardDatabase::class.java,
                    "flashcard_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}