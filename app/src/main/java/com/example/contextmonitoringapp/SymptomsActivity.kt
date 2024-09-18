package com.example.contextmonitoringapp

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SymptomsActivity : AppCompatActivity() {

    private lateinit var vitalsWithDiseaseDao: VitalsAndDiseasesDao
    private val diseaseRatings = mutableMapOf<Int, Float>()  // Stores ratings for selected diseases
    private var respiratoryRate: Int = 0
    private var heartRate: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_symptoms)

        // Receive heart rate and respiratory rate from MainActivity
        respiratoryRate = intent.getIntExtra("RESPIRATORY_RATE", 0)
        heartRate = intent.getIntExtra("HEART_RATE", 0)

        // If heart rate or respiratory rate are not passed correctly, show an error
        if (respiratoryRate == 0 || heartRate == 0) {
            Toast.makeText(this, "Error: Missing vitals data", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Initialize the DAO
        val db = VitalsDatabase.getDatabase(this)
        vitalsWithDiseaseDao = db.vitalsAndDiseasesDao()

        // UI elements
        val spinnerDiseases = findViewById<Spinner>(R.id.spinner_diseases)
        val ratingBar = findViewById<RatingBar>(R.id.ratingBar)
        val buttonAddRating = findViewById<Button>(R.id.button_add_rating)
        val buttonSubmit = findViewById<Button>(R.id.button_submit)

        // Set up the Spinner with disease options
        val diseases = arrayOf(
            "Cough", "Fever", "Shortness of Breath", "Fatigue",
            "Headache", "Sore Throat", "Muscle Pain",
            "Loss of Taste", "Loss of Smell", "Nausea"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, diseases)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDiseases.adapter = adapter

        // Add rating button click
        buttonAddRating.setOnClickListener {
            val selectedDiseasePosition = spinnerDiseases.selectedItemPosition + 1
            val rating = ratingBar.rating

            // Store the rating for the selected disease
            diseaseRatings[selectedDiseasePosition] = rating

            Toast.makeText(this, "Rating added for ${diseases[selectedDiseasePosition - 1]}", Toast.LENGTH_SHORT).show()

            // Reset the rating bar
            ratingBar.rating = 0f
        }

        // Submit button click
        buttonSubmit.setOnClickListener {
            lifecycleScope.launch {
                try {
                    // Ensure all ratings are captured, default to 0 for unselected diseases
                    val vitalsWithDiseases = VitalsAndDiseases(
                        respiratoryRate = respiratoryRate,
                        heartRate = heartRate,
                        disease1Rating = diseaseRatings[1] ?: 0f,
                        disease2Rating = diseaseRatings[2] ?: 0f,
                        disease3Rating = diseaseRatings[3] ?: 0f,
                        disease4Rating = diseaseRatings[4] ?: 0f,
                        disease5Rating = diseaseRatings[5] ?: 0f,
                        disease6Rating = diseaseRatings[6] ?: 0f,
                        disease7Rating = diseaseRatings[7] ?: 0f,
                        disease8Rating = diseaseRatings[8] ?: 0f,
                        disease9Rating = diseaseRatings[9] ?: 0f,
                        disease10Rating = diseaseRatings[10] ?: 0f
                    )

                    // Insert the data into the database
                    withContext(Dispatchers.IO) {
                        vitalsWithDiseaseDao.insertVitalsAndDiseases(vitalsWithDiseases)
                    }

                    Toast.makeText(this@SymptomsActivity, "Data submitted successfully!", Toast.LENGTH_SHORT).show()

                } catch (e: Exception) {
                    // Catch any potential issues and log them
                    Toast.makeText(this@SymptomsActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("SymptomsActivity", "Error: ${e.message}", e)
                    e.printStackTrace()
                }
            }
        }
    }
}
