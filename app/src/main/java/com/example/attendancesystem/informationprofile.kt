package com.example.attendancesystem

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.attendancesystem.databinding.ActivityInformationprofileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class informationprofile : AppCompatActivity() {
    private lateinit var binding: ActivityInformationprofileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var dialog: Dialog
    private lateinit var user: User
    private lateinit var uid: String
    private var imageUri: Uri? = null  // ✅ FIX: Declare imageUri

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityInformationprofileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        databaseReference = FirebaseDatabase.getInstance().getReference("USER")
        storageReference = FirebaseStorage.getInstance().getReference("profile_pics") // ✅ FIX: Initialize Firebase Storage Reference

        binding.btnback.setOnClickListener {
            val intent = Intent(this, SettingFragment::class.java)
            startActivity(intent)
        }

        if (uid.isNotEmpty()) {
            getUserData()
        }

        // ✅ Load existing profile picture
        loadProfileImage()

        binding.profileImageView.setOnClickListener {
            selectImage()
        }

        binding.uploadButton.setOnClickListener {
            uploadImage()
        }
    }

    private fun getUserData() {
        databaseReference.child(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(User::class.java)!!
                binding.tvname.text = user.username
                binding.tvroll.text = user.rollNo
                binding.tvemail.text = user.email
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@informationprofile, "Error fetching user data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            imageUri = data?.data
            binding.profileImageView.setImageURI(imageUri) // ✅ FIX: Correct reference to ImageView
        }
    }

    private fun uploadImage() {
        if (imageUri != null) {
            val fileRef = storageReference.child("$uid.jpg") // ✅ FIX: Store image under profile_pics/{uid}.jpg

            fileRef.putFile(imageUri!!)
                .addOnSuccessListener {
                    fileRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        databaseReference.child(uid).child("profileImage").setValue(imageUrl)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show()
                                loadProfileImage() // ✅ FIX: Load new profile pic after upload
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Upload Failed!", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Please Select an Image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadProfileImage() {
        databaseReference.child(uid).child("profileImage")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val imageUrl = snapshot.getValue(String::class.java)
                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(this@informationprofile) // ✅ FIX: Correct context reference
                            .load(imageUrl)
                            .into(binding.profileImageView)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@informationprofile, "Failed to load profile image", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
