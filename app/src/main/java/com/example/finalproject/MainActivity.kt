package com.example.finalproject

import SignUpActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            // No user is signed in
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // User is signed in
            // Continue with your MainActivity code
        }

    }
}