package com.zpf.aaa.banner

interface IBannerChangeListener {

    fun onSelected(position: Int)

    fun onDataChanged(size: Int, initPosition: Int)
}