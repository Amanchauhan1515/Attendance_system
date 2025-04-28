package com.example.attendancesystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class AdminLoginActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button

    private val adminEmail = "admin@gmail.com"
    private val adminPassword = "admin123"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)

        email = findViewById(R.id.etAdminEmail)
        password = findViewById(R.id.etAdminPassword)
        loginButton = findViewById(R.id.btnAdminLogin)

        loginButton.setOnClickListener {
            val inputEmail = email.text.toString().trim()
            val inputPassword = password.text.toString().trim()

            if (inputEmail == adminEmail && inputPassword == adminPassword) {
                val intent = Intent(this,AttendanceFilterActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
