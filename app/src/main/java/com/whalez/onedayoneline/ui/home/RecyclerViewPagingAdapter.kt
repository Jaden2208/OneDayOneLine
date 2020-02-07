package com.whalez.onedayoneline.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.whalez.onedayoneline.R
import com.whalez.onedayoneline.model.DiaryPost

open class RecyclerViewPagingAdapter(options: FirestorePagingOptions<DiaryPost>) :
    FirestorePagingAdapter<DiaryPost, DiaryViewHolder>(options) {
    private val TAG = "kkk.RvpAdapter"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.diary_list_item, parent, false)
        return DiaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int, model: DiaryPost) {
        holder.bind(model)
    }

    override fun onError(e: Exception) {
        super.onError(e)
        Log.e(TAG, e.message.toString())
    }

}

