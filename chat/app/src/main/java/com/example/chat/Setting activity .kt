package com.example.chatapp

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val logoutButton = findViewById<Button>(R.id.logoutButton)

        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            finish()
        }
    }
}