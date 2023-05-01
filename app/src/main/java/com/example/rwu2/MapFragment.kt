package com.example.rwu2

import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.rwu2.databinding.FragmentMapBinding
import com.example.teamproject.CustomDialog
import com.example.teamproject.CustomDialog2
import com.example.teamproject.DownloadDialog
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException
import java.util.ArrayList
import com.example.rwu2.Application_Init
import com.example.rwu2.Application_Init.Companion.K_USER_NAME
import com.example.rwu2.Application_Init.Companion.RECORDFB_ARRAY
import com.example.rwu2.Application_Init.Companion.PLANFB_ARRAY
import kotlinx.android.synthetic.main.row.*

class MapFragment : Fragment() {
    var binding:FragmentMapBinding ?=null
    lateinit var googleMap: GoogleMap
    lateinit var mainActivity: MainActivity
    lateinit var Plandb:DatabaseReference
    lateinit var Recorddb:DatabaseReference
    var uname = Application_Init.sSharedPreferences.getString(K_USER_NAME,null)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    //var mGoogleApiClient: GoogleApiClient? = null
    //var mCurrentLocationMarker: Marker? = null

    var loc = LatLng(37.554752, 126.970631)

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    val permissions = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (checkGPSProvider()) {
            getLastLocation()
        } else {
            setCurrentLocation(loc)
        }
    }

    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                    permissions.getOrDefault(
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        false
                    ) -> {
                getLastLocation()
            }
            else -> {
                setCurrentLocation(loc)
            }
        }
    }

    private fun checkGPSProvider(): Boolean {
        val locationManager = mainActivity.getSystemService(LOCATION_SERVICE) as LocationManager
        //getSystemService(LOCATION_SERVICE) as LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
    }


    private fun showGPSSetting() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage(
            "앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" +
                    "위치 설정을 허용하시겠습니까?"
        )
        builder.setPositiveButton("설정", DialogInterface.OnClickListener { dialog, id ->
            val GpsSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            activityResultLauncher.launch(GpsSettingIntent)
        })
        builder.setNegativeButton("취소",
            DialogInterface.OnClickListener { dialog, id ->
                dialog.dismiss()
                setCurrentLocation(loc)
            })
        builder.create().show()
    }

    private fun showPermissionRequestDlg() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("위치 서비스 제공")
        builder.setMessage(
            "앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" +
                    "기기의 위치를 제공하도록 설정하시겠습니까?"
        )
        builder.setPositiveButton("설정", DialogInterface.OnClickListener { dialog, id ->
            locationPermissionRequest.launch(permissions)
        })
        builder.setNegativeButton("취소",
            DialogInterface.OnClickListener { dialog, id ->
                dialog.dismiss()
                setCurrentLocation(loc)
            })
        builder.create().show()
    }

    private fun checkFineLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            //this -> requiredContext()
            requireContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun checkCoarseLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }




    private fun getLastLocation() : LatLng {
        when {
            checkFineLocationPermission() -> {
                if (!checkGPSProvider()) {
                    showGPSSetting()
                } else {
                    fusedLocationProviderClient.getCurrentLocation(LocationRequest.QUALITY_HIGH_ACCURACY,
                        object : CancellationToken() {
                            override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                                return CancellationTokenSource().token
                            }

                            override fun isCancellationRequested(): Boolean {
                                return false
                            }
                        }).addOnSuccessListener {
                        if (it != null) {
                            loc = LatLng(it.latitude, it.longitude)
                        }
                        setCurrentLocation(loc)
                    }
                }
            }

            checkCoarseLocationPermission() -> {
                fusedLocationProviderClient.getCurrentLocation(LocationRequest.QUALITY_BALANCED_POWER_ACCURACY,
                    object : CancellationToken() {
                        override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                            return CancellationTokenSource().token
                        }

                        override fun isCancellationRequested(): Boolean {
                            return false
                        }
                    }).addOnSuccessListener {
                    if (it != null) {
                        loc = LatLng(it.latitude, it.longitude)
                    }
                    setCurrentLocation(loc)
                }
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) -> {
                showPermissionRequestDlg()
            }
            else -> {
                locationPermissionRequest.launch(permissions)
            }
        }

        return loc
    }






    private fun setCurrentLocation(location: LatLng) {
        val option = MarkerOptions()
        option.position(loc)
        option.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        googleMap.addMarker(option)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f))
    }


    private fun moveMap() {
        when
        {
            checkFineLocationPermission()-> {
                if (!checkGPSProvider()) {
                    showGPSSetting()
                } else {
                    fusedLocationProviderClient.getCurrentLocation(
                        LocationRequest.QUALITY_HIGH_ACCURACY,
                        object : CancellationToken() {
                            override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                                return CancellationTokenSource().token
                            }
                            override fun isCancellationRequested(): Boolean {
                                return false
                            }
                        }).addOnSuccessListener {
                        val currentPos = LatLng(it.latitude, it.longitude)
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPos, 16.0f))
                        val option = MarkerOptions()
                        option.position(currentPos)
                        googleMap.addMarker(option)?.showInfoWindow()
                    }
                }
            }
        }
    }



    private fun initmap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        Plandb = Firebase.database.getReference("Dates/$uname/plans")
        Recorddb = Firebase.database.getReference("Dates/$uname/records")
        mapFragment.getMapAsync {
            /*
            binding?.searchButton?.setOnClickListener{
                searchLocation()
            }*/
            mainActivity.findViewById<TextView>(R.id.search_button).setOnClickListener{
                searchLocation()
            }
            googleMap = it
            initLocation()
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f))
            googleMap.setMinZoomPreference(10.0f)
            googleMap.setMaxZoomPreference(18.0f)
            val option = MarkerOptions()
            option.position(loc)
            option.icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)
            )
            option.title("선택한 곳")
            option.snippet("어떤 추억이 있나요?")

            for(a in RECORDFB_ARRAY)
            {
                Log.e("값 : ", "${a.lat} " + " ${a.lng}")
                option.position(LatLng(a.lat, a.lng))
                option.icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)
                )
                option.title(a.date)
                option.snippet(a.title)
                it.addMarker(option)
            }

            for(a in PLANFB_ARRAY)
            {
                Log.e("값 : ", "${a.lat} " + " ${a.lng}")
                option.position(LatLng(a.lat, a.lng))
                option.icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                )
                option.title(a.date)
                option.snippet(a.title)
                it.addMarker(option)
            }

            googleMap.setOnMapClickListener {
                /*
                binding?.cardView?.visibility = View.GONE
                */
                mainActivity.findViewById<LinearLayout>(R.id.cardView).visibility = View.GONE
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("잠시만요!")
                builder.setMessage("계속해서 추억을 기록하시겠어요?.")
                builder.setNegativeButton("네, 기록할래요!", DialogInterface.OnClickListener { dialog, id ->
                    val dialog = CustomDialog(it)
                    fragmentManager?.let { dialog.show(it,"CustomDialog") }
//                    recorditem = dialog.record!!
                })

                builder.setNeutralButton("일정 계획하기",
                    DialogInterface.OnClickListener { dialog, id ->
                        val dialog = CustomDialog2(it)
                        fragmentManager?.let { dialog.show(it,"CustomDialog2") }
//                        planitem = dialog.plan!!
                    })

                builder.setPositiveButton("아니요, 잘못 눌렀어요!",
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()
                    })
                builder.create().show()


                googleMap.setOnMarkerClickListener(object : GoogleMap.OnMarkerClickListener {
                    override fun onMarkerClick(p0: Marker): Boolean {
                        Log.e("record","${p0.position}")
                        createDialog(p0,p0.position)
                        return false
                    }
                })
            }
        }
    }

    /*
    override fun onMapReady(p0: GoogleMap) {
        var mMap = googleMap
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(
                    mainActivity, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
            ){
                buildGoogleApiClient()
                mMap!!.isMyLocationEnabled = true
            }
        }else{
            buildGoogleApiClient()
            mMap!!.isMyLocationEnabled = true
        }
    }



    protected fun buildGoogleApiClient(){
        val mGoogleApiClient = GoogleApiClient.Builder(requireContext()).
        addConnectionCallbacks().
        addOnConnectionFailedListener(this).
        addApi(LocationServices.API).build()

        mGoogleApiClient!!.connect()
    }*/

    fun updateMarker(latLng: LatLng){
        val option = MarkerOptions()
        option.icon(
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)
        )
        googleMap!!.addMarker(option.position(latLng))
    }

    private fun initLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        getLastLocation()
    }


    fun createDialog(marker : Marker ,latLng: LatLng){
//        val builder = AlertDialog.Builder(requireContext())
//        builder.setTitle("알림")
////        builder.setMessage("원하시는 항목을 골라주세요.")
        mainActivity.findViewById<LinearLayout>(R.id.cardView).visibility = View.VISIBLE
        mainActivity.findViewById<TextView>(R.id.place_name).text = "없음"
        mainActivity.findViewById<TextView>(R.id.details).text = "없음"
        for(FB in RECORDFB_ARRAY){
            if(FB.lat.toString()==latLng.latitude.toString() && FB.lng.toString()==latLng.longitude.toString()){
                Log.e("record","${latLng.toString()}")
                mainActivity.findViewById<TextView>(R.id.place_name).text = FB.title
                mainActivity.findViewById<TextView>(R.id.details).text = FB.text
            }
        }
        for(FB in PLANFB_ARRAY){
            if(FB.lat.toString()==latLng.latitude.toString() && FB.lng.toString()==latLng.longitude.toString()){
                Log.e("record","${latLng.toString()}")
                mainActivity.findViewById<TextView>(R.id.place_name).text = FB.title
                mainActivity.findViewById<TextView>(R.id.details).text = FB.text
            }
        }
        mainActivity.findViewById<Button>(R.id.remove_pin).setOnClickListener {
            var itRCFB = RECORDFB_ARRAY.iterator()
            var itPLFB = PLANFB_ARRAY.iterator()
            while(itRCFB.hasNext()){
                var i = itRCFB.next()
                var title = "${i.lat.toString()}_${i.lng.toString()}".replace(".","")
                if(i.lat.toString()==latLng.latitude.toString() && i.lng.toString()==latLng.longitude.toString()){
                    Recorddb.child(title).removeValue()
                    Log.e("title",title)
                    Log.e("name","$uname")
                    Log.e("record","remove")
                    Log.e("i",i.toString())
                    itRCFB.remove()
                }
            }
            while(itPLFB.hasNext()){
                var i = itPLFB.next()
                var title = "${i.lat.toString()}_${i.lng.toString()}".replace(".","")
                if(i.lat.toString()==latLng.latitude.toString() && i.lng.toString()==latLng.longitude.toString()){
                    Plandb.child(title).removeValue()
                    Log.e("plan","remove")
                    Log.e("i",i.toString())
                    itPLFB.remove()
                }
            }
            marker.remove()
            mainActivity.findViewById<LinearLayout>(R.id.cardView).visibility = View.GONE
        }

        mainActivity.findViewById<Button>(R.id.manage_pin).setOnClickListener {
            val dialog = CustomDialog(marker.position)
            fragmentManager?.let { dialog.show(it,"CustomDialog") }
            mainActivity.findViewById<LinearLayout>(R.id.cardView).visibility = View.GONE
        }

//        builder.setNegativeButton("추억 돌아보기", DialogInterface.OnClickListener { dialog, id ->
//            val dialog = DownloadDialog(marker.position)
////            fragmentManager?.let { dialog.show(it,"DownloadDialog") }
//            /*binding?.cardView?.visibility = View.VISIBLE*/
//            mainActivity.findViewById<LinearLayout>(R.id.cardView).visibility = View.VISIBLE
//        })
//
//        builder.setPositiveButton("추억 관리하기", DialogInterface.OnClickListener { dialog, id ->
//
//            builder.setTitle("알림")
//            builder.setMessage("원하시는 항목을 골라주세요.")
//
//            builder.setNegativeButton("추억 남기기", DialogInterface.OnClickListener { dialog, id ->
//                val dialog = CustomDialog(marker.position)
//                fragmentManager?.let { dialog.show(it,"CustomDialog") }
//            })
//
//            /*
//            builder.setPositiveButton("일정 계획하기", DialogInterface.OnClickListener { dialog, id ->
//                val dialog = CustomDialog2(loc)
//                fragmentManager?.let { dialog.show(it,"CustomDialog2") }
//            })*/
//
//            builder.setNeutralButton("삭제하기", DialogInterface.OnClickListener{dialog, id ->
//                marker.remove()
//                dialog.dismiss()
//            })
//
//            builder.create().show()
//        })
//
//        builder.setNeutralButton("취소", DialogInterface.OnClickListener{dialog, id ->
//            dialog.dismiss()
//        })
//
//        builder.create().show()
    }


    private fun searchLocation() {
        /*location = binding?.searchText?.text.toString().trim()*/
        var location = mainActivity.findViewById<EditText>(R.id.searchText).text.toString().trim()
        println(location)
        var addressList:List<Address>? = null

        if(location == null || location == ""){
            Toast.makeText(/*this*/requireContext(), "검색어를 입력하세요!", Toast.LENGTH_SHORT).show()
        }else{
            val geoCoder = Geocoder(requireContext())
            try{
                addressList = geoCoder.getFromLocationName(location, 5)
            }catch(e: IOException){
                e.printStackTrace()
            }

            if(addressList!!.isEmpty()){
                Toast.makeText(/*this*/requireContext(), "검색결과가 없습니다. 검색어를 확인해주세요!", Toast.LENGTH_SHORT).show()
            }
            else {
                val option = MarkerOptions()
                val address = addressList!![0]
                val latLng = LatLng(address.latitude, address.longitude)
                option.title("${address.getAddressLine(0)}")
                option.snippet(address.featureName)
                googleMap!!.addMarker(option.position(latLng).title(location))
                println(address)
                googleMap!!.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initmap()
        mainActivity.findViewById<ImageView>(R.id.button).setOnClickListener{
            println("clicked!")
            moveMap()
        }

        mainActivity.findViewById<LinearLayout>(R.id.cardView).visibility = View.GONE
        /*
        binding?.cardView?.visibility = View.GONE

        binding?.button?.setOnClickListener {
            println("clicked")
            moveMap()
        }*/
    }

}