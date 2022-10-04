package com.example.dolarxreal.activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.example.dolarxreal.R
import com.example.dolarxreal.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity()
{
    private lateinit var bind : ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(bind.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            window.insetsController!!.hide(android.R.style.Theme_NoTitleBar_Fullscreen)

        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
        showGIF()
        Handler(Looper.getMainLooper()).postDelayed({openMainActivity()}, 6000)
    }

    private fun openMainActivity() {
    startActivity(Intent(this, MainActivity::class.java))
    finish()

    }
    private fun showGIF(){Glide.with(this).load(R.drawable.gif).into(bind.imgSplash)}

}