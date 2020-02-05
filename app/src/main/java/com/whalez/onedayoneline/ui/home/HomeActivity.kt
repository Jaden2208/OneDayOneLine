package com.whalez.onedayoneline.ui.home

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.view.isEmpty
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.whalez.onedayoneline.sharedpreference.UserSessionManager
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.skydoves.powermenu.kotlin.powerMenu
import com.whalez.onedayoneline.R
import com.whalez.onedayoneline.models.DiaryPost
import com.whalez.onedayoneline.ui.auth.LoginActivity
import com.whalez.onedayoneline.ui.post.PostActivity
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity: AppCompatActivity(){

    private val TAG = "kkk.HomeActivity"

    private val menu by powerMenu(PowerMenuFactory::class)
    private val userMenu by powerMenu(UserMenuFactory::class)

    private lateinit var mAdapter: FirestorePagingAdapter<DiaryPost, DiaryViewHolder>
    private val mFirestore = FirebaseFirestore.getInstance()
    private lateinit var mPostsCollection: CollectionReference
    private lateinit var mQuery: Query
    private lateinit var userEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        Log.d(TAG, "onCreate")

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

        // (+) 버튼 클릭
        btn_post.setOnClickListener {
            val intent = Intent(this, PostActivity::class.java)
            startActivity(intent)
        }

        // Recyclerview ScrollListener
        rv_main.addOnScrollListener(object : MyRecyclerScroll() {
            override fun show() {
                fab_subtitle.animate().translationY(0F).setInterpolator(DecelerateInterpolator(2F))
                    .start()
                rv_main.animate().translationY(0F).setInterpolator(DecelerateInterpolator(2F))
                    .start()
            }

            override fun hide() {
                fab_subtitle.animate().translationY(0F - fab_subtitle.height).setInterpolator(
                    AccelerateInterpolator(2F)
                ).start()
                rv_main.animate().translationY(0F - fab_subtitle.height)
                    .setInterpolator(AccelerateInterpolator(2F)).start()
            }

        })
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
        mAdapter.refresh()
        mAdapter.startListening()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
        mAdapter.stopListening()
    }

    private fun setupAdapter(){
        Log.d(TAG, "setupAdapter 진입")
        mPostsCollection = mFirestore.collection("users/${userEmail}/posts")
        mQuery = mPostsCollection.orderBy("timestamp", Query.Direction.DESCENDING)

        // Init Paging Configuration
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(2)
            .setPageSize(5) // 이 메서드의 파라미터 * 3 개 씩 로드 됨.
            .build()

        // Init Adapter Configuration
        val options = FirestorePagingOptions.Builder<DiaryPost>()
            .setLifecycleOwner(this)
            .setQuery(mQuery, config, DiaryPost::class.java)
            .build()

        // FireStore 페이징 어댑터 초기화
        mAdapter = object : FirestorePagingAdapter<DiaryPost, DiaryViewHolder>(options) {
            init {
                Log.d(TAG, "PagingAdapter 진입")
            }
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
                Log.d(TAG, "onCreateViewHolder진입")
                val view = layoutInflater.inflate(R.layout.diary_list_item, parent, false)
                return DiaryViewHolder(view)
            }

            override fun onBindViewHolder(viewHolder: DiaryViewHolder, position: Int, post: DiaryPost) {
                Log.d(TAG, "onBindeViewHolder진입")
                Log.d("kkk Post", post.toString())
                viewHolder.bind(post)

            }

            override fun onError(e: Exception) {
                super.onError(e)
                Log.e("kkkMainActivity", e.message)
            }

            override fun refresh() {
                super.refresh()
                Log.d(TAG, "refresh!")
                mQuery.get().addOnCompleteListener {
                    val itemCount = it.result!!.documents.size
                    if(itemCount == 0){
                        tutorial.visibility = View.VISIBLE
                    }
                    else{
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
