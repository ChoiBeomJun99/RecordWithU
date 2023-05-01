package com.example.rwu2.Splash

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.rwu2.Login.Before_Logged_In
import com.example.rwu2.MainActivity
import com.example.rwu2.R
import com.example.rwu2.databinding.ActivitySplashBinding
import com.kakao.sdk.user.UserApiClient

class Splash : AppCompatActivity() {
    lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)



        UserApiClient.instance.me { user, error ->
            if(user !=null){
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                    finish()
                },3000)

            }else{
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, Before_Logged_In::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                    finish()
                },3000)
            }
        }

    }

}