package com.zpf.aaa

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VideoPageAdapter : RecyclerView.Adapter<VideoPageAdapter.ViewHolder>() {
    private val colors =
        arrayOf(Color.GRAY, Color.BLUE, Color.YELLOW, Color.RED, Color.GREEN, Color.MAGENTA)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = TextView(parent.context)
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        view.gravity = Gravity.CENTER
        view.textSize = 20f
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = 3

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.run {
            text = position.toString()
            setBackgroundColor(colors[position % colors.size])
        }
    }

    class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
}