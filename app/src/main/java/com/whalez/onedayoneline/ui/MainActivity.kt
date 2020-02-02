package com.whalez.onedayoneline.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.indyproject2.UserSessionManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.skydoves.powermenu.kotlin.powerMenu
import com.whalez.onedayoneline.R
import com.whalez.onedayoneline.models.DiaryPost
import com.whalez.onedayoneline.utils.MyRecyclerScroll
import com.whalez.onedayoneline.utils.PowerMenuFactory
import com.whalez.onedayoneline.utils.UserMenuFactory
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val TAG = "kkk"

    var db: FirebaseFirestore? = null
    private lateinit var diaryRef: CollectionReference
    private lateinit var diaryAdapter: DiaryRvAdapter

    private val menu by powerMenu(PowerMenuFactory::class)
    private val userMenu by powerMenu(UserMenuFactory::class)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userSessionManager = UserSessionManager(this)
        val id = userSessionManager.userDetail["ID"]

        db = FirebaseFirestore.getInstance()
        diaryRef = db!!.collection("users/${id}/posts")

        userSessionManager.checkLogin()

        initRecyclerView()
//        addDataSet()
//        toolbar.bringToFront()

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
                            this@MainActivity,
                            R.style.MyAlertDialogStyle
                        )
                    )
                    builder.setMessage("정말 로그아웃 하시겠습니까?")
                        .setPositiveButton("예") { _, _ ->
                            finish()
                            userSessionManager.logout()
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
        rv_main.addOnScrollListener(object: MyRecyclerScroll() {
            override fun show() {
                fab_subtitle.animate().translationY(0F).setInterpolator(DecelerateInterpolator(2F)).start()
                rv_main.animate().translationY(0F).setInterpolator(DecelerateInterpolator(2F)).start()
            }

            override fun hide() {
                fab_subtitle.animate().translationY(0F - fab_subtitle.height).setInterpolator(AccelerateInterpolator(2F)).start()
                rv_main.animate().translationY(0F - fab_subtitle.height).setInterpolator(AccelerateInterpolator(2F)).start()
            }
        })
    }

//    private fun addDataSet() {
//        val data = DataSource.createDataSet()
//        diaryAdapter.submitList(data)
//    }

    private fun initRecyclerView() {
        Log.d(TAG, "initRecyclerView 진입")
//        val query = diaryRef.orderBy("timestamp", Query.Direction.DESCENDING)
//        val options = FirestoreRecyclerOptions.Builder<DiaryPost>()
//            .setQuery(query, DiaryPost::class.java)
//            .build()
//        Log.d(TAG, "options 저장 완료")
        rv_main.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            diaryAdapter = DiaryRvAdapter(context)
            adapter = diaryAdapter
        }
        Log.d(TAG, "initRecyclerView 종료")
//            .hasFixedSize()
    }

//    // 앱이 시작하면 변경된 데이터베이스에 대해서 어댑터에 반영하도록 한다.
//    override fun onStart() {
//        super.onStart()
//        diaryAdapter.startListening()
//    }
//
//    // 앱이 백그라운드로 들어가면 변경된 데이터베이스에 대해서 어댑터에 반영하지 않는다.
//    override fun onStop() {
//        super.onStop()
//        diaryAdapter.stopListening()
//    }
}
