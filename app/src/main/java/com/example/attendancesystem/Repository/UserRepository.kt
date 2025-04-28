package com.example.attendancesystem.Repository

import androidx.lifecycle.MutableLiveData
import com.example.attendancesystem.Models.Student
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

class UserRepository{
    val calendar= Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val date = "$year-$month-$day"
     private val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().getReference("ATTENDANCE").child(date)

     @Volatile private var INSTANCE : UserRepository ?= null

     fun getInstance() : UserRepository{
         return INSTANCE ?: synchronized(this){

             val instance = UserRepository()
             INSTANCE = instance
             instance
         }


     }


     fun loadUsers(userList : MutableLiveData<List<Student>>){

         databaseReference.addValueEventListener(object : ValueEventListener {
             override fun onDataChange(snapshot: DataSnapshot) {

                 try {

                     val _userList : List<Student> = snapshot.children.map { dataSnapshot ->

                         dataSnapshot.getValue(Student::class.java)!!

                     }

                     userList.postValue(_userList)

                 }catch (e : Exception){


                 }


             }

             override fun onCancelled(error: DatabaseError) {
                 TODO("Not yet implemented")
             }


         })


     }

 }
