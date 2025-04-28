package com.example.attendancesystem

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.annotation.SuppressLint
import android.content.Intent
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.zxing.integration.android.IntentIntegrator
import java.text.SimpleDateFormat
import java.util.*

class scanQr : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val targetLatitude = 19.2281004
    private val targetLongitude = 73.1326731
    private val locationThreshold = 100.0

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var user: User
    private lateinit var mAuth: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var btn: Button
    private var firestore = Firebase.firestore
    private val uid = FirebaseAuth.getInstance().currentUser!!.uid
    private val useruid = FirebaseAuth.getInstance().currentUser!!.uid
    private val ref = firestore.collection("USER").document(useruid)

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val date = "$year-$month-$day"
    val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.time)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_qr)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        //checkUserLocation()

        btn = findViewById(R.id.button2)
        btn.setOnClickListener {
            val scanner = IntentIntegrator(this)
            scanner.initiateScan()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == "subject1" || result.contents == "subject2" || result.contents == "subject3" || result.contents == "subject4" || result.contents == "subject5") {

                    val subject = result.contents
                    Toast.makeText(this, "Please wait...", Toast.LENGTH_LONG).show()
                    auth = FirebaseAuth.getInstance()
                    databaseReference = FirebaseDatabase.getInstance().getReference("USER")

                    if (uid.isNotEmpty()) {
                        getUserData(subject)
                    }

                } else {
                    Toast.makeText(this, "Attendance not marked ", Toast.LENGTH_LONG).show()
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun getUserData(subject: String) {
        databaseReference.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(User::class.java)!!
                val userName = user.username.toString()
                val roll = user.rollNo.toString()
                val timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

                val storageRef = FirebaseStorage.getInstance().getReference("profile_pics/$uid.jpg")

                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val profileImageUrl = uri.toString()

                    val subjectName = when (subject) {
                        "subject1" -> "Linux administration"
                        "subject2" -> "Cloud computing"
                        "subject3" -> "Web services"
                        "subject4" -> "Ethical hacking"
                        "subject5" -> "Data science"
                        else -> "Unknown Subject"
                    }

                    addUserToDatabase(subjectName, userName, roll, profileImageUrl, timestamp)

                }.addOnFailureListener {
                    Toast.makeText(this@scanQr, "Profile image not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@scanQr, "Failed to get user data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addUserToDatabase(subjectName: String, userName: String, roll: String?, imageUrl: String, timestamp: String) {
        val attendance = Attendance(userName, roll, imageUrl, timestamp)
        mAuth = FirebaseDatabase.getInstance().getReference()
        mAuth.child("Attendance $year")
            .child(monthName)
            .child(subjectName)
            .child(date)
            .child(uid)
            .setValue(attendance)
            .addOnSuccessListener {
                Toast.makeText(this, "Attendance marked successfully", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val distance = FloatArray(1)
                Location.distanceBetween(
                    location.latitude,
                    location.longitude,
                    targetLatitude,
                    targetLongitude,
                    distance
                )

                if (distance[0] > locationThreshold) {
                    Toast.makeText(this, "You are not at the target location!", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            } else {
                Toast.makeText(this, "Unable to retrieve location. Try again later.", Toast.LENGTH_LONG).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
}

