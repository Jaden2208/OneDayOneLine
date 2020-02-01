package com.whalez.onedayoneline.ui

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.makeramen.roundedimageview.RoundedImageView
import com.whalez.onedayoneline.R
import com.whalez.onedayoneline.models.DiaryPost
import kotlinx.android.synthetic.main.activity_post.view.*
import kotlinx.android.synthetic.main.diary_list_item.view.*


//class DiaryRvAdapter(options: FirestoreRecyclerOptions<DiaryPost>) : FirestoreRecyclerAdapter<DiaryPost, DiaryRvAdapter.DiaryHolder>(
//    options
//) {
//
//    class DiaryHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView){
//
//        private val TAG = "kkk_DiaryRvAdapter"
//        var contentText = itemView.content_text!!
//        var postDate = itemView.post_date!!
//        var imgPost = itemView.cv_image!!
//
//        fun bind(diaryPost: DiaryPost){
//            Log.d(TAG, "bind 진입")
//            Log.d(TAG, "내용: " + diaryPost.content)
//            Log.d(TAG, "날짜: " + diaryPost.date)
//            Log.d(TAG, "이미지url: " + diaryPost.imageUrl)
//            contentText.text = diaryPost.content
//            postDate.text = diaryPost.date
//            Glide.with(itemView).load(diaryPost.imageUrl).into(imgPost)
//        }
//    }
//
//    private var items: List<DiaryPost> = ArrayList()
//
//    override fun onBindViewHolder(holder: DiaryHolder, position: Int, model: DiaryPost) {
//        Log.d("kkk", "onBindViewHolder 호출")
////        when(holder){
////            is DiaryHolder -> {
////                holder.bind(items[position])
////            }
////        }
//        holder.bind(items[position])
//    }
//
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryHolder {
//        return DiaryHolder(LayoutInflater.from(parent.context).inflate(
//            R.layout.diary_list_item, parent, false
//        ))
//    }
//
//    override fun getItemCount(): Int {
//        return items.size
//    }
//
//    fun submitList(diaryList: List<DiaryPost>){
//        items = diaryList
//    }
//}

class DiaryRvAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var items: ArrayList<DiaryPost> = arrayListOf()

    init {
        Log.d("kkkAdapter", "init")
        firestore.collection("users/ryu2208@naver.com/posts")
            .orderBy("timestamp").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            items.clear()
            Log.d("kkkAdapter", "snapshot: " + querySnapshot!!.documents.size)
            for(snapshot in querySnapshot.documents){
                Log.d("kkkAdapter", "snapshot: " + snapshot.toString())
                val item = snapshot.toObject(DiaryPost::class.java)
                items.add(item!!)
            }
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d("kkkAdapter", "onCreateViewHolder")
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

//    fun submitList(diaryList: List<DiaryPost>){
//        items = diaryList
//    }

    class DiaryViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView){
        private val contentText = itemView.content_text
        private val postDate = itemView.post_date
        private val imgPost = itemView.cv_image!!

        fun bind(diaryPost: DiaryPost){
            Log.d("kkkAdapter", "bind")
            contentText.text = diaryPost.content
            postDate.text = diaryPost.date
            Glide.with(itemView.context).load(diaryPost.imageUrl).into(imgPost)
        }
    }
}