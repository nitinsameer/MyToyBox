package com.toybox.app

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context

/**
 * Database version history:
 * 1 -> 2: Added indexing for queries
 */

@Database(entities = [Toy::class], version = 2)
abstract class ToyDatabase : RoomDatabase() {
    abstract fun toyDao(): ToyDao

    companion object {
        @Volatile private var INSTANCE: ToyDatabase? = null

        // Migration from version 1 to version 2: Add indexes for better query performance
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                try {
                    // Add index on category for faster filtering
                    database.execSQL("CREATE INDEX IF NOT EXISTS `idx_toys_category` ON `toys` (`category`)")
                    // Add index on purchaseDate for sorting
                    database.execSQL("CREATE INDEX IF NOT EXISTS `idx_toys_purchaseDate` ON `toys` (`purchaseDate`)")
                } catch (e: Exception) {
                    android.util.Log.e("ToyDatabase", "Migration 1->2 failed: ${e.message}")
                }
            }
        }

        fun getDatabase(context: Context): ToyDatabase {
            return INSTANCE ?: synchronized(this) {
                try {
                    Room.databaseBuilder(context, ToyDatabase::class.java, "toy_db")
                        .addMigrations(MIGRATION_1_2)
                        .build()
                        .also { INSTANCE = it }
                } catch (e: Exception) {
                    android.util.Log.e("ToyDatabase", "Database initialization failed: ${e.message}", e)
                    throw e
                }
            }
        }
    }
}