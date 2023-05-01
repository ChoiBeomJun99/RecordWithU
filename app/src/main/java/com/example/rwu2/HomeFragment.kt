package com.example.rwu2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.rwu2.Application_Init.Companion.RECORDFB_ARRAY
import kotlinx.android.synthetic.main.activity_before_logged_in.*
import org.w3c.dom.Text

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val dateText : TextView = view.findViewById(R.id.textView2) // 데이터베이스에서 접근해서 쌓인 기록의 count를 이곳에다가 저장
        val Text : TextView = view.findViewById(R.id.textView1)

        return inflater.inflate(R.layout.fragment_home, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.textView2).text = RECORDFB_ARRAY.size.toString() + "개"
    }



}