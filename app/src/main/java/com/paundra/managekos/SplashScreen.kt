package com.paundra.managekos

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.*

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        val lottieAnimationView = findViewById<LottieAnimationView>(R.id.lottieAnimationView)

        // Mulai animasi
        lottieAnimationView.playAnimation()

        // Untuk menghentikan animasi
        lottieAnimationView.cancelAnimation()

        // Atur animasi untuk loop
        lottieAnimationView.repeatCount = LottieDrawable.INFINITE
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        Handler().postDelayed({
            // Intent untuk berpindah ke Activity lain, misalnya MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()  // Menutup SplashScreen setelah berpindah ke activity lain
        }, 2000) // 2000ms = 2 detik
    }
}