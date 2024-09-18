package com.example.contextmonitoringapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vitals_and_diseases")
data class VitalsAndDiseases(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val respiratoryRate: Int,
    val heartRate: Int,
    val disease1Rating: Float = 0f,
    val disease2Rating: Float = 0f,
    val disease3Rating: Float = 0f,
    val disease4Rating: Float = 0f,
    val disease5Rating: Float = 0f,
    val disease6Rating: Float = 0f,
    val disease7Rating: Float = 0f,
    val disease8Rating: Float = 0f,
    val disease9Rating: Float = 0f,
    val disease10Rating: Float = 0f
)