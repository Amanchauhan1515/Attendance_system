package com.example.attendancesystem

class Attendance {
    var userName: String? = null

    var Roll: String? = null
    var profileImageUrl: String? = null
    var timestamp: String? = null



    constructor() {}


    constructor(userName: String, Roll: String?,profileImageUrl: String?,timestamp: String?){
        this.userName = userName

        this.Roll = Roll
        this.profileImageUrl = profileImageUrl
        this.timestamp = timestamp
    }
}


