package com.example.myrsaapp.ui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 * RecycleView分割线
 *
 * @author Wiger Cheng
 */
class RecycleViewItemDecoration(private val offset: Int = 10) : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect[0, 0, 0] = offset
    }
}