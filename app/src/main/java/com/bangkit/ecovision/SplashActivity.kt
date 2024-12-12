package com.bangkit.ecovision

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // Ensure edge-to-edge layout

        setContentView(R.layout.activity_splash)

        // Handle system bars (status bar and navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Transition to MainActivity after 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            // Start the MainActivity
            startActivity(Intent(this, MainActivity::class.java))

            // Close the SplashActivity so the user can't return to it
            finish()
        }, 3000)  // 3 seconds delay for splash screen
    }
}