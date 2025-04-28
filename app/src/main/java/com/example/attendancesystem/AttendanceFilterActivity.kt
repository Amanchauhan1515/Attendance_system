package com.example.attendancesystem


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class AttendanceFilterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance_filter)

        val titleText = findViewById<TextView>(R.id.titleText)
        val currentMonth = SimpleDateFormat("MMMM", Locale.getDefault()).format(Date())
        titleText.text = "$currentMonth Attendance Percentage"


        findViewById<Button>(R.id.btnSeventy).setOnClickListener {
            val intent = Intent(this,LowTotalAttendanceActivity ::class.java)
            startActivity(intent)

        }

        findViewById<Button>(R.id.btnAll).setOnClickListener {
            val intent = Intent(this,TimetableActivity ::class.java)
            startActivity(intent)
        }

    }

}
