package a.termview.views.view

import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Layout
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ScrollView
import android.widget.TextView
import utils.StackTraceInfo
import utils.`class`.extensions.ThreadWaitForCompletion
import utils.`class`.extensions.pxToSp
import utils.`class`.extensions.isLessThan
import kotlin.concurrent.thread

/**
 * the Terminal class
 */
class Terminal {

    /**
     * @sample parameters
     */
    private fun parameters() = LayoutParams(MATCH_PARENT, MATCH_PARENT)

    inner class FontFitTextView : TextView {

        constructor(context: Context) : super(context) {
            mainThread = context as Activity
        }

        constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
            mainThread = context as Activity
        }

        var columns = 80
        var ready = false
        private lateinit var mUpdateThread: Thread
        var mStarted: Boolean = false
        var mainThread: Activity? = null
        var WIDTH = 1440
        var HEIGHT = 2960
        private var WIDTHOLD = 1440
        private var HEIGHTOLD = 2960

        fun adjustTextSize(
            paint: Paint,
            text: String,
            numCharacters: Int,
            widthPixels: Int,
            heightPixels: Int
        ): Paint? {
            if (numCharacters == 0 || numCharacters == 0 || widthPixels == 0 || heightPixels == 0) return null
            Log.i(StackTraceInfo().invokingMethodName, "text: \"$text\"")
            var width = paint.measureText(text) * numCharacters / numCharacters
            Log.i(StackTraceInfo().invokingMethodName, "width: $width")
            var newSize = (widthPixels / width * paint.textSize).toInt().toFloat()
            Log.i(StackTraceInfo().invokingMethodName, "newSize: $newSize")
            if (newSize == 0.0f) return null
            paint.textSize = newSize

            // remeasure with font size near our desired result
            width = paint.measureText(text) * numCharacters / numCharacters
            Log.i(StackTraceInfo().invokingMethodName, "width: $width")
            newSize = (widthPixels / width * paint.textSize).toInt().toFloat()
            Log.i(StackTraceInfo().invokingMethodName, "newSize: $newSize")
            if (newSize == 0.0f) return null
            paint.textSize = newSize

            // Check height constraints
            val metrics = paint.fontMetricsInt
            Log.i(StackTraceInfo().invokingMethodName, "metrics.descent: " + metrics.descent.toString())
            Log.i(StackTraceInfo().invokingMethodName, "metrics.ascent: " + metrics.ascent.toString())
            val textHeight = (metrics.descent - metrics.ascent).toFloat()
            Log.i(StackTraceInfo().invokingMethodName, "textHeight: $textHeight")
            Log.i(StackTraceInfo().invokingMethodName, "heightPixels: $heightPixels")
            if (textHeight > heightPixels) {
                newSize = (newSize * (heightPixels / textHeight)).toInt().toFloat()
                Log.i(StackTraceInfo().invokingMethodName, "newSize: $newSize")
                if (newSize == 0.0f) return null
                paint.textSize = newSize
            }
            return paint
        }

        fun DRAW(): Boolean {
            if (HEIGHT == 0 || WIDTH == 0 || columns == 0) return false
            var returnValue = false
            ThreadWaitForCompletion(mainThread!!) {
                var paint: Paint?
                paint = adjustTextSize(getPaint(), " ".repeat(columns), columns, WIDTH, HEIGHT)
                if (paint == null) {
                    Log.i(StackTraceInfo().invokingMethodName(4), "rejected $columns")
                } else {
                    Log.i(StackTraceInfo().invokingMethodName(4), "accepted $columns")
                    paint.textSize = paint.textSize.pxToSp(this.context)
                    textSize = paint.textSize
                    returnValue = true
                }
            }
            return returnValue
        }

        fun DRAWTHREAD() {
            if (!mStarted) {
                mStarted = true
                mUpdateThread = thread {
                    while (true) {
                        if (ready) {
                            ready = false
                            DRAW()
                            break
                        }
                        Thread.sleep(16)
                    }
                }
            } else {
                DRAW()
            }
        }

        override fun append(text: CharSequence?, start: Int, end: Int) {
//            Log.i(utils.StackTraceInfo().invokingMethodName, "appending '$text' to '${this.text}'")
            super.append(text, start, end)
        }

        override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
            super.onTextChanged(text, start, lengthBefore, lengthAfter)
//            Log.i(utils.StackTraceInfo().invokingMethodName, "text updated to '$text'")
            DRAWTHREAD()
            // TODO: reformat text due to wrapping and text size in order to wrap correctly
        }
    }

    inner class Zoomable : ScrollView {
        constructor(context: Context) : super(context)

        constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

        private var mScaleFactor = 1f
        private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {

                // do not allow scrolling during touch event
                requestDisallowInterceptTouchEvent(true)

                // get text a.view
                val child = getChildAt(0) as FontFitTextView

                // set up some restoration values on event of failure
                val previousMScaleFactor = mScaleFactor
                val previousColumns = child.columns

                // do scale
                mScaleFactor *= detector.scaleFactor
                Log.i(StackTraceInfo().currentMethodName, "PREVIOUS $previousMScaleFactor")
                Log.i(StackTraceInfo().currentMethodName, "CURRENT  $mScaleFactor")
                var mode = 0
                val ZOOM_IN = 1
                val ZOOM_OUT = 2
                val ZOOM_NO_CHANGE = 3
                when {
                    mScaleFactor < previousMScaleFactor -> mode = ZOOM_IN
                    mScaleFactor > previousMScaleFactor -> mode = ZOOM_OUT
                    mScaleFactor == previousMScaleFactor -> mode = ZOOM_NO_CHANGE

                }
                when (mode) {
                    ZOOM_IN -> {
                        Log.i(StackTraceInfo().currentMethodName, "zooming in")
                        if (mScaleFactor isLessThan 1f) {
                            mScaleFactor = previousMScaleFactor
                            return false
                        }
                        if (child.columns == 1) {
                            Log.i(StackTraceInfo().currentMethodName, "EXCEEDS")
                            // Don't let the object exceed maximum scale
                            mScaleFactor = previousMScaleFactor
                            return false
                        } else {
                            Log.i(StackTraceInfo().currentMethodName, "DOES NOT EXCEED")
                        }
                    }
                    ZOOM_OUT -> {
                        Log.i(StackTraceInfo().currentMethodName, "zooming out")
                        if (child.textSize == 1.0f) {
                            mScaleFactor = previousMScaleFactor
                            return false
                        }
                    }
                    ZOOM_NO_CHANGE -> {
                        Log.i(StackTraceInfo().currentMethodName, "no change")
                    }
                }

                child.columns = mScaleFactor.toInt()

                Log.i(StackTraceInfo().currentMethodName, "mScaleFactor = $mScaleFactor")
                Log.i(StackTraceInfo().currentMethodName, "new columns are ${child.columns}")
                Log.i(StackTraceInfo().currentMethodName, "SCALED")
                requestDisallowInterceptTouchEvent(false)
                return if (child.DRAW()) {
                    Log.i(StackTraceInfo().currentMethodName, "DRAW RETURNS TRUE")
                    invalidate()
                    true
                } else {
                    Log.i(StackTraceInfo().currentMethodName, "DRAW RETURNS FALSE")
                    mScaleFactor = previousMScaleFactor
                    child.columns = previousColumns
                    false
                }
            }
        }
        private val mScaleDetector = ScaleGestureDetector(context, scaleListener)

        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            Log.i(StackTraceInfo().currentMethodName, "CHANGED")
            val child = getChildAt(0) as FontFitTextView
            child.HEIGHT = h
            child.WIDTH = w
            child.ready = true
            child.DRAWTHREAD()
            super.onSizeChanged(w, h, oldw, oldh)
        }


        override fun onTouchEvent(ev: MotionEvent): Boolean {
            // Let the ScaleGestureDetector inspect all events.
            mScaleDetector.onTouchEvent(ev)
            super.onTouchEvent(ev)
            return true
        }
    }

    /**
     * creates a new terminal a.view
     *
     * this is main terminal a.view function in which will return a terminal suitable for text output
     *
     * if attached to a scrollable View the Terminal may become inaccessible during resizing,
     * this is due to the terminal a.view's requirement of always being accessible during resizing of its text
     *
     * @param activity the main UI activity, this is required to resize text in the event that its layout is not yet
     * measurable
     *
     * @see termViewInit
     */

    internal fun termView(activity: Activity): Zoomable {
        val textView = FontFitTextView(activity).also {
            it.layoutParams = parameters()
            it.setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
            it.hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NONE
            it.breakStrategy = Layout.BREAK_STRATEGY_SIMPLE
        }
        val scroll = Zoomable(activity)
        scroll.addView(textView)
        scroll.layoutParams = parameters()
        return scroll
    }
}