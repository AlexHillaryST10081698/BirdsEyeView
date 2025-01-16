package com.example.birdseyeview

import Sighting
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class DatabaseHelper {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().reference // Initialize the DatabaseReference
    val database = FirebaseDatabase.getInstance();
    val SightingsRef = database.getReference("Sightings");
    fun registerUser(
        fullName: String,
        username: String,
        email: String,
        password: String,
        callback: (Boolean) -> Unit
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId =
                        firebaseAuth.currentUser?.uid // Get the UID of the newly registered user
                    val userData = HashMap<String, Any>() // Store user data in a HashMap
                    userData["fullName"] = fullName
                    userData["username"] = username
                    userData["email"] = email

                    userId?.let {
                        databaseReference.child("users").child(it).setValue(userData)
                    }

                    callback(true) // Notify that registration was successful
                } else {
                    Log.e("RegistrationError", task.exception?.message ?: "Registration failed")
                    callback(false)
                }
            }
    }

    // Function for user login using Firebase Authentication
    fun loginUser(email: String, password: String, callback: (Boolean) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
    }

    // Function to store sighting information in Firebase
    fun addSightingData(
        speciesId: String,
        birdingHotspotName: String,
        birdingHotspotLatitude: String,
        birdingHotspotLongitude: String,
        speciesName: String,
        speciesDescription: String,
        speciesGender: String,
        speciesTypeSighting: String,
        speciesCaptureDate: String,
        callback: (Boolean) -> Unit
    ) {
        val sightingData = mapOf(
            "speciesId" to speciesId,
            "birdingHotspotName" to birdingHotspotName,
            "birdingHotspotLatitude" to birdingHotspotLatitude,
            "birdingHotspotLongitude" to birdingHotspotLongitude,
            "speciesName" to speciesName,
            "speciesDescription" to speciesDescription,
            "speciesGender" to speciesGender,
            "speciesTypeSighting" to speciesTypeSighting,
            "speciesCaptureDate" to speciesCaptureDate
        )

        // Log the sighting data before writing
        Log.d("DatabaseHelper", "Sighting data before writing: $sightingData")

        //SightingsRef.push().setValue(sightingData)

        databaseReference.child("sightings").push().setValue(sightingData)
            .addOnSuccessListener {
                // If the data was saved successfully, invoke the callback with true
                Log.d("DatabaseHelper", "Sighting data saved successfully")
                callback(true)
            }
            .addOnFailureListener { e ->
                // If an error occurred while saving, log the error and invoke the callback with false
                Log.e("DatabaseHelper", "Error saving sighting data: ${e.message}")
                callback(false)
            }
    }
    public fun getSightingsFromFirebase(callback: (List<Sighting>) -> Unit) {
        val sightings = mutableListOf<Sighting>()

        // Replace "sightings" with your actual Firebase node for sightings
        val sightingsRef = FirebaseDatabase.getInstance().getReference("sightings")

        sightingsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val sighting = snapshot.getValue(Sighting::class.java)
                    sighting?.let { sightings.add(it) }
                }

                callback(sightings)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("MapFragment", "Error getting sightings from Firebase: ${databaseError.message}")
            }
        })
    }
}