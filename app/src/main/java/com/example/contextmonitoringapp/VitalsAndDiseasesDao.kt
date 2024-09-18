package com.example.contextmonitoringapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface VitalsAndDiseasesDao {
    @Insert
    suspend fun insertVitalsAndDiseases(vitalsAndDiseases: VitalsAndDiseases)

    @Query("SELECT * FROM vitals_and_diseases")
    suspend fun getAllVitalsAndDiseases(): List<VitalsAndDiseases>
}


