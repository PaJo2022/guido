package com.guido.app.adapters

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.intuit.sdp.R


class CustomItemDecoration(private val appContext: Context) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        val leftAndRightMargin = appContext.resources.getDimensionPixelSize(R.dimen._12sdp)
        val horizontalMargin = appContext.resources.getDimensionPixelSize(R.dimen._4sdp)
        appContext.resources.getDimensionPixelSize(R.dimen._12sdp)
        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount

        when (position) {
            0 -> {
                outRect.left = leftAndRightMargin
                outRect.right = horizontalMargin
            }
            itemCount - 1 -> {
                outRect.left = horizontalMargin
                outRect.right = leftAndRightMargin
            }
            else -> {
                outRect.left = horizontalMargin
                outRect.right = horizontalMargin
            }
        }
    }
}

