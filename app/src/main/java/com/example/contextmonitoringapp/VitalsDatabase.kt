package com.example.contextmonitoringapp

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [VitalsAndDiseases::class], version = 1, exportSchema = false)
abstract class VitalsDatabase : RoomDatabase() {

    abstract fun vitalsAndDiseasesDao(): VitalsAndDiseasesDao

    companion object {
        @Volatile
        private var INSTANCE: VitalsDatabase? = null

        fun getDatabase(context: Context): VitalsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VitalsDatabase::class.java,
                    "vitals_database"
                ).fallbackToDestructiveMigration() //This will delete the existing database and create a new one
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
