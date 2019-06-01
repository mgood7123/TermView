/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package a.termview.floating.hover

import android.content.Context
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.util.TypedValue
import android.view.View

/**
 * Visual representation of a top-level tab in a Hover menu.
 */
class DemoTabView(context: Context, private var mIconDrawable: Drawable?) : View(context) {

    private var mBackgroundColor: Int = 0
    private var mForegroundColor: Int? = null
    private var mIconInsetLeft: Int = 0
    private var mIconInsetTop: Int = 0
    private var mIconInsetRight: Int = 0
    private var mIconInsetBottom: Int = 0

    init {
        init()
    }

    private fun init() {
        val insetsDp =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, context.resources.displayMetrics).toInt()
        mIconInsetBottom = insetsDp
        mIconInsetRight = mIconInsetBottom
        mIconInsetTop = mIconInsetRight
        mIconInsetLeft = mIconInsetTop
    }

    fun setTabBackgroundColor(@ColorInt backgroundColor: Int) {
        mBackgroundColor = backgroundColor
        mIconDrawable!!.setColorFilter(mBackgroundColor, PorterDuff.Mode.SRC_ATOP)
    }

    fun setTabForegroundColor(@ColorInt foregroundColor: Int?) {
        mForegroundColor = foregroundColor
        if (null != mForegroundColor) {
            mIconDrawable!!.setColorFilter(mForegroundColor!!, PorterDuff.Mode.SRC_ATOP)
        } else {
            mIconDrawable!!.colorFilter = null
        }
    }

    fun setIcon(icon: Drawable?) {
        mIconDrawable = icon
        if (null != mForegroundColor && null != mIconDrawable) {
            mIconDrawable!!.setColorFilter(mForegroundColor!!, PorterDuff.Mode.SRC_ATOP)
        }
        updateIconBounds()

        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // Make circle as large as View minus padding.
        mIconDrawable!!.setBounds(paddingLeft, paddingTop, w - paddingRight, h - paddingBottom)

        // Re-size the icon as necessary.
        updateIconBounds()

        invalidate()
    }

    private fun updateIconBounds() {
        if (null != mIconDrawable) {
            val bounds = Rect(mIconDrawable!!.bounds)
            bounds.set(
                bounds.left + mIconInsetLeft,
                bounds.top + mIconInsetTop,
                bounds.right - mIconInsetRight,
                bounds.bottom - mIconInsetBottom
            )
            mIconDrawable!!.bounds = bounds
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (null != mIconDrawable) {
            mIconDrawable!!.draw(canvas)
        }
    }
}
