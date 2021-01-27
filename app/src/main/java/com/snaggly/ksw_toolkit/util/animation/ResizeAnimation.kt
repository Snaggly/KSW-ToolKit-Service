package com.snaggly.ksw_toolkit.util.animation

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

class ResizeAnimation(private val view: View?, private val startWidth: Int, endW: Int, startH: Int, endH: Int) : Animation() {
    private val deltaWidth : Int = endW - startWidth // distance between start and end height
    private val startHeight: Int = startH
    private val deltaHeight: Int = endH - startH
    private val originalWidth: Int = view!!.width
    private val originalHeight: Int = view!!.height
    private var fillEnabled = false
    override fun setFillEnabled(enabled: Boolean) {
        fillEnabled = enabled
        super.setFillEnabled(enabled)
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        if (interpolatedTime.toDouble() == 1.0 && !fillEnabled) {
            view!!.layoutParams.height = originalHeight
            view.layoutParams.width = originalWidth
        } else {
            if (deltaHeight != 0) view!!.layoutParams.height = (startHeight + deltaHeight * interpolatedTime).toInt()
            if (deltaWidth != 0) view!!.layoutParams.width = (startWidth + deltaWidth * interpolatedTime).toInt()
        }
        view!!.requestLayout()
    }

}