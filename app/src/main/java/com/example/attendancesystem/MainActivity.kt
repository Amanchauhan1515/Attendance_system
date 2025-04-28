

package com.example.attendancesystem

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.attendancesystem.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.qrcode.QRCodeWriter
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)




        replaceFragment(HomeFragment())


        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId){

                R.id.btnhome -> replaceFragment(HomeFragment())
                R.id.btnprofile -> replaceFragment(ProfileFragment())
                R.id.btnnotification -> replaceFragment(Subject_fragment())
                R.id.btnsetting -> replaceFragment(SettingFragment())


                else ->{



                }

            }

            true

        }

        firebaseAuth = FirebaseAuth.getInstance()


        val logout = findViewById<ImageView>(R.id.logoutbutton)
        logout.setOnClickListener {
            startActivity(Intent(this, scanQr::class.java))

        }

        val btnnotification = findViewById<ImageView>(R.id.tvnotification)
        btnnotification.setOnClickListener {
            startActivity(Intent(this, activityNotification::class.java))

        }
    }
    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()


    }
    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Exit App")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") { _, _ ->
                finishAffinity()
                System.exit(0)// Exit the app
            }
            .setNegativeButton("No", null) // Dismiss dialog
            .show()
    }

}


