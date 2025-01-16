package com.example.birdseyeview

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment()
{
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var loginUsername: TextView
    private lateinit var loginButton: Button
    private lateinit var loginPassword: TextView
    private lateinit var dbHandler: DatabaseHandler
    private val TAG = "LoginFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        dbHandler = DatabaseHandler()
        //Variable
        loginButton = view.findViewById(R.id.LoginBtn)
        //Login Button
        loginButton.setOnClickListener {
            Log.d("RegisterFragment", "Button clicked")
            //captures user input
            loginUsername = view.findViewById(R.id.usernameEditText)
            loginPassword = view.findViewById(R.id.passwordEditText)
            val UserUsername = loginUsername.text.toString()
            val UserPassword = loginPassword.text.toString()
            //Displays to user
            //Toast.makeText(requireContext(), "Login Succeeded: $UserUsername", Toast.LENGTH_SHORT).show()

            // Read from the database
            dbHandler.NewUserRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (userSnapshot in snapshot.children) {
                        val username = userSnapshot.child("Username").getValue(String::class.java)
                        val password = userSnapshot.child("Password").getValue(String::class.java)
                        if (username == UserUsername && password == UserPassword) {
                            Log.d(TAG, "Login Succeeded: $UserUsername")
                            Toast.makeText(requireContext(), "Login Suceeded: $UserUsername", Toast.LENGTH_SHORT).show()
                            val nextFragment = HomeFragment() // Replace with your second fragment class
                            val transaction = parentFragmentManager.beginTransaction()
                            transaction.replace(R.id.LoginContainer, nextFragment) // Replace 'fragmentContainer' with your actual container ID
                            transaction.addToBackStack(null)
                            transaction.commit()
                            return
                        }
                    }
                    // If the loop completes without finding a matching user, it means the login failed
                    Log.d(TAG, "Login Failed: $UserUsername")
                    Toast.makeText(requireContext(), "Login Failed: $UserUsername", Toast.LENGTH_SHORT).show()
                    loginUsername.setText("")
                    loginPassword.setText("")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "Login Failed: $UserUsername")
                }
            })
        }
        val nextPageLink = view.findViewById<TextView>(R.id.createNewAccount)
        nextPageLink.setOnClickListener {
            val intent = Intent(requireContext(), RegisterFragment::class.java)
            startActivity(intent)
        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}