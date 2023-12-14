package com.example.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var authStateListener : FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                // User is signed in
                val newFragment = HomeScreenFragment()
                val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.fragment_container, newFragment)
                transaction.addToBackStack(null)

                // Commit the transaction
                transaction.commit()
            } else {
                // User is signed out
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener)
    }

    public override fun onStop() {
        super.onStop()
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener)
        }
    }
}
