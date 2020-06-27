package com.jemy.jemychat.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jemy.jemychat.R

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        setupSignUpClickListener()
    }

    private fun setupSignUpClickListener() {

    }
}