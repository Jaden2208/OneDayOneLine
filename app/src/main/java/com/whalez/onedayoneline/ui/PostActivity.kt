package com.whalez.onedayoneline.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.whalez.onedayoneline.R
import kotlinx.android.synthetic.main.activity_post.*

class PostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        // back 버튼 클릭
        btn_back.setOnClickListener { finish() }
    }
}
