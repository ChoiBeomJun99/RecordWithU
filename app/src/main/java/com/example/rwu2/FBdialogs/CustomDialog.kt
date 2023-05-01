package com.example.teamproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.example.rwu2.*
import com.example.rwu2.Application_Init.Companion.K_USER_NAME
import com.example.rwu2.Application_Init.Companion.RECORDFB_ARRAY
import com.example.rwu2.databinding.PopupBinding
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.popup2.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule


class CustomDialog(loc: LatLng) : DialogFragment() {
    private var lng = loc.longitude
    private var lat = loc.latitude
    var uname = Application_Init.sSharedPreferences.getString(K_USER_NAME, null)
    lateinit var binding: PopupBinding
    lateinit var rdb: DatabaseReference
    var storageRef : StorageReference? = null
    var fbStorage: FirebaseStorage? = null
    var uriPhoto: Uri? = null
    var downlodUri:Uri?=null
    var title:String?=null
    var record:RecordFromDB?=null
    private var num: Int = 0
    lateinit var cal: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = PopupBinding.inflate(inflater, container, false)
        rdb = Firebase.database.getReference("Dates/$uname/records")
        fbStorage = FirebaseStorage.getInstance()
        binding.apply {
            confirmButton.setOnClickListener {
                uploadCheck.visibility = View.GONE
                val fileName = "image${lat}_${lng}.png"
                storageRef = fbStorage?.reference?.child(uname.toString())?.child(fileName)
                if(uriPhoto!=null) {
                    Log.e("e","uriphoto nullX")
                    storageRef?.putFile(uriPhoto!!)
                        ?.addOnFailureListener { Log.i("업로드 실패", "") }
                        ?.addOnSuccessListener {
                            Log.i("업로드 성공", "")
                            storageRef?.downloadUrl?.addOnCompleteListener {
                                downlodUri = it.result
                                title = "${lat.toString()}_${lng.toString()}".replace(".","")
                                val item = RecordFromDB(
                                    posEdit.text.toString(),
                                    downlodUri.toString(),
                                    lat,
                                    lng,
                                    TextEdit.text.toString(),
                                    datePicker.year.toString() + "/" +
                                            (datePicker.month + 1).toString() + "/" +
                                            datePicker.dayOfMonth.toString()
                                )
                                RECORDFB_ARRAY.add(item)
                                record=item
                                rdb.child(title!!).setValue(item)
                                mapFragment?.updateMarker(LatLng(lat,lng))
                                dismiss()
                            }
                        }
                }
                else{
                    Log.e("e","uriphoto null O")
                    title = "${lat.toString()}_${lng.toString()}".replace(".","")
                    val item = RecordFromDB(
                        posEdit.text.toString(),
                        "none",
                        lat,
                        lng,
                        TextEdit.text.toString(),
                        datePicker.year.toString() + "/" +
                                (datePicker.month + 1).toString() + "/" +
                                datePicker.dayOfMonth.toString()
                    )
                    RECORDFB_ARRAY.add(item)
                    record=item
                    rdb.child(title!!).setValue(item)
                    mapFragment?.updateMarker(LatLng(lat,lng))

                    dismiss()
                }
            }

            cancelButton.setOnClickListener {

                dismiss()
            }
            FileEditButton.setOnClickListener {
                var photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                Timer().schedule(2000){
                    uploadCheck.visibility = View.VISIBLE
                }
                startActivityForResult(photoPickerIntent, 0)
            }
            posButton.setOnClickListener {
                posEdit.visibility = View.VISIBLE
                FileEditButton.visibility = View.GONE
                TextEdit.visibility = View.GONE
                datePicker.visibility = View.GONE
            }
            fileButton.setOnClickListener {
                posEdit.visibility = View.GONE
                FileEditButton.visibility = View.VISIBLE
                TextEdit.visibility = View.GONE
                datePicker.visibility = View.GONE
            }
            memoButton.setOnClickListener {
                posEdit.visibility = View.GONE
                FileEditButton.visibility = View.GONE
                TextEdit.visibility = View.VISIBLE
                datePicker.visibility = View.GONE
            }
            calButton.setOnClickListener {
                posEdit.visibility = View.GONE
                FileEditButton.visibility = View.GONE
                TextEdit.visibility = View.GONE
                datePicker.visibility = View.VISIBLE
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
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
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

    interface ButtonClickListener {
        fun onClicked(text: String)
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnclickListener(listener: ButtonClickListener) {
        onClickListener = listener
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            uriPhoto = data?.data
        }
    }




}