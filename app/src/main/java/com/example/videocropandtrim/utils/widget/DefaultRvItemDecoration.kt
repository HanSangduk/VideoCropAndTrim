package com.example.videocropandtrim.utils.widget

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.videocropandtrim.utils.logg

class DefaultRvItemDecoration(val space: Int): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        parent.adapter?.itemCount?.let {
            if (position == 0) {
                outRect.left = space
                outRect.right = 0
            } else if (it > 10 && position == it - 1) {
                outRect.left = 0
                outRect.right = space
            } else {
                outRect.left = 0
                outRect.right = 0
            }
        }
    }
}