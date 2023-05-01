package com.example.rwu2

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.common.KakaoSdk

class Application_Init : Application() {
    lateinit var rdb: DatabaseReference
    lateinit var rdb2:DatabaseReference
    // 코틀린의 전역변수 문법
    companion object {
        // 만들어져있는 SharedPreferences 를 사용해야합니다. 재생성하지 않도록 유념해주세요
        // 값 불러오기 예시 입니다 sSharedPreferences.getString(K_USER_ACCOUNT,null)
        lateinit var sSharedPreferences: SharedPreferences
        val K_USER_NAME = "kakaoUserName"
        val K_USER_ACCOUNT = "kakaoUserAccount"
        val K_USER_THUMB = "kakaoUserThumbnailImageUrl"
        val RECORDFB_ARRAY = ArrayList<RecordFromDB>()
        val PLANFB_ARRAY = ArrayList<PlanFromDB>()

    }

    // 앱이 처음 생성되는 순간, SharedPreference 생성 및 사용된 SDK 들 초기화
    override fun onCreate() {
        super.onCreate()
        sSharedPreferences =
            applicationContext.getSharedPreferences("SOFTSQUARED_TEMPLATE_APP", MODE_PRIVATE)

        // KakaoSDK 초기화
        KakaoSdk.init(this,"c3dd083780fdd8c0892d72c3db1d3821")
        var uname = sSharedPreferences.getString(K_USER_NAME,null)
        rdb = Firebase.database.getReference("Dates/$uname/records")
        rdb2 = Firebase.database.getReference("Dates/$uname/plans")
        rdb.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children){
                    val result = data.getValue(RecordFromDB::class.java)
                    if (result != null) {
                        RECORDFB_ARRAY.add(result)
                    }
                }

                Log.e("0","0")
                for(FB in RECORDFB_ARRAY){
                    Log.e("lat","${FB.lat}")
                    Log.e("lng","${FB.lng}")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("error","error")
            }
        })
        rdb2.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children){
                    val result = data.getValue(PlanFromDB::class.java)
                    if (result != null) {
                        PLANFB_ARRAY.add(result)
                    }
                }

                Log.e("0","0")
                for(FB in PLANFB_ARRAY){
                    Log.e("lat","${FB.lat}")
                    Log.e("lng","${FB.lng}")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("error","error")
            }
        })
    }

}