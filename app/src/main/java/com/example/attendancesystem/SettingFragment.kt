package com.example.attendancesystem

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SettingFragment : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth
    private var databaseReference: DatabaseReference? = null
    private var profileListener: ValueEventListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser

        val profileImageView = view.findViewById<ImageView>(R.id.profileImageView)
        val userFullName = view.findViewById<TextView>(R.id.profilename)

        user?.let {
            val uid = it.uid
            databaseReference = FirebaseDatabase.getInstance().getReference("USER").child(uid)

            // Load Profile Image
            loadProfileImage(uid, profileImageView)

            // Fetch User Data
            profileListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val fullName = dataSnapshot.child("username").getValue(String::class.java)
                    userFullName.text = fullName ?: "Unknown"
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            }
            databaseReference?.addValueEventListener(profileListener!!)
        }

        // Set up click listeners
        view.findViewById<TextView>(R.id.profile).setOnClickListener {
            startActivity(Intent(activity, informationprofile::class.java))
        }

        view.findViewById<TextView>(R.id.Caption2).setOnClickListener {
            startActivity(Intent(activity, GenerateQr::class.java))
        }

        view.findViewById<TextView>(R.id.Caption1).setOnClickListener {
            startActivity(Intent(activity, scanQr::class.java))
        }

        view.findViewById<TextView>(R.id.Caption5).setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(activity, SignInActivity::class.java))
        }

        // Social Media Links
        view.findViewById<ImageView>(R.id.facebookId).setOnClickListener {
            openUrl("https://www.facebook.com/profile.php?id=100029988158285")
        }

        view.findViewById<ImageView>(R.id.instagramId).setOnClickListener {
            openUrl("https://www.instagram.com/amann___06/")
        }

        return view
    }

    private fun loadProfileImage(uid: String, profileImageView: ImageView) {
        val userRef = FirebaseDatabase.getInstance().getReference("USER").child(uid).child("profileImage")

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val imageUrl = snapshot.getValue(String::class.java)
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(requireContext()) // ✅ Correct context
                        .load(imageUrl)
                        .into(profileImageView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load profile image", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        profileListener?.let { databaseReference?.removeEventListener(it) }
    }
}
