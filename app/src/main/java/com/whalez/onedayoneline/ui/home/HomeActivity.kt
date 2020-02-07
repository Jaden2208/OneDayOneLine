package com.whalez.onedayoneline.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.paging.PagedList
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.whalez.onedayoneline.sharedpreference.UserSessionManager
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.skydoves.powermenu.kotlin.powerMenu
import com.whalez.onedayoneline.R
import com.whalez.onedayoneline.model.DiaryPost
import com.whalez.onedayoneline.ui.auth.LoginActivity
import com.whalez.onedayoneline.ui.home.menu.PowerMenuFactory
import com.whalez.onedayoneline.ui.home.menu.UserMenuFactory
import com.whalez.onedayoneline.ui.post.PostActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.diary_list_item.*

class HomeActivity : AppCompatActivity() {

    private val TAG = "kkk.HomeActivity"

    private val menu by powerMenu(PowerMenuFactory::class)
    private val userMenu by powerMenu(UserMenuFactory::class)

    private lateinit var mAdapter: FirestorePagingAdapter<DiaryPost, DiaryViewHolder>
    private val mFirestore = FirebaseFirestore.getInstance()
    private lateinit var mPostsCollection: CollectionReference
    private lateinit var mQuery: Query
    private lateinit var userEmail: String

    private val showItemCounts = 5 // ==> 5 * 3 개 씩 로드 됨

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val userSessionManager = UserSessionManager(this)
        userSessionManager.checkLogin()
        userEmail = userSessionManager.currentID.toString()

        rv_main.setHasFixedSize(true)
        rv_main.layoutManager = LinearLayoutManager(this)

        setupAdapter()

        // Refresh Action on Swipe Refresh Layout
        swipeRefreshLayout.setOnRefreshListener {
            mAdapter.refresh()
        }

        // 메뉴 버튼 클릭
        btn_menu.setOnClickListener { menu.showAsDropDown(it) }
        // 메뉴 아이템 클릭
        menu.setOnMenuItemClickListener { position, item ->
            Toast.makeText(this, item.title, Toast.LENGTH_SHORT).show()
            when (position) {
                0 -> { // 삭제
                }
            }
        }

        // 사용자 메뉴 버튼 클릭
        btn_user.setOnClickListener { userMenu.showAsDropDown(it) }

        // 사용자 메뉴 아이템 클릭
        userMenu.setOnMenuItemClickListener { position, item ->
            when (position) {
                0 -> {
                    Toast.makeText(this, item.title, Toast.LENGTH_SHORT).show()
                }
                1 -> { // 로그아웃 버튼 클릭
                    val builder = AlertDialog.Builder(
                        ContextThemeWrapper(
                            this@HomeActivity,
                            R.style.MyAlertDialogStyle
                        )
                    )
                    builder.setMessage("정말 로그아웃 하시겠습니까?")
                        .setPositiveButton("예") { _, _ ->
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            userSessionManager.logout()
                            finish()
                            Toast.makeText(this, "정상적으로 로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                        .setNegativeButton("아니오") { _, _ -> }
                        .show()
                }
            }
        }

        // Swipe를 통한 아이템 삭제
        val mIth = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
                ): Boolean {
                    return false // true if moved, false otherwise
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    // remove from adapter
                    viewHolder as DiaryViewHolder
                    mFirestore.collection("users/${userEmail}/posts")
                        .document(viewHolder.postDateText)
                        .delete()
                        .addOnSuccessListener {
                            mAdapter.refresh()
                            Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                        .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }

                }
            })
        mIth.attachToRecyclerView(rv_main)

        // (+) 버튼 클릭
        btn_post.setOnClickListener {
            val intent = Intent(this, PostActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
        mAdapter.refresh()
        mAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        mAdapter.stopListening()
    }

    private fun setupAdapter() {
        mPostsCollection = mFirestore.collection("users/${userEmail}/posts")
        mQuery = mPostsCollection.orderBy("timestamp", Query.Direction.DESCENDING)

        // Init Paging Configuration
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(2)
            .setPageSize(showItemCounts)
            .build()

        // Init Adapter Configuration
        val options = FirestorePagingOptions.Builder<DiaryPost>()
            .setLifecycleOwner(this)
            .setQuery(mQuery, config, DiaryPost::class.java)
            .build()

        // FireStore 페이징 어댑터 초기화
        mAdapter = object : RecyclerViewPagingAdapter(options) {

            override fun refresh() {
                super.refresh()
                Log.d(TAG, "refresh!")
                mQuery.get().addOnCompleteListener {
                    val itemCount = it.result!!.documents.size
                    if (itemCount == 0) {
                        tutorial.visibility = View.VISIBLE
                    } else {
                        tutorial.visibility = View.GONE
                    }
                }
            }

            override fun onLoadingStateChanged(state: LoadingState) {
                when (state) {
                    LoadingState.LOADING_INITIAL -> {
                        swipeRefreshLayout.isRefreshing = true
                    }

                    LoadingState.LOADING_MORE -> {
                        swipeRefreshLayout.isRefreshing = true
                    }

                    LoadingState.LOADED -> {
                        swipeRefreshLayout.isRefreshing = false
                        Log.d(TAG, "~~~LOADED~~~")

                    }

                    LoadingState.ERROR -> {
                        Toast.makeText(
                            applicationContext,
                            "Error Occurred!",
                            Toast.LENGTH_SHORT
                        ).show()
                        swipeRefreshLayout.isRefreshing = false
                    }

                    LoadingState.FINISHED -> {
                        swipeRefreshLayout.isRefreshing = false
                    }
                }
            }
        }
        // Finally Set the Adapter to RecyclerView
        rv_main.adapter = mAdapter
    }

}
