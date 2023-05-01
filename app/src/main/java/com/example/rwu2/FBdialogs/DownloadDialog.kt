package com.example.teamproject

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.PermissionChecker
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.rwu2.Application_Init
import com.example.rwu2.Application_Init.Companion.K_USER_NAME
import com.example.rwu2.Application_Init.Companion.RECORDFB_ARRAY
import com.example.rwu2.RecordFromDB
import com.example.rwu2.databinding.RowBinding
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import org.w3c.dom.Text
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest
import kotlin.collections.ArrayList

class DownloadDialog(loc: LatLng) : DialogFragment() {
    lateinit var binding: RowBinding
    var lng = loc.longitude
    var lat = loc.latitude
    lateinit var rdb:DatabaseReference
    var uname = Application_Init.sSharedPreferences.getString(K_USER_NAME,null)
    private val FBarray = ArrayList<RecordFromDB>()
    var fbStorage : FirebaseStorage? = null
    var uriPhoto : Uri? = null
    private var num:Int=0
    lateinit var cal : String
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = RowBinding.inflate(inflater, container, false)
        fbStorage = FirebaseStorage.getInstance()
        rdb = Firebase.database.getReference("Dates/$uname/records")
        var rds = fbStorage!!.reference
        binding.apply {
            confirmButton.setOnClickListener{
                rdb.addValueEventListener(object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (data in snapshot.children){
                            val result = data.getValue(RecordFromDB::class.java)
                            if (result != null) {
                                FBarray.add(result)
                            }
                        }

                        Log.e("0","0")
                        for(FB in FBarray){
                            if(FB.lat.toString()==lat.toString() && FB.lng.toString()==lng.toString()){
                                posText.text = FB.title
                                memoText.text = FB.text
                                dateText.text = FB.date

//                                rds.child("$uname/image${FB.lat}_${FB.lng}.png").downloadUrl.addOnSuccessListener { Uri->
//                                    val imageURi = Uri.toString()
//                                    Log.e("imageUrl","${FB.imgURL}")
//                                    context?.let { it1 -> Glide.with(it1).load(imageURi).override(800,800).into(FileImage) }
//                                    Log.e("imgUrl","${imageURi}")
//                                }
                                context?.let { it1 -> Glide.with(it1).load(FB.imgURL).override(800,800).into(FileImage) }
//                                Log.e("imageUrl","${FB.imgURL}")
//                                context?.let { it1 -> Glide.with(it1).load(FB.imgURL).override(800,800).into(FileImage)}
//                                Log.e("imgUrl","${RECORDFB_ARRAY[0].lat}_${RECORDFB_ARRAY[0].lng}")

                            }
                            else{
                                Log.i("error!","error!")
                            }
                        }


                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("error","error")
                    }
                })

            }

            cancelButton.setOnClickListener{
                dismiss()
            }
            posButton.setOnClickListener{
                posText.visibility = View.VISIBLE
                FileImage.visibility = View.GONE
                memoText.visibility = View.GONE
                dateText.visibility = View.GONE
            }
            fileButton.setOnClickListener{
                posText.visibility = View.GONE
                FileImage.visibility = View.VISIBLE
                memoText.visibility = View.GONE
                dateText.visibility = View.GONE
            }
            memoButton.setOnClickListener{
                posText.visibility = View.GONE
                FileImage.visibility = View.GONE
                memoText.visibility = View.VISIBLE
                dateText.visibility = View.GONE
            }
            calButton.setOnClickListener{
                posText.visibility = View.GONE
                FileImage.visibility = View.GONE
                memoText.visibility = View.GONE
                dateText.visibility = View.VISIBLE
            }
        }

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        //context?.dialogFragmentResize(this,0.9f,0.9f)
    }

//    fun Context.dialogFragmentResize(dialogFragment: DialogFragment, width: Float, height: Float) {
//        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
//
//        if (Build.VERSION.SDK_INT < 30) {
//
//            val display = windowManager.defaultDisplay
//            val size = Point()
//
//            display.getSize(size)
//
//            val window = dialogFragment.dialog?.window
//
//            val x = (size.x * width).toInt()
//            val y = (size.y * height).toInt()
//            window?.setLayout(x, y)
//
//        } else {
//
//            val rect = windowManager.currentWindowMetrics.bounds
//
//            val window = dialogFragment.dialog?.window
//
//            val x = (rect.width() * width).toInt()
//            val y = (rect.height() * height).toInt()
//
//            window?.setLayout(x, y)
//        }
//    }

    interface ButtonClickListener{
        fun onClicked(text:String)
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnclickListener(listener:ButtonClickListener){
        onClickListener = listener
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==2000){
            uriPhoto = data?.data
        }
    }


}