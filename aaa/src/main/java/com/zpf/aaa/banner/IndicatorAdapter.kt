package com.zpf.aaa.banner

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class IndicatorAdapter :
    ListAdapter<Boolean, RecyclerView.ViewHolder>(object : DiffUtil.ItemCallback<Boolean>() {
        override fun areItemsTheSame(oldItem: Boolean, newItem: Boolean): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Boolean, newItem: Boolean): Boolean {
            return oldItem == newItem
        }

    }), IBannerChangeListener {

     override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): RecyclerView.ViewHolder {
        val size = (parent.resources.displayMetrics.density * 20).toInt()
        val margin = size / 4
        val itemView = View(parent.context)
        val lp = RecyclerView.LayoutParams(
            size, size
        )
        lp.marginStart = margin
        lp.marginEnd = margin
        itemView.layoutParams = lp

        return object : RecyclerView.ViewHolder(itemView) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItem(position)) {
            holder.itemView.setBackgroundColor(Color.RED)
        } else {
            holder.itemView.setBackgroundColor(Color.BLUE)
        }
    }

    override fun onSelected(position: Int) {
        val list = ArrayList<Boolean>()
        for (i in 0 until itemCount) {
            list.add(position == i)
        }
        submitList(list)
    }

    override fun onDataChanged(size: Int, initPosition: Int) {
        val list = ArrayList<Boolean>()
        for (i in 0 until size) {
            list.add(initPosition == i)
        }
        submitList(list)
    }
}