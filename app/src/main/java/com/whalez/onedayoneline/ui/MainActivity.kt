package com.whalez.onedayoneline.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.indyproject2.UserSessionManager
import com.whalez.onedayoneline.data.DataSource
import com.whalez.onedayoneline.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var diaryAdapter: DiaryRvAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userSessionManager = UserSessionManager(this)
        userSessionManager.checkLogin()

        initRecyclerView()
        addDataSet()

        // 임시 로그아웃 버튼
        btn_add.setOnClickListener {
            val builder = AlertDialog.Builder(
                ContextThemeWrapper(
                    this@MainActivity,
                    R.style.Theme_AppCompat_Light_Dialog
                )
            )
            builder.setMessage("로그아웃 하시겠습니까?")
                .setPositiveButton("예") { _, _ ->
                    userSessionManager.logout()
                    Toast.makeText(this@MainActivity, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("아니오"){ _, _ ->

                }
                .show()
        }
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
