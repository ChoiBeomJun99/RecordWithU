package com.example.rwu2.recordView

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rwu2.databinding.ListItemCalendarBinding
import com.example.rwu2.databinding.RecordRowBinding
import java.util.*

class CalendarAdapter(val context: Context, val date: Date):RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {


    // xml파일을 inflate하여 ViewHolder를 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemCalendarBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    // onCreateViewHolder에서 만든 view와 실제 데이터를 연결
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.days.setText("28")
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }

    inner class ViewHolder(val binding: ListItemCalendarBinding) : RecyclerView.ViewHolder(binding.root){
        val days = binding.itemCalendarDateText


    }
}