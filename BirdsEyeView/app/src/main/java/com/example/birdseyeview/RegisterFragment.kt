package com.example.birdseyeview

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class RegisterFragment : Fragment() {

    private lateinit var FullName: EditText
    private lateinit var Username: EditText
    private lateinit var Email: EditText
    private lateinit var Password: EditText
    private lateinit var ConfirmPassword: EditText
    private lateinit var dbHandler: DatabaseHandler
    private lateinit var RegisterButton : Button
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)
        dbHandler = DatabaseHandler()

        FullName = view.findViewById(R.id.fullnameEditText)
        Username = view.findViewById(R.id.usernameEditText)
        Email = view.findViewById(R.id.emailEditText)
        Password = view.findViewById(R.id.passwordEditText)
        ConfirmPassword = view.findViewById(R.id.confirmpasswordEditText)
        RegisterButton = view.findViewById(R.id.regButton)

        RegisterButton.setOnClickListener {
            Log.d("RegisterFragment", "Button clicked") // Add this log statement
            val RegisteredFullName = FullName.text.toString()
            val RegisteredUsername = Username.text.toString()
            val RegisteredEmail = Email.text.toString()
            val RegisteredPassword = Password.text.toString()
            val RegisteredConfirmPassword = ConfirmPassword.text.toString()

            if(RegisteredPassword.equals(RegisteredConfirmPassword)){
                dbHandler.SendsNewUserData(RegisteredFullName,RegisteredUsername,RegisteredEmail,RegisteredPassword)
                Toast.makeText(requireContext(), "Credentials Saved", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(requireContext(), "Password Do Not Match", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }
}