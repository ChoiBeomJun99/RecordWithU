package com.example.rwu2.recordView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rwu2.Application_Init.Companion.RECORDFB_ARRAY
import com.example.rwu2.PlanFromDB
import com.example.rwu2.PlusFragment
import com.example.rwu2.R
import com.example.rwu2.RecordFromDB
import com.example.rwu2.databinding.RecordRowBinding
import kotlinx.coroutines.withContext

class PlusViewPagerAdapter(
    recordList: ArrayList<RecordFromDB>,
    context: PlusFragment
)
    : RecyclerView.Adapter<PlusViewPagerAdapter.PagerViewHolder>() {
    var fragContext = context
    var recordArr = recordList


    // xml파일을 inflate하여 ViewHolder를 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder { //ViewHolder를 생성해서 반환하는 함수
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.row,parent,false)
        val binding = RecordRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PagerViewHolder(binding)
    }

//    override fun getItemCount(): Int = recordArr.size
    override fun getItemCount(): Int = Int.MAX_VALUE

    // onCreateViewHolder에서 만든 view와 실제 데이터를 연결
    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
//        holder.record.setImageResource(recordArr[position%recordArr.size].imgURL.toInt())
        Glide.with(fragContext).load(recordArr[position%recordArr.size].imgURL)
            .into(holder.record)
        holder.record.clipToOutline =true

        holder.text.setText(recordArr[position%recordArr.size].text)
    }

    inner class PagerViewHolder(val binding: RecordRowBinding) : RecyclerView.ViewHolder(binding.root){
        val record = itemView.findViewById<ImageView>(R.id.imageView_record)
        val text = itemView.findViewById<TextView>(R.id.textView_record)

        // (1) 리스트 내 항목 클릭 시 onClick() 호출
        init {
            binding.imageViewRecord.setOnClickListener {
                // 아이템 클릭 시 전달해줄 값을 이 함수 인자에 넣는다
                itemClickListener?.OnItemClick(binding.imageViewRecord,recordArr[bindingAdapterPosition%recordArr.size])
                binding.imageViewRecord.visibility = View.GONE
                binding.textViewRecord.visibility = View.VISIBLE
            }
            binding.textViewRecord.setOnClickListener {
                itemClickListener?.OnItemClick(binding.textViewRecord,recordArr[bindingAdapterPosition%recordArr.size])
                binding.textViewRecord.visibility = View.GONE
                binding.imageViewRecord.visibility = View.VISIBLE
            }
        }
    }

    // (2) 리스너 인터페이스
    interface OnItemClickListener{
        fun OnItemClick(v:View,recordData:RecordFromDB){

        }
    }

    // (3) 외부에서 OnItemClickListener 의 이벤트 설정
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    // (4) setItemClickListener 로 설정한 함수 실행
    var itemClickListener: PlusViewPagerAdapter.OnItemClickListener?= null
}