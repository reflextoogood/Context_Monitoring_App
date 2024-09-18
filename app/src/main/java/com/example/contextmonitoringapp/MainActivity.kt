package com.example.contextmonitoringapp

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private var respiratoryRate: Int = 0
    private var heartRate: Int = 0

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set the toolbar as the action bar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Find the UI elements
        val buttonRespiratoryRate = findViewById<Button>(R.id.button_respiratory_rate)
        val textRespiratoryRate = findViewById<TextView>(R.id.text_respiratory_rate)

        val buttonHeartRate = findViewById<Button>(R.id.button_heart_rate)
        val textHeartRate = findViewById<TextView>(R.id.text_heart_rate)

        val buttonSymptoms = findViewById<Button>(R.id.button_symptoms)

        // Button click listener for respiratory rate
        buttonRespiratoryRate.setOnClickListener {
            // Measure respiratory rate
            val accelValuesX = readCSVFromAssets("CSVBreatheX.csv")
            val accelValuesY = readCSVFromAssets("CSVBreatheY.csv")
            val accelValuesZ = readCSVFromAssets("CSVBreatheZ.csv")

            respiratoryRate = respiratoryRateCalculator(accelValuesX, accelValuesY, accelValuesZ)
            // Update the TextView with the respiratory rate
            textRespiratoryRate.text = "Respiratory Rate: $respiratoryRate breaths/min"
        }

        // Button click listener for heart rate
        buttonHeartRate.setOnClickListener {
            // Measure heart rate using the first video found in MediaStore
            val videoUriList = queryVideosFromMediaStore(this)
            if (videoUriList.isNotEmpty()) {
                val videoUri = videoUriList.first()
                Log.d("MainActivity", "Found video URI: $videoUri")

                // Calculate heart rate and update the UI
                CoroutineScope(Dispatchers.Main).launch {
                    heartRate = calculateHeartRate(videoUri)
                    textHeartRate.text = "Heart Rate: $heartRate bpm"
                }
            } else {
                Log.e("MainActivity", "No video found for heart rate measurement")
            }
        }

        // Button click listener for navigating to Symptoms page
        buttonSymptoms.setOnClickListener {
            // Navigate to the Symptoms Activity, passing the heart and respiratory rates
            val intent = Intent(this, SymptomsActivity::class.java)
            intent.putExtra("HEART_RATE", heartRate)
            intent.putExtra("RESPIRATORY_RATE", respiratoryRate)
            startActivity(intent)
        }
    }

    // Function to calculate heart rate
    @RequiresApi(Build.VERSION_CODES.P)
    private suspend fun calculateHeartRate(videoUri: Uri): Int {
        // Dummy implementation for simplicity, your actual logic will go here
        return heartRateCalculator(videoUri, contentResolver)
    }

    // Function to read CSV from assets
    private fun readCSVFromAssets(fileName: String): MutableList<Float> {
        val inputStream = assets.open(fileName)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val values = mutableListOf<Float>()
        reader.forEachLine { line -> values.add(line.toFloat()) }
        reader.close()
        return values
    }

    // Function to query videos from MediaStore
    private fun queryVideosFromMediaStore(context: Context): List<Uri> {
        val videoUriList = mutableListOf<Uri>()
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME
        )

        val cursor = context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val id = it.getLong(idColumn)
                val videoUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                videoUriList.add(videoUri)
            }
        }

        return videoUriList
    }
}

