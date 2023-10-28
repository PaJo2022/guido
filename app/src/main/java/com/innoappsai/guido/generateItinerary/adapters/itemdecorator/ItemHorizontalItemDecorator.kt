package com.innoappsai.guido.generateItinerary.adapters.itemdecorator

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.intuit.sdp.R


class ItemHorizontalItemDecorator(private val appContext: Context) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        val leftAndRightMargin = appContext.resources.getDimensionPixelSize(R.dimen._12sdp)
        val horizontalMargin = appContext.resources.getDimensionPixelSize(R.dimen._4sdp)
        appContext.resources.getDimensionPixelSize(R.dimen._12sdp)
        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount
        if (position == 0) {
            outRect.right = horizontalMargin
        } else if (position == itemCount - 1) {
            outRect.left = horizontalMargin
        }else{
            outRect.left = horizontalMargin
            outRect.right = horizontalMargin
        }
    }
}

