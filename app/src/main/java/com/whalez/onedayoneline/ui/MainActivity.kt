package com.whalez.onedayoneline.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.indyproject2.UserSessionManager
import com.skydoves.powermenu.kotlin.powerMenu
import com.whalez.onedayoneline.R
import com.whalez.onedayoneline.data.DataSource
import com.whalez.onedayoneline.utils.MyRecyclerScroll
import com.whalez.onedayoneline.utils.PowerMenuFactory
import com.whalez.onedayoneline.utils.UserMenuFactory
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val TAG = "kkk"

    private lateinit var diaryAdapter: DiaryRvAdapter
    private val menu by powerMenu(PowerMenuFactory::class)
    private val userMenu by powerMenu(UserMenuFactory::class)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userSessionManager = UserSessionManager(this)
        userSessionManager.checkLogin()

        initRecyclerView()
        addDataSet()
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

    private fun addDataSet() {
        val data = DataSource.createDataSet()
        diaryAdapter.submitList(data)
    }

    private fun initRecyclerView() {
        rv_main.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            diaryAdapter = DiaryRvAdapter()
            adapter = diaryAdapter
        }
    }
}
