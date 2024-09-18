package com.example.contextmonitoringapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "symptom_ratings")
data class SymptomRating(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val diseaseName: String,
    val rating: Float
)