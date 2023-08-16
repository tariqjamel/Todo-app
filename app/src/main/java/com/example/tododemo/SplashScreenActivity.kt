package com.example.tododemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val tvSplashScreen = findViewById<ImageView>(R.id.tvSplashScreen)
        tvSplashScreen.alpha = 0f
        tvSplashScreen.animate().setDuration(1000).alpha(1f).withEndAction{
            val intent = Intent(this,loginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()
        }
    }
}