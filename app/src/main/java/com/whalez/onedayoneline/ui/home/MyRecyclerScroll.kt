package com.whalez.onedayoneline.ui.home

import androidx.recyclerview.widget.RecyclerView

abstract class MyRecyclerScroll : RecyclerView.OnScrollListener() {
    private var scrollDist = 0
    private var isVisible = true
    private val MINIMUM = 25

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        // 스크롤에 따른 subtitle 사라짐 효과
        if (isVisible && scrollDist > MINIMUM) {
            hide()
            scrollDist = 0
            isVisible = false
        } else if (!isVisible && scrollDist < -MINIMUM) {
            show()
            scrollDist = 0
            isVisible = true
        }

        if ((isVisible && dy > 0) || (!isVisible && dy < 0)) {
            scrollDist += dy
        }
    }

    abstract fun show()
    abstract fun hide()
}