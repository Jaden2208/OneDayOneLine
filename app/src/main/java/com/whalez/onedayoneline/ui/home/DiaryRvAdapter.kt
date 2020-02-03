package com.whalez.onedayoneline.ui.home

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.indyproject2.UserSessionManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.whalez.onedayoneline.R
import com.whalez.onedayoneline.data.DiaryPost
import com.whalez.onedayoneline.data.repositories.UserRepository
import kotlinx.android.synthetic.main.diary_list_item.view.*

class DiaryRvAdapter(context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var firestore = FirebaseFirestore.getInstance()
    private var items: ArrayList<DiaryPost> = arrayListOf()

    init {
//        val userSessionManager = UserSessionManager(context)
//        val id = userSessionManager.userDetail["ID"]

        firestore.collection("users/ryu2208@naver.com/posts")
//        firestore.collection("users/${id}/posts")
            .orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener { querySnapshot, _ ->
            items.clear()
            for(snapshot in querySnapshot!!.documents){
                val item = snapshot.toObject(DiaryPost::class.java)
                items.add(item!!)
            }
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DiaryViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.diary_list_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("kkkAdapter", "onBindViewHolder")
        when(holder){
            is DiaryViewHolder -> {
                holder.bind(items[position]) // == holder.bind(items.get(position))
            }
        }
    }

    class DiaryViewHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView){
        private val contentText = itemView.content_text
        private val postDate = itemView.post_date
        private val imgPost = itemView.cv_image

        fun bind(diaryPost: DiaryPost){
            contentText.text = diaryPost.message
            postDate.text = diaryPost.date
            Glide.with(itemView.context).load(diaryPost.image_url).into(imgPost)
            imgPost.setColorFilter(Color.parseColor("#A0A0A0"), PorterDuff.Mode.SCREEN)
        }
    }
}