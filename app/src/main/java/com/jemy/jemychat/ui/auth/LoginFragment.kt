package com.jemy.jemychat.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.jemy.jemychat.R
import com.jemy.jemychat.ui.main.main.MainActivity
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment() {

    private var email = ""
    private var password = ""
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signUpTextView.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_login_to_navigation_register)
        }
        setupLoginButtonClickListener()

    }

    private fun isValid(): Boolean {
        var isValid = true
        if (email.isBlank() || password.isBlank()) {
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
        email = emailLoginEditText.text.toString()
        password = passwordLoginEditText.text.toString()
    }

    private fun setupLoginButtonClickListener() {
        signInButton.setOnClickListener {
            instantiateViews()
            login()
        }
    }

    private fun login() {
        if (isValid()) {
            loginProgressBar.visibility = View.VISIBLE
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    loginProgressBar.visibility = View.INVISIBLE
                    val intent = Intent(activity!!, MainActivity::class.java)
                    activity!!.finish()
                    startActivity(intent)
                } else {
                    Toast.makeText(activity!!, "Something went wrong", Toast.LENGTH_LONG).show()
                }
            }
                .addOnFailureListener { error ->
                    Toast.makeText(activity!!, error.message, Toast.LENGTH_LONG).show()
                    loginProgressBar.visibility = View.INVISIBLE
                }
        }
    }
}