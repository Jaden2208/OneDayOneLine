package com.whalez.onedayoneline.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.whalez.onedayoneline.data.DataSource
import com.whalez.onedayoneline.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var diaryAdapter: DiaryRvAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRecyclerView()
        addDataSet()
    }

    private fun addDataSet(){
        val data = DataSource.createDataSet()
        diaryAdapter.submitList(data)
    }

    private fun initRecyclerView(){
        rv_main.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            diaryAdapter = DiaryRvAdapter()
            adapter = diaryAdapter
        }
    }
}
