package com.whalez.onedayoneline.ui.home

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.whalez.onedayoneline.model.DiaryPost
import kotlinx.android.synthetic.main.diary_list_item.view.*

class DiaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val TAG = "kkk.DiaryViewHolder"

    private val contentText = itemView.content_text
//    lateinit var timestamp: String
    private val postDate = itemView.post_date
    lateinit var postDateText: String
    private val imgPost = itemView.cv_image

    fun bind(diaryPost: DiaryPost) {
//        timestamp = diaryPost.timestamp.toString()
        contentText.text = diaryPost.message
        postDate.text = diaryPost.date
        Glide.with(itemView.context).load(diaryPost.image_url).into(imgPost)
        imgPost.setColorFilter(Color.parseColor("#A0A0A0"), PorterDuff.Mode.SCREEN)
        postDateText = postDate.text.toString()
    }
}