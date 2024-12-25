package com.zpf.aaa.banner

import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ContentAdapter :
    ListAdapter<String, RecyclerView.ViewHolder>(object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }) {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): RecyclerView.ViewHolder {
        val margin = (parent.resources.displayMetrics.density * 16).toInt()
        val textView = TextView(parent.context)
        val lp = RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        textView.gravity = Gravity.CENTER
        textView.setBackgroundColor(Color.GREEN)
        lp.marginStart = margin
        lp.marginEnd = margin
        textView.layoutParams = lp
        return object : RecyclerView.ViewHolder(textView) {}
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    final override fun getItemCount(): Int {
        if (currentList.size > 1) {
            return currentList.size * 3
        }
        return currentList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val realPosition = position % currentList.size
        (holder.itemView as? TextView)?.text = getItem(realPosition)
    }
}