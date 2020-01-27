package com.whalez.onedayoneline

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.whalez.onedayoneline.models.DiaryPost
import kotlinx.android.synthetic.main.diary_list_item.view.*

class DiaryRvAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items: List<DiaryPost> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DiaryViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.diary_list_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is DiaryViewHolder -> {
                holder.bind(items[position]) // == holder.bind(items.get(position))
            }
        }
    }

    fun submitList(diaryList: List<DiaryPost>){
        items = diaryList
    }

    class DiaryViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView){
        val contentText = itemView.content_text
        val postDate = itemView.post_date

        fun bind(diaryPost: DiaryPost){
            contentText.text = diaryPost.content
            postDate.text = diaryPost.date

//            val requestOptions = RequestOptions()
//                .placeholder(R.drawable.default_image)
//                .error(R.drawable.default_image)
//            Glide.with(itemView.context)
//                .applyDefaultRequestOptions(requestOptions)
//                .load(diaryPost.image)
//                .into(contentImg)
        }
    }
}