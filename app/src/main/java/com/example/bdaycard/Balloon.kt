package com.example.bdaycard

import android.animation.Animator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.util.TypedValue
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.AppCompatImageView
import androidx.navigation.fragment.NavHostFragment


class Balloon : AppCompatImageView, Animator.AnimatorListener, AnimatorUpdateListener {
    private val mAnimator: ValueAnimator by lazy { ValueAnimator() }
    private val mListener: BalloonListener by lazy {
        ((this.context as MainActivity).supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.primaryNavigationFragment) as BalloonListener }
    private var mPopped = false

    constructor(context: Context) : super(context)
    constructor(context: Context, color: Int, rawHeight: Int) : super(context) {
        setImageResource(R.drawable.balloon)
        setColorFilter(color)
        val rawWidth = rawHeight / 2
        val dpHeight = pixelsToDp(rawHeight, context)
        val dpWidth = pixelsToDp(rawWidth, context)
        val params = ViewGroup.LayoutParams(dpWidth, dpHeight)
        this.layoutParams = params
    }

    fun releaseBalloon(screenHeight: Int, duration: Int) {
        mAnimator.duration = duration.toLong()
        mAnimator.setFloatValues(screenHeight.toFloat(), 0f)
        mAnimator.interpolator = LinearInterpolator()
        mAnimator.setTarget(this)
        mAnimator.addListener(this)
        mAnimator.addUpdateListener(this)
        mAnimator.start()
    }

    override fun onAnimationStart(animator: Animator) {}
    override fun onAnimationEnd(animator: Animator) {
        if (!mPopped) {
            mListener.popBalloon(this, false)
        }
    }

    override fun onAnimationCancel(animator: Animator) {
        if (!mPopped) {
            mListener.popBalloon(this, false)
        }
    }

    override fun onAnimationRepeat(animator: Animator) {}
    override fun onAnimationUpdate(valueAnimator: ValueAnimator) {
        y = (valueAnimator.animatedValue as Float)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!mPopped && event.action == MotionEvent.ACTION_DOWN) {
            mListener.popBalloon(this, true)
            mPopped = true
            mAnimator.cancel()
        }

//        return super.onTouchEvent(event);
        return true
    }

    fun setPopped(popped: Boolean) {
        mPopped = popped
        if (popped) {
            mAnimator.cancel()
        }
    }

    interface BalloonListener {
        fun popBalloon(balloon: Balloon?, userTouch: Boolean)
    }

    companion object {
        fun pixelsToDp(px: Int, context: Context): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, px.toFloat(),
                context.resources.displayMetrics
            ).toInt()
        }
    }
}