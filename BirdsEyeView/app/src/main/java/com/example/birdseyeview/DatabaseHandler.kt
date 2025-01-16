package com.example.birdseyeview
import com.google.firebase.database.FirebaseDatabase

class DatabaseHandler {

    //Write from Database
    val database = FirebaseDatabase.getInstance();
    val NewUserRef = database.getReference("RegisteredUser")
    fun SendsNewUserData(registeredFullName: String, registeredUsername: String, registeredEmail: String, registeredPassword: String){
        val NewUser = mapOf(
            "FullName" to registeredFullName,
            "Username" to registeredUsername,
            "Email" to registeredEmail,
            "Password" to registeredPassword
        )
        NewUserRef.push().setValue(NewUser)
    }
}