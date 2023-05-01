package com.example.rwu2

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.example.rwu2.Application_Init.Companion.PLANFB_ARRAY
import com.example.rwu2.Application_Init.Companion.RECORDFB_ARRAY
import com.example.rwu2.databinding.FragmentPlusBinding
import com.example.rwu2.recordView.PlusViewPagerAdapter
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import com.michalsvec.singlerowcalendar.utils.DateUtils.getDates
import kotlinx.android.synthetic.main.calendar_item.view.*
import kotlinx.android.synthetic.main.fragment_plus.*
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList


class PlusFragment : Fragment() {
    private val MIN_SCALE = 0.85f // 뷰가 몇퍼센트로 줄어들 것인지
    private val MIN_ALPHA = 0.5f // 어두워지는 정도를 나타낸 듯 하다.
    private val calendar = Calendar.getInstance()
    private var currentMonth = 0


    var recordArr = arrayListOf<RecordFromDB>()
    var planArr = arrayListOf<PlanFromDB>()
    var planMap = mutableMapOf<String,PlanFromDB>() //<날짜, PlanFromDB>
    var binding:FragmentPlusBinding ?= null // 메모리 누수 가능성 때문에 null 값이 가능한 변수로 선언해주는것을 주의하자
    lateinit var pvpAdapter:PlusViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPlusBinding.inflate(layoutInflater,container,false)
        return binding!!.root
    }

    // 프레그먼트 객체가 실체화(inflate)된 후에 호출되는 함수
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initArrays()
        initPagerView()
        initCalendar()
        binding.apply {
            totalPageCount.text = "총 ${RECORDFB_ARRAY.size}장"
        }

    }

    override fun onResume() {
        super.onResume()
        Log.i("플러스프래그먼트 리쥼","플러스프래그먼트 리쥼")
        initArrays()
        initPagerView()
        initCalendar()

        activity?.runOnUiThread {
            binding.apply {
            totalPageCount.text = "총 ${RECORDFB_ARRAY.size}장"
            }
        }

    }

    private fun initCalendar() {

        // set current date to calendar and current month to currentMonth variable
        calendar.time = Date()
        currentMonth = calendar[Calendar.MONTH]

        // calendar view manager is responsible for our displaying logic
        val myCalendarViewManager = object : CalendarViewManager {
            override fun setCalendarViewResourceId(
                position: Int,
                date: Date,
                isSelected: Boolean
            ): Int {
                // set date to calendar according to position where we are
                val cal = Calendar.getInstance()
                cal.time = date
                // if item is selected we return this layout items
                // datestr => Mon Jun 06 03:29:08 GMT 2022
                // 2022//6/12 형태로 스트링 편집
                var datelist = date.toString().split(' ')
                var datestr = datelist[5]+'/'
                datestr += when(datelist[1]){
                    "Jan"->'1'
                    "Feb"->'2'
                    "Mar"->'3'
                    "Apr"->'4'
                    "May"->'5'
                    "Jun"->'6'
                    "Jul"->'7'
                    "Aug"->'8'
                    "Sep"->'9'
                    "Oct"->"10"
                    "Nov"->"11"
                    "Dec"->"12"
                    else->"0"
                }
                datestr += '/'
                datestr += if ( datelist[2][0] == '0'){
                    datelist[2][1]
                }else{
                    datelist[2]
                }
                Log.i("tdml datestr",datestr)
                return if (isSelected) {
                    Log.i("tdml isSelected", position.toString())
                    Log.i("tdml date.toString()",date.toString())

                    if (planMap.contains(datestr)) {
                        R.layout.first_special_selected_calendar_item
                    } else {
                        R.layout.selected_calendar_item
                    }
                }
                else{
                    Log.i("tdml Not - Selected",position.toString())
                    Log.i("tdml date.toString()",date.toString())

                    if(planMap.contains(datestr)){
                        R.layout.first_special_calendar_item
                    }else{
                        R.layout.calendar_item
                    }
                }


                // NOTE: if we don't want to do it this way, we can simply change color of background
                // in bindDataToCalendarView method
            }

            override fun bindDataToCalendarView(
                holder: SingleRowCalendarAdapter.CalendarViewHolder,
                date: Date,
                position: Int,
                isSelected: Boolean
            ) {
                // using this method we can bind data to calendar view
                // good practice is if all views in layout have same IDs in all item views
                holder.itemView.tv_date_calendar_item.text = DateUtils.getDayNumber(date)
                holder.itemView.tv_day_calendar_item.text = DateUtils.getDay3LettersName(date)

            }
        }

        tv_row_calendar.text = "${DateUtils.getYear(Date())}"
        // using calendar changes observer we can track changes in calendar
        val myCalendarChangesObserver = object : CalendarChangesObserver {
            // you can override more methods, in this example we need only this one
            override fun whenSelectionChanged(isSelected: Boolean, position: Int, date: Date) {
                tv_row_calendar.text = "${DateUtils.getMonthName(date)} ${DateUtils.getDayNumber(date)}일 "
                var datelist = date.toString().split(' ')
                var datestr = datelist[5]+'/'
                datestr += when(datelist[1]){
                    "Jan"->'1'
                    "Feb"->'2'
                    "Mar"->'3'
                    "Apr"->'4'
                    "May"->'5'
                    "Jun"->'6'
                    "Jul"->'7'
                    "Aug"->'8'
                    "Sep"->'9'
                    "Oct"->"10"
                    "Nov"->"11"
                    "Dec"->"12"
                    else->"0"
                }
                datestr += '/'
                datestr += if ( datelist[2][0] == '0'){
                    datelist[2][1]
                }else{
                    datelist[2]
                }
                if(isSelected && planMap.contains(datestr)){
                    binding?.plusCard?.visibility = View.VISIBLE
                    binding?.placeName?.text = planMap[datestr]?.title
                    binding?.details?.text = planMap[datestr]?.text
                }

                // 여기에 가져온 데이터에 맞는 인텐트 실행
                super.whenSelectionChanged(isSelected, position, date)
            }

        }

        // selection manager is responsible for managing selection
        val mySelectionManager = object : CalendarSelectionManager {
            override fun canBeItemSelected(position: Int, date: Date): Boolean {
                // set date to calendar according to position
                val cal = Calendar.getInstance()
                cal.time = date
                // in this example sunday and saturday can't be selected, others can
                return when (cal[Calendar.DAY_OF_WEEK]) {
                    else -> true
                }
            }
        }

        // here we init our calendar, also you can set more properties if you haven't specified in XML layout
        val singleRowCalendar = main_single_row_calendar.apply {
            calendarViewManager = myCalendarViewManager
            calendarChangesObserver = myCalendarChangesObserver
            calendarSelectionManager = mySelectionManager
            setDates(getFutureDatesOfCurrentMonth())
            init()
        }

        btnRight.setOnClickListener {
            singleRowCalendar.setDates(getDatesOfNextMonth())
            tv_row_calendar.text = "${DateUtils.getYear(singleRowCalendar.getDates()[0])}년 ${DateUtils.getMonthNumber(singleRowCalendar.getDates()[0])}월 "

        }

        btnLeft.setOnClickListener {
            singleRowCalendar.setDates(getDatesOfPreviousMonth())
            tv_row_calendar.text = "${DateUtils.getYear(singleRowCalendar.getDates()[0])}년 ${DateUtils.getMonthNumber(singleRowCalendar.getDates()[0])}월 "
        }
    }

    private fun getDatesOfNextMonth(): List<Date> {
        currentMonth++ // + because we want next month
        if (currentMonth == 12) {
            // we will switch to january of next year, when we reach last month of year
            calendar.set(Calendar.YEAR, calendar[Calendar.YEAR] + 1)
            currentMonth = 0 // 0 == january
        }
        return getDates(mutableListOf())
    }

    private fun getDatesOfPreviousMonth(): List<Date> {
        currentMonth-- // - because we want previous month
        if (currentMonth == -1) {
            // we will switch to december of previous year, when we reach first month of year
            calendar.set(Calendar.YEAR, calendar[Calendar.YEAR] - 1)
            currentMonth = 11 // 11 == december
        }
        return getDates(mutableListOf())
    }

    private fun getFutureDatesOfCurrentMonth(): List<Date> {
        // get all next dates of current month
        currentMonth = calendar[Calendar.MONTH]
        return getDates(mutableListOf())
    }


    private fun getDates(list: MutableList<Date>): List<Date> {
        // load dates of whole month
        calendar.set(Calendar.MONTH, currentMonth)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        list.add(calendar.time)
        while (currentMonth == calendar[Calendar.MONTH]) {
            calendar.add(Calendar.DATE, +1)
            if (calendar[Calendar.MONTH] == currentMonth)
                list.add(calendar.time)
        }
        calendar.add(Calendar.DATE, -1)
        return list
    }



    // 여기 부터는 ViewPager
    private fun initPagerView() {
        binding!!.apply {

            close_card_inPlus.setOnClickListener {
                plusCard.visibility = View.GONE
            }

            pvpAdapter = PlusViewPagerAdapter(getRecordList(),this@PlusFragment)

            /* 여백, 너비에 대한 정의 */
//            binding!!.recordViewPager.setPageTransformer(ZoomOutPageTransformer())
            val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.pageMargin) // dimen 파일 안에 크기를 정의해두었다.
            val pagerWidth = resources.getDimensionPixelOffset(R.dimen.pageWidth) // dimen 파일이 없으면 생성해야함
            val screenWidth = resources.displayMetrics.widthPixels // 스마트폰의 너비 길이를 가져옴
            val offsetPx = screenWidth - pageMarginPx - pagerWidth
            binding?.recordViewPager?.setPageTransformer{ page, position ->
                page.translationX = position * -offsetPx
            }
            binding?.recordViewPager?.offscreenPageLimit = 1 // 몇개의 페이지를 미리 로드해둘 것인지


            pvpAdapter.setOnItemClickListener(object :PlusViewPagerAdapter.OnItemClickListener{
                override fun OnItemClick(v: View,recordData:RecordFromDB) {
                    super.OnItemClick(v, recordData)


                }
            })

            recordViewPager.adapter = pvpAdapter
        }
    }

    // 뷰 페이저에 들어갈 아이템
    private fun getRecordList(): ArrayList<RecordFromDB> {

        return recordArr
    }

    private fun getPlanList(): ArrayList<PlanFromDB> {

        return planArr
    }

    // DB 에서 값 받아와 recordArr,planArr 채워주는 함수
    private fun initArrays(){
        // 여기의 리스트를 그림 받아와서 진행
        recordArr = arrayListOf()
        recordArr.clear()

        if(!RECORDFB_ARRAY.isEmpty()){
            for(i in RECORDFB_ARRAY){
                recordArr.add(i)
            }
        }else{
            val t:Int = RECORDFB_ARRAY.size
            for(i in t until 3){
                recordArr.add(RecordFromDB("Dummy Title",R.drawable.profileimage.toString(),1.0,2.0,"Dummy text","Dummy date"))
            }
        }


//        planArr = PLANFB_ARRAY

        planArr = arrayListOf()
        if(!PLANFB_ARRAY.isEmpty()){
            for (i in PLANFB_ARRAY){
                // Mon Jun 06 03:29:08 GMT 2022
                // 2022/6/12
                planMap.put(i.date,i)
            }
        }
    }

    /* 공식문서에 있는 코드 긁어온거임 */
    inner class ZoomOutPageTransformer : ViewPager2.PageTransformer {
        override fun transformPage(view: View, position: Float) {
            view.apply {
                val pageWidth = width
                val pageHeight = height
                when {
                    position < -1 -> { // [-Infinity,-1)
                        // This page is way off-screen to the left.
                        alpha = 0f
                    }
                    position <= 1 -> { // [-1,1]
                        // Modify the default slide transition to shrink the page as well
                        val scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position))
                        val vertMargin = pageHeight * (1 - scaleFactor) / 2
                        val horzMargin = pageWidth * (1 - scaleFactor) / 2
                        translationX = if (position < 0) {
                            horzMargin - vertMargin / 2
                        } else {
                            horzMargin + vertMargin / 2
                        }

                        // Scale the page down (between MIN_SCALE and 1)
                        scaleX = scaleFactor
                        scaleY = scaleFactor

                        // Fade the page relative to its size.
                        alpha = (MIN_ALPHA +
                                (((scaleFactor - MIN_SCALE) / (1 - MIN_SCALE)) * (1 - MIN_ALPHA)))
                    }
                    else -> { // (1,+Infinity]
                        // This page is way off-screen to the right.
                        alpha = 0f
                    }
                }
            }
        }
    }
}