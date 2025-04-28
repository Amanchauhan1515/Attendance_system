package com.example.attendancesystem

import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import java.io.File
import java.io.FileOutputStream

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.attendancesystem.Adapter.UserAdapter
import com.google.firebase.database.*

class LowTotalAttendanceActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter
    private val lowAttendanceList = ArrayList<UserWithPercentage>()
    private val allUsersMap = mutableMapOf<String, User>()
    private var thresholdPercentage: Int = 75  // Default threshold, can be changed by user

    private val subjects = listOf(
        "Linux administration",
        "Cloud computing",
        "Web services",
        "Ethical hacking",
        "Data science"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_users)

        recyclerView = findViewById(R.id.allUsersRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = UserAdapter(lowAttendanceList)
        recyclerView.adapter = adapter

        val calendar = java.util.Calendar.getInstance()
        val currentYear = calendar.get(java.util.Calendar.YEAR).toString()
        val currentMonth = java.text.SimpleDateFormat("MMMM").format(calendar.time)

        val numberInput = findViewById<EditText>(R.id.numberInput)
        val submitButton = findViewById<Button>(R.id.submitButton)

        submitButton.setOnClickListener {
            val inputText = numberInput.text.toString()
            if (inputText.isNotEmpty()) {
                val number = inputText.toInt()
                if (number in 0..100) {
                    thresholdPercentage = number
                    fetchAllUsers {
                        calculateOverallAttendance(currentYear, currentMonth)
                    }
                } else {
                    Toast.makeText(this, "Please enter a number between 0 and 100", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Enter the number", Toast.LENGTH_LONG).show()
            }
        }

        findViewById<Button>(R.id.btnNotifyDefaulters).setOnClickListener {
            if (lowAttendanceList.isEmpty()) {
                Toast.makeText(this, "No defaulters to notify.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setTitle("Notify Defaulters")
                .setMessage("Send notification to ${lowAttendanceList.size} students?")
                .setPositiveButton("Send") { _, _ -> sendNotificationToDefaulters() }
                .setNegativeButton("Cancel", null)
                .show()
        }

        findViewById<Button>(R.id.btnExportPdf).setOnClickListener {
            exportDefaultersToPdf()
        }
    }

    private fun exportDefaultersToPdf() {
        if (lowAttendanceList.isEmpty()) {
            Toast.makeText(this, "No defaulters to export.", Toast.LENGTH_SHORT).show()
            return
        }

        val pdfDocument = PdfDocument()
        val paint = Paint()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        paint.textSize = 18f
        paint.isFakeBoldText = true
        canvas.drawText("Defaulter Report", 200f, 40f, paint)

        paint.textSize = 14f
        paint.isFakeBoldText = false
        var y = 80f
        canvas.drawText("Name", 30f, y, paint)
        canvas.drawText("Roll No", 220f, y, paint)
        canvas.drawText("Attendance %", 400f, y, paint)
        y += 20f
        canvas.drawLine(30f, y, 550f, y, paint)
        y += 20f

        for (defaulter in lowAttendanceList) {
            if (y > 800f) break // prevent overflow
            canvas.drawText(defaulter.user.username ?: "N/A", 30f, y, paint)
            canvas.drawText(defaulter.user.rollNo ?: "N/A", 220f, y, paint)
            canvas.drawText("${defaulter.percent}%", 400f, y, paint)
            y += 25f
        }

        pdfDocument.finishPage(page)

        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "Defaulter_Report.pdf"
        )

        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()

        Toast.makeText(this, "PDF saved: ${file.absolutePath}", Toast.LENGTH_LONG).show()
    }

    private fun sendNotificationToDefaulters() {
        val databaseRef = FirebaseDatabase.getInstance().getReference("NOTIFICATIONS")
        var count = 0

        for (defaulter in lowAttendanceList) {
            val uid = defaulter.user.uid ?: continue
            val message = mapOf(
                "title" to "Defaulter Alert",
                "body" to "Dear ${defaulter.user.username}, your attendance is ${defaulter.percent}%. Please improve it.",
                "timestamp" to System.currentTimeMillis().toString()
            )

            databaseRef.child(uid).push().setValue(message)
            count++
        }

        Toast.makeText(this, "Notifications sent to $count defaulters.", Toast.LENGTH_SHORT).show()
    }

    private fun fetchAllUsers(onComplete: () -> Unit) {
        FirebaseDatabase.getInstance().getReference("USER")
            .get()
            .addOnSuccessListener { snapshot ->
                for (child in snapshot.children) {
                    val user = child.getValue(User::class.java)
                    if (user != null && user.uid != null) {
                        allUsersMap[user.uid!!] = user
                    }
                }
                onComplete()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch users", Toast.LENGTH_SHORT).show()
            }
    }

    private fun calculateOverallAttendance(year: String, month: String) {
        val attendanceRef = FirebaseDatabase.getInstance().reference.child("Attendance $year").child(month)
        val studentPresentCount = mutableMapOf<String, Int>()
        var totalClassCount = 0
        var subjectProcessed = 0

        for (subject in subjects) {
            attendanceRef.child(subject).get().addOnSuccessListener { subjectSnapshot ->
                for (dateSnapshot in subjectSnapshot.children) {
                    totalClassCount++
                    for (studentSnapshot in dateSnapshot.children) {
                        val uid = studentSnapshot.key ?: continue
                        val currentCount = studentPresentCount[uid] ?: 0
                        studentPresentCount[uid] = currentCount + 1
                    }
                }

                subjectProcessed++
                if (subjectProcessed == subjects.size) {
                    generateLowAttendanceList(studentPresentCount, totalClassCount)
                }

            }.addOnFailureListener {
                Toast.makeText(this, "Failed to fetch $subject attendance", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateLowAttendanceList(presentMap: Map<String, Int>, totalClasses: Int) {
        lowAttendanceList.clear()

        for ((uid, user) in allUsersMap) {
            val present = presentMap[uid] ?: 0
            val percentage = if (totalClasses > 0) (present * 100.0 / totalClasses).toInt() else 0
            if (percentage <= thresholdPercentage) {
                lowAttendanceList.add(UserWithPercentage(user, percentage))
            }
        }

        adapter.notifyDataSetChanged()
    }

    data class UserWithPercentage(val user: User, val percent: Int)
}
