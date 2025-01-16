package com.example.birdseyeview

import DatabaseHandler
import MapFragment
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

class LoginFragment : Fragment() {
    private lateinit var loginUsername: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginButton: Button
    private lateinit var createAccountLink: TextView
    private lateinit var dbHandler: DatabaseHandler
    private lateinit var dbHelper:DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        dbHandler = DatabaseHandler(requireContext())

        dbHelper = DatabaseHelper()
        loginUsername = view.findViewById(R.id.usernameEditText)
        loginPassword = view.findViewById(R.id.passwordEditText)
        loginButton = view.findViewById(R.id.LoginBtn)
        createAccountLink = view.findViewById(R.id.createNewAccount)

        loginButton.setOnClickListener {
            val userUsername = loginUsername.text.toString()
            val userPassword = loginPassword.text.toString()

            if (userUsername.isEmpty() || userPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter both username and password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Firebase login using email and password
            dbHelper.loginUser(userUsername, userPassword) { success ->
                if (success) {
                    // Successful login
                    Toast.makeText(requireContext(), "Login Successful", Toast.LENGTH_SHORT).show()
                    // Handle navigation or other actions upon successful login
                    val homeFragment = SettingsFragment() // Replace with your main content fragment
                    val transaction = parentFragmentManager.beginTransaction()
                    transaction.replace(
                        R.id.drawer_layout,
                        homeFragment
                    ) // Replace 'drawer_layout' with your actual container ID
                    transaction.addToBackStack(null)
                    transaction.commit()
                } else {
                    // Login failed
                    Toast.makeText(
                        requireContext(),
                        "Login Failed. Invalid username or password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        createAccountLink.setOnClickListener {
            navigateToRegisterFragment()
        }

        return view
    }

    @SuppressLint("Range")
    private fun isLoginValid(username: String, password: String): Boolean {
        val cursor = dbHandler.getAllUsers()
        if (cursor.moveToFirst()) {
            do {
                val dbUsername = cursor.getString(cursor.getColumnIndex(DatabaseHandler.COLUMN_USERNAME))
                val dbPassword = cursor.getString(cursor.getColumnIndex(DatabaseHandler.COLUMN_PASSWORD))
                if (username == dbUsername && password == dbPassword) {
                    cursor.close()
                    return true
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        return false
    }


    private fun navigateToHomeFragment() {
        val homeFragment = HomeFragment() // Replace with your main content fragment
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.drawer_layout, homeFragment) // Replace 'drawer_layout' with your actual container ID
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun navigateToRegisterFragment() {
        val nextFragment = RegisterFragment() // Replace with your second fragment class
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.drawer_layout, nextFragment) // Replace 'fragmentContainer' with your actual container ID
        transaction.addToBackStack(null)
        transaction.commit()


    }
}

