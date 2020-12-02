package com.example.videocropandtrim.utils.widget

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class DefaultRvItemDecoration(val space: Int, val itemCount: Int): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position == 0) {
            outRect.left = space
            outRect.right = 0
        } else if (itemCount > 10 && position == itemCount - 1) {
            outRect.left = 0
            outRect.right = space
        } else {
            outRect.left = 0
            outRect.right = 0
        }
    }
}