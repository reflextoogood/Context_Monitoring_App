//package com.example.contextmonitoringapp
//
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.Query
//
//@Dao
//interface SymptomDao {
//    @Insert
//    suspend fun insertSymptomRating(symptomRating: SymptomRating)
//
//    @Query("SELECT * FROM symptom_ratings")
//    suspend fun getAllSymptomRatings(): List<SymptomRating>
//}
