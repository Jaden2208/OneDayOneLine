package com.whalez.onedayoneline.ui.home

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.whalez.onedayoneline.R
import com.whalez.onedayoneline.data.models.DiaryPost
import kotlinx.android.synthetic.main.diary_list_item.view.*

class DiaryRvAdapter(context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TAG = "kkkRvAdapter"

    private var userEmail = FirebaseAuth.getInstance().currentUser!!.email
    private var firestore = FirebaseFirestore.getInstance()
    private var items: ArrayList<DiaryPost> = arrayListOf()

    init {
        firestore.collection("users/${userEmail}/posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
//            .limit(3)
            .get().addOnSuccessListener { querySnapshot ->
                for (snapshot in querySnapshot.documents) {
                    val item = snapshot.toObject(DiaryPost::class.java)
                    items.add(item!!)
                    Log.d(TAG, "ITEM1st: " + item.toString())
                }
//                val lastVisible = querySnapshot.documents[querySnapshot.size() - 1]
//                Log.d(TAG, "lastVisible: " + lastVisible)
                notifyDataSetChanged()
//                Toast.makeText(context, "First Three Items", Toast.LENGTH_SHORT).show()


//                firestore.collection("users/${userEmail}/posts")
//                    .orderBy("timestamp", Query.Direction.DESCENDING)
//                    .startAfter(lastVisible)
//                    .limit(3)
//                    .addSnapshotListener { querySnapshot, _ ->
//                        for (snapshot in querySnapshot!!.documents) {
//                            val item = snapshot.toObject(DiaryPost::class.java)
//                            items.add(item!!)
//                            Log.d(TAG, "ITEM2nd: " + item.toString())
//                        }
//                    }

            }
//            .addSnapshotListener { querySnapshot, _ ->
//
//                Log.d(TAG, "LAST VISIBLE: " + lastVisible.toString())
//                items.clear()
//                for (snapshot in querySnapshot.documents) {
//                    val item = snapshot.toObject(DiaryPost::class.java)
//                    items.add(item!!)
//                    Log.d(TAG, "ITEM: " + item.toString())
//                }
//                val lastVisible = querySnapshot!!.documents[querySnapshot.size() - 1]
//                val next = firestore.collection("users/${userEmail}/posts")
//                    .orderBy("timestamp")
//                notifyDataSetChanged()
//            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DiaryViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.diary_list_item, parent, false
                )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("kkkAdapter", "onBindViewHolder")
        when (holder) {
            is DiaryViewHolder -> {
                holder.bind(items[position]) // == holder.bind(items.get(position))
            }
        }
    }

    class DiaryViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contentText = itemView.content_text
        private val postDate = itemView.post_date
        private val imgPost = itemView.cv_image

        fun bind(diaryPost: DiaryPost) {
            contentText.text = diaryPost.message
            postDate.text = diaryPost.date
            Glide.with(itemView.context).load(diaryPost.image_url).into(imgPost)
            imgPost.setColorFilter(Color.parseColor("#A0A0A0"), PorterDuff.Mode.SCREEN)
        }
    }
}