package com.example.attendancesystem

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.attendancesystem.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mAuth: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        mAuth = FirebaseDatabase.getInstance().reference // Initialize database reference

        // Navigate to Sign In page when clicking on "Already have an account?" text
        binding.textView.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        // Sign Up Button Click
        binding.signUpButton.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val pass = binding.passET.text.toString().trim()
            val confirmPass = binding.confirmPassEt.text.toString().trim()
            val username = binding.nameEt.text.toString().trim()
            val rollNo = binding.rollEt.text.toString().trim()

            if (validateInput(email, pass, confirmPass, username, rollNo)) {
                registerUser(email, pass, username, rollNo)
            }
        }
    }

    // Function to validate input fields
    private fun validateInput(email: String, pass: String, confirmPass: String, username: String, rollNo: String): Boolean {
        if (email.isEmpty() || pass.isEmpty() || confirmPass.isEmpty() || username.isEmpty() || rollNo.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!isValidGmail(email)) {
            Toast.makeText(this, "Please enter a valid Student email address", Toast.LENGTH_SHORT).show()
            return false
        }

        if (rollNo.any { !it.isDigit() }) {  // Ensures roll number contains only digits
            Toast.makeText(this, "Roll Number should contain only digits!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (pass.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (pass != confirmPass) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    // Function to register user
    private fun registerUser(email: String, pass: String, username: String, rollNo: String) {
        binding.signUpButton.isEnabled = false  // Disable button to prevent multiple clicks

        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = firebaseAuth.uid!!
                addUserToDatabase(username, email, rollNo, uid)

                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                finish() // Prevents navigating back to SignUpActivity
            } else {
                val errorMessage = task.exception?.localizedMessage ?: "Registration failed"
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                binding.signUpButton.isEnabled = true // Re-enable button on failure
            }
        }
    }

    private fun isValidGmail(email: String): Boolean {
        val gmailRegex = Regex("^[a-zA-Z0-9._%+-]+@somaiya\\.edu$")
        return email.matches(gmailRegex) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Function to store user data in Firebase Realtime Database
    private fun addUserToDatabase(username: String, email: String, rollNo: String, uid: String) {
        val user = User(username, email, rollNo, uid)
        mAuth.child("USER").child(uid).setValue(user)
    }
}
