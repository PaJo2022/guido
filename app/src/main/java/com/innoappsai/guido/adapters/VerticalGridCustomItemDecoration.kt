package com.innoappsai.guido.adapters

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.intuit.sdp.R


class VerticalGridCustomItemDecoration(private val appContext : Context) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        val leftAndRightMargin = appContext.resources.getDimensionPixelSize(R.dimen._8sdp)
        val horizontalMargin = appContext.resources.getDimensionPixelSize(R.dimen._8sdp)
        val verticalMargin = appContext.resources.getDimensionPixelSize(R.dimen._6sdp)

        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount
        outRect.left = leftAndRightMargin
        outRect.right = leftAndRightMargin
        if(itemCount == 0){
            outRect.left = leftAndRightMargin
        }else if(position == 0 || position % 5 == 0){
            outRect.left = leftAndRightMargin
            outRect.right = horizontalMargin/2
        }else if(position == itemCount - 1){
            outRect.left = horizontalMargin/2
            outRect.right = leftAndRightMargin
        }else{
            outRect.left = horizontalMargin/2
            outRect.right = horizontalMargin/2
        }
        outRect.bottom = verticalMargin
    }
}

