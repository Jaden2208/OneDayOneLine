package com.whalez.onedayoneline.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.indyproject2.UserSessionManager
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.skydoves.powermenu.kotlin.powerMenu
import com.whalez.onedayoneline.R
import com.whalez.onedayoneline.databinding.ActivityHomeBinding
import com.whalez.onedayoneline.ui.post.PostActivity
import kotlinx.android.synthetic.main.activity_home.*
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import org.kodein.di.android.kodein


class HomeActivity : AppCompatActivity(), KodeinAware {

    private val TAG = "kkk"

    private lateinit var diaryAdapter: DiaryRvAdapter

    private val menu by powerMenu(PowerMenuFactory::class)
    private val userMenu by powerMenu(UserMenuFactory::class)

    override val kodein by kodein()
    private val factory : HomeViewModelFactory by instance()

    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val binding: ActivityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        viewModel = ViewModelProviders.of(this, factory).get(HomeViewModel::class.java)
        binding.viewmodel = viewModel

//        val userSessionManager = UserSessionManager(this)
//        val id = userSessionManager.userDetail["ID"]
//
//        userSessionManager.checkLogin()

        initRecyclerView()

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
                            finish()
                            Toast.makeText(this, "정상적으로 로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                        .setNegativeButton("아니오") { _, _ -> }
                        .show()
                }
            }
        }

//        // (+) 버튼 클릭
//        btn_post.setOnClickListener {
//            val intent = Intent(this, PostActivity::class.java)
//            startActivity(intent)
//        }
//
//        // Recyclerview ScrollListener
//        rv_main.addOnScrollListener(object: MyRecyclerScroll() {
//            override fun show() {
//                fab_subtitle.animate().translationY(0F).setInterpolator(DecelerateInterpolator(2F)).start()
//                rv_main.animate().translationY(0F).setInterpolator(DecelerateInterpolator(2F)).start()
//            }
//
//            override fun hide() {
//                fab_subtitle.animate().translationY(0F - fab_subtitle.height).setInterpolator(AccelerateInterpolator(2F)).start()
//                rv_main.animate().translationY(0F - fab_subtitle.height).setInterpolator(AccelerateInterpolator(2F)).start()
//            }
//        })
    }

    private fun initRecyclerView() {
        Log.d(TAG, "initRecyclerView 진입")
        rv_main.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            diaryAdapter =
                DiaryRvAdapter(context)
            adapter = diaryAdapter
        }
    }
}
