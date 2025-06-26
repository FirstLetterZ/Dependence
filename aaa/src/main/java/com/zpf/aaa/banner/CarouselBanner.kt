package com.zpf.aaa.banner

import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

open class CarouselBanner(
    private val bannerContent: RecyclerView,
    private val bannerIndicator: RecyclerView?,
    val intervalTime: Long = 3000L,
    val scrollTime: Long = 600L,
) : View.OnAttachStateChangeListener {
    private val listeners = HashSet<IBannerChangeListener>()
    private val snapHelper = PagerSnapHelper()
    private val contentScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                val dataSize = getContentSize()
                if (dataSize == 0) {
                    return
                }
                if (dataSize == 1) {
                    if (currentPosition != 0) {
                        currentPosition = 0
                        dispatchSelectChanged(currentPosition)
                    }
                    return
                }
                val contentLayoutManager = bannerContent.layoutManager ?: return
                val snapView: View = snapHelper.findSnapView(contentLayoutManager) ?: return
                val snapPosition: Int = contentLayoutManager.getPosition(snapView)
                if (snapPosition >= 0 && currentPosition != snapPosition) {
                    if (snapPosition >= dataSize) {
                        currentPosition = snapPosition % dataSize
                        bannerContent.scrollToPosition(currentPosition)
                    } else {
                        currentPosition = snapPosition
                    }
                    dispatchSelectChanged(currentPosition)
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
        }
    }
    private val contentTouchListener = object : RecyclerView.OnItemTouchListener {
        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            when (e.action) {
                MotionEvent.ACTION_DOWN -> {
                    stop()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    start()
                }
            }
            return false
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
            when (e.action) {
                MotionEvent.ACTION_DOWN -> {
                    stop()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    start()
                }
            }
        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

        }
    }
    private val contentDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            onChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            onChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            onChanged()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            onChanged()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            onChanged()
        }

        override fun onChanged() {
            if (currentPosition != 0) {
                currentPosition = 0
                val dataSize = getContentSize()
                bannerContent.scrollToPosition(0)
                dispatchDataSizeChanged(dataSize, 0)
            }
        }
    }
    private var lastContentAdapter: RecyclerView.Adapter<*>? = null
    private val scrollRunnable = Runnable {
        autoScroll()
    }
    var currentPosition: Int = -1
        private set

    init {
        checkContentAdapterChanged()
        snapHelper.attachToRecyclerView(bannerContent)
        bannerContent.itemAnimator = null
        bannerContent.setScrollingTouchSlop(RecyclerView.TOUCH_SLOP_PAGING)
        bannerIndicator?.itemAnimator = null
        bannerContent.post {
            addSelfListeners()
        }
        bannerContent.stopScroll()
    }

    override fun onViewAttachedToWindow(v: View) {
        start()
    }

    override fun onViewDetachedFromWindow(v: View) {
        stop()
    }

    protected open fun addSelfListeners() {
        bannerContent.addOnScrollListener(contentScrollListener)
        bannerContent.addOnItemTouchListener(contentTouchListener)
        bannerContent.addOnAttachStateChangeListener(this)
    }

    fun start() {
        bannerContent.removeCallbacks(scrollRunnable)
        if (intervalTime > 0) {
            bannerContent.postDelayed(scrollRunnable, intervalTime)
        }
    }

    fun stop() {
        bannerContent.removeCallbacks(scrollRunnable)
    }

    fun scrollToPosition(position: Int, smooth: Boolean) {
        val dataSize = getContentSize()
        if (position < 0 || position >= dataSize) {
            return
        }
        start()
        if (smooth) {
            bannerContent.smoothScrollToPosition(position)
        } else {
            bannerContent.scrollToPosition(position)
        }
    }

    fun addListener(listener: IBannerChangeListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: IBannerChangeListener) {
        listeners.remove(listener)
    }

    fun clearListener() {
        listeners.clear()
    }

    protected fun dispatchSelectChanged(position: Int) {
        listeners.forEach {
            it.onSelected(position)
        }
    }

    protected fun dispatchDataSizeChanged(size: Int, initSelect: Int) {
        listeners.forEach {
            it.onDataChanged(size, initSelect)
        }
    }

    protected fun getContentSize(): Int {
        val itemCount = bannerContent.adapter?.itemCount ?: 0
        return itemCount / 3
    }

    private fun autoScroll() {
        val dataSize = getContentSize()
        val changed = checkContentAdapterChanged()
        if (changed) {
            if (dataSize > 1) {
                bannerContent.scrollToPosition(0)
            } else {
                bannerContent.scrollToPosition(dataSize)
            }
            if (currentPosition != 0) {
                currentPosition = 0
                dispatchDataSizeChanged(dataSize, 0)
            }
        } else if (dataSize > 1) {
            bannerContent.smoothScrollToPosition(currentPosition + 1)
        }
        if (dataSize > 1) {
            start()
        }
    }

    private fun checkContentAdapterChanged(): Boolean {
        var changed = false
        val current = bannerContent.adapter
        if (lastContentAdapter != current) {
            changed = true
            if (current != null) {
                try {
                    current.registerAdapterDataObserver(contentDataObserver)
                } catch (e: Exception) {
                    //
                }
            }
        }
        lastContentAdapter = current
        return changed
    }
}