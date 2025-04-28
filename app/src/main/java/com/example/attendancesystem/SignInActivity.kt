package com.example.attendancesystem

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.attendancesystem.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Navigate to Sign Up page when clicking "Don't have an account?"
        binding.textView.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        binding.adminLoginbtn.setOnClickListener {
            startActivity(Intent(this,AdminLoginActivity::class.java))
        }

        // Sign In Button Click
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val pass = binding.passET.text.toString().trim()

            if (validateInput(email, pass)) {
                signInUser(email, pass)
            }
        }
    }

    // Function to validate email and password input
    private fun validateInput(email: String, pass: String): Boolean {
        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
            return false
        }

        // Ensure email follows "xyz@somaiya.edu" format
        val emailPattern = "^[a-zA-Z0-9._%+-]+@somaiya\\.edu$".toRegex()
        if (!email.matches(emailPattern)) {
            Toast.makeText(this, "Email must be in @somaiya.edu format!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    // Function to sign in user
    private fun signInUser(email: String, pass: String) {
        binding.button.isEnabled = false // Disable button to prevent multiple clicks

        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish() // Prevents user from going back to Sign In screen
            } else {
                val errorMessage = task.exception?.localizedMessage ?: "Authentication failed"
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                binding.button.isEnabled = true // Re-enable button on failure
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (firebaseAuth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Ensures user doesn't go back to Sign In screen after login
        }
    }
}
