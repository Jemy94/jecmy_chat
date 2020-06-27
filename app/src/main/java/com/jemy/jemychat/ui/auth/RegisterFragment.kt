package com.jemy.jemychat.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.jemy.jemychat.R
import com.jemy.jemychat.model.User
import com.jemy.jemychat.ui.main.main.MainActivity
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment() {

    private var email = ""
    private var name = ""
    private var password = ""
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val databaseReference by lazy { FirebaseDatabase.getInstance().reference }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRegisterButtonClickListener()
    }

    private fun isValid(): Boolean {
        var isValid = true
        if (email.isBlank() || name.isBlank() || password.isBlank()) {
            Toast.makeText(activity!!, "Please fill the messing data", Toast.LENGTH_LONG).show()
            isValid = false
        } else if (password.length < 6) {
            Toast.makeText(activity!!, "Password must be more than 6 characters", Toast.LENGTH_LONG)
                .show()
            isValid = false
        }
        return isValid
    }

    private fun instantiateViews() {
        email = emailEditText.text.toString()
        password = passwordEditText.text.toString()
        name = nameEditText.text.toString()
    }

    private fun setupRegisterButtonClickListener() {
        registerButton.setOnClickListener {
            instantiateViews()
            register()
        }
    }

    private fun register() {
        if (isValid()) {
            registerProgressBar.visibility = View.VISIBLE
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
                    val currentUser =
                        User(name, "Hey there I'm using Jemy Chat", "default", "default")
                    databaseReference.child("Users").child(currentUserId)
                        .setValue(currentUser)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                registerProgressBar.visibility = View.INVISIBLE
                                val intent = Intent(activity!!, MainActivity::class.java)
                                activity!!.finish()
                                startActivity(intent)
                            }
                        }
                        .addOnFailureListener { error ->
                            Toast.makeText(activity!!, error.message, Toast.LENGTH_LONG).show()
                            registerProgressBar.visibility = View.INVISIBLE
                        }

                } else {
                    Toast.makeText(activity!!, "Something went wrong", Toast.LENGTH_LONG).show()
                }
            }
                .addOnFailureListener { error ->
                    Toast.makeText(activity!!, error.message, Toast.LENGTH_LONG).show()
                    registerProgressBar.visibility = View.INVISIBLE
                }
        }
    }
}