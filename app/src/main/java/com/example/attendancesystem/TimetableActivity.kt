package com.example.attendancesystem

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.bumptech.glide.Glide

class TimetableActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var btnChoose: Button
    private lateinit var btnUpload: Button
    private var imageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timetable)

        imageView = findViewById(R.id.photoPreview)
        btnChoose = findViewById(R.id.btnChooseImage)
        btnUpload = findViewById(R.id.btnUploadImage)

        btnChoose.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        btnUpload.setOnClickListener {
            if (imageUri != null) {
                uploadImageToFirebase(imageUri!!)
            } else {
                Toast.makeText(this, "Please choose an image first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            imageView.setImageURI(imageUri)
        }
    }

    private fun uploadImageToFirebase(uri: Uri) {
        val storageRef = FirebaseStorage.getInstance().getReference("GlobalDashboard/timetable.jpg")

        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val photoUrl = downloadUri.toString()
                    FirebaseDatabase.getInstance().getReference("GlobalDashboard")
                        .child("photoUrl").setValue(photoUrl)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Timetable uploaded successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to save URL", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
            }
    }
}
