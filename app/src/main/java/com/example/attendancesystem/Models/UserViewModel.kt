package com.example.attendancesystem.Models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.attendancesystem.Repository.UserRepository

class UserViewModel : ViewModel(){ private val repository : UserRepository
    private val _allUsers = MutableLiveData<List<Student>>()
    val allUsers : LiveData<List<Student>> = _allUsers


    init {

        repository = UserRepository().getInstance()
        repository.loadUsers(_allUsers)

    }
}