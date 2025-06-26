package com.zpf.aaa.banner

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class RecyclerViewDivider(
    private var horizontalSpace: Float = 0f, private var verticaSpace: Float = 0f
) : RecyclerView.ItemDecoration() {

    fun reset(horizontalSpace: Float, verticaSpace: Float) {
        this.horizontalSpace = horizontalSpace
        this.verticaSpace = verticaSpace
    }

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        if (horizontalSpace <= 0 && verticaSpace <= 0) {
            outRect.set(0, 0, 0, 0)
            return
        }
        val manager = parent.layoutManager
        val n = manager?.itemCount ?: 0
        if (manager == null || n < 2) {
            outRect.set(0, 0, 0, 0)
            return
        }
        if (manager is GridLayoutManager) {
            val index = parent.getChildAdapterPosition(view)
            val spanSize = manager.spanSizeLookup.getSpanSize(index)
            calcGridOffset(
                manager.orientation, manager.spanCount, spanSize, index, n, outRect
            )
        } else if (manager is StaggeredGridLayoutManager) {
            val index = parent.getChildAdapterPosition(view)
            calcGridOffset(manager.orientation, manager.spanCount, 1, index, n, outRect)
        } else if (manager is LinearLayoutManager) {
            val index = parent.getChildAdapterPosition(view)
            if (manager.orientation == RecyclerView.HORIZONTAL) {
                if (index < manager.itemCount - 1) {
                    outRect.right = horizontalSpace.toInt()
                } else {
                    outRect.right = 0
                }
            } else {
                if (index < manager.itemCount - 1) {
                    outRect.bottom = verticaSpace.toInt()
                } else {
                    outRect.bottom = 0
                }
            }
        }
    }

    private fun calcGridOffset(
        orientation: Int,
        spanCount: Int,
        spanSize: Int,
        itemIndex: Int,
        itemCount: Int,
        outRect: Rect
    ) {
        val x = itemIndex % spanCount
        val y = itemIndex / spanCount
        val m = if (itemCount % spanCount > 0) {
            itemCount / spanCount + 1
        } else {
            itemCount / spanCount
        }
        val rowIndex: Int
        val columnIndex: Int
        val totalRowCount: Int
        val totalColumnCount: Int
        if (orientation == RecyclerView.VERTICAL) {
            totalColumnCount = spanCount
            totalRowCount = m
            rowIndex = y
            columnIndex = x
        } else {
            totalColumnCount = m
            totalRowCount = spanCount
            rowIndex = x
            columnIndex = y
        }
        if (totalColumnCount > 1) {
            val p = horizontalSpace / totalColumnCount
            outRect.left = (p * columnIndex).toInt()
            outRect.right = (p * (totalColumnCount - columnIndex - spanSize)).toInt()
        } else {
            outRect.left = 0
            outRect.right = 0
        }
        if (totalRowCount > 1) {
            val p = verticaSpace / totalRowCount
            outRect.top = (p * rowIndex).toInt()
            outRect.bottom = (p * (totalRowCount - rowIndex - spanSize)).toInt()
        } else {
            outRect.top = 0
            outRect.bottom = 0
        }
    }
}