package com.example.rwu2.Login

import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.example.rwu2.Application_Init.Companion.K_USER_ACCOUNT
import com.example.rwu2.Application_Init.Companion.K_USER_NAME
import com.example.rwu2.Application_Init.Companion.K_USER_THUMB
import com.example.rwu2.Application_Init.Companion.sSharedPreferences
import com.example.rwu2.MainActivity
import com.example.rwu2.R
import com.example.rwu2.databinding.ActivityBeforeLoggedInBinding
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient


class Before_Logged_In : AppCompatActivity() {
    lateinit var editor: SharedPreferences.Editor

    lateinit var binding: ActivityBeforeLoggedInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeforeLoggedInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateKakaoLoginUi()
        init()
    }

    private fun init(){

        var keyHash = Utility.getKeyHash(this)
        Log.i("키해쉬",keyHash)
        //f3VaihUmG6nkeWqMzM7AJF1zi60=

//        var ani:Animation = AnimationUtils.loadAnimation(this,R.anim.anim)
//        binding.activityLoginKakaoLayout.startAnimation(ani)

        UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
            binding.activityLoginKakaoLayout.setOnClickListener {
                // 카카오톡이 설치되어있는지 확인하여 ture or false return 하는 함수
                if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)){
                    UserApiClient.instance.loginWithKakaoTalk(this){
                            token, error ->
                        if (error != null) {
                            Log.e(ContentValues.TAG, "로그인 실패", error)
                        }
                        else if (token != null) {
                            Log.i(ContentValues.TAG, "로그인 성공 ${token.accessToken}")
                        }
                        // 로그인 성공시 아래의 함수 호출
                        updateKakaoLoginUi()
                    }
                }else{
                    UserApiClient.instance.loginWithKakaoAccount(this){
                            token, error ->
                        if (error != null) {
                            Log.e(ContentValues.TAG, "로그인 실패", error)
                        }
                        else if (token != null) {
                            Log.i(ContentValues.TAG, "로그인 성공 ${token.accessToken}")
                        }
                        // 로그인 성공시 아래의 함수 호출
                        updateKakaoLoginUi()
                    }
                }
            }

        }


    }

    private fun updateKakaoLoginUi() {
        UserApiClient.instance.me { user, error ->
            if(user !=null){
                // 로그인이 되어있다면 닉네임, 프로필을 보여줌
                Log.d("myLog","invoke: id=" + user.id)
                Log.d("myLog","invoke: kakaoAccount=" + user.kakaoAccount)
                Log.d("myLog","invoke: hasSignedUp=" + user.hasSignedUp)
                Log.d("myLog","invoke: connectedAt=" + user.connectedAt)
                Log.d("myLog","invoke: properties=" + user.properties)

                user.kakaoAccount?.profile?.nickname
//                    user.kakaoAccount?.profile?.thumbnailImageUrl

//                Glide.with(this).load(user.kakaoAccount?.profile?.thumbnailImageUrl)
//                    .circleCrop() // 가져온 프로필 사진을 동그랗게 출력해주는 메소드
//                    .into(binding.profile)


                editor = sSharedPreferences.edit()
                editor.putString(K_USER_NAME,user?.kakaoAccount?.profile?.nickname)
                editor.putString(K_USER_ACCOUNT,user?.kakaoAccount?.email )
                //user?.kakaoAccount?.profile?.profileImageUrl
                editor.putString(K_USER_THUMB,user?.kakaoAccount?.profile?.thumbnailImageUrl)
                editor.apply()
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                finish()

            }else{
                // 로그인 버튼을 보여줌

                binding.apply {
//                    activityLoginMainText.text = null
//                    centerImg.setImageBitmap(null)
                }
            }
            return@me
        }
    }
}
