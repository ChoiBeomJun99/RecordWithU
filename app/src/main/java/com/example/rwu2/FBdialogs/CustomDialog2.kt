package com.example.teamproject

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
import com.example.rwu2.Application_Init.Companion.PLANFB_ARRAY
import com.example.rwu2.databinding.Popup2Binding
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.popup.*
import java.text.SimpleDateFormat
import java.util.*

class CustomDialog2(loc: LatLng) : DialogFragment() {
    var plan:PlanFromDB?=null
    var lng = loc.longitude
    var lat = loc.latitude
    var uname = Application_Init.sSharedPreferences.getString(K_USER_NAME,null)
    lateinit var binding: Popup2Binding
    lateinit var rdb:DatabaseReference
    private var num:Int=0
    private var title:String?=null
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
        binding = Popup2Binding.inflate(inflater, container, false)
        rdb = Firebase.database.getReference("Dates/$uname/plans")
        binding.apply {
            confirmButton2.setOnClickListener{
                title = "${lat.toString()}_${lng.toString()}".replace(".","")
                val item = PlanFromDB(
                    posEdit2.text.toString(),
                    "none",
                    lat,
                    lng,
                    TextEdit2.text.toString(),
                    datePicker2.year.toString() + "/" +
                            (datePicker2.month + 1).toString() + "/" +
                            datePicker2.dayOfMonth.toString()
                )
                PLANFB_ARRAY.add(item)
                plan=item
                rdb.child(title!!).setValue(item)
                mapFragment?.updateMarker(LatLng(lat,lng))
                dismiss()
            }

            cancelButton2.setOnClickListener{
                dismiss()
            }
            posButton2.setOnClickListener{
                posEdit2.visibility = View.VISIBLE
                TextEdit2.visibility = View.GONE
                datePicker2.visibility = View.GONE
            }
            memoButton2.setOnClickListener{
                posEdit2.visibility = View.GONE
                TextEdit2.visibility = View.VISIBLE
                datePicker2.visibility = View.GONE
            }
            calButton2.setOnClickListener{
                posEdit2.visibility = View.GONE
                TextEdit2.visibility = View.GONE
                datePicker2.visibility = View.VISIBLE
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


}