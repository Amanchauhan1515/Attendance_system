package com.example.attendancesystem

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase

class studentTimeTable : AppCompatActivity() {

    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_time_table)

        imageView = findViewById(R.id.adminPhotoDisplay)

        loadTimetableImage()
    }

    private fun loadTimetableImage() {
        val photoRef = FirebaseDatabase.getInstance().getReference("GlobalDashboard/photoUrl")

        photoRef.get().addOnSuccessListener {
            val url = it.getValue(String::class.java)
            if (!url.isNullOrEmpty()) {
                Glide.with(this).load(url).into(imageView)
            } else {
                Toast.makeText(this, "No timetable available", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
        }
    }
}
