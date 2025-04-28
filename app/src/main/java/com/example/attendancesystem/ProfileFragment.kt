package com.example.attendancesystem

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast


class ProfileFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val v=inflater.inflate(R.layout.fragment_profile, container, false)

        val eventsCard= v.findViewById<ImageView>(R.id.btnevent)

        eventsCard.setOnClickListener {
            val intent = Intent (activity, studentTimeTable::class.java)
            activity?.startActivity(intent)
        }

        return v
    }


}