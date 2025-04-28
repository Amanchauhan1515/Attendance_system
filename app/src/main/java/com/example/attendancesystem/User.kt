package com.example.attendancesystem

class User {
    var username: String? =null
    var email: String? =null
    var rollNo :String? =null
    var uid: String? =null
    constructor(){}
    constructor(username : String?,email: String?,rollNo :String?,uid:String?){
        this.username=username
        this.email=email
        this.rollNo=rollNo
        this.uid=uid

    }
}