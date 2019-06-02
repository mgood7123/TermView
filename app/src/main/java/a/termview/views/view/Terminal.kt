package a.termview.views.view

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.support.constraint.ConstraintLayout
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
import utils.UiThread
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
    fun parameters(): LayoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)

    inner class FontFitTextView : TextView {

        var AutoFitBasedOnUserCode = false

        var AutoFitCode: () -> Int = { columns }

        var AutoFitDisabled = false

        constructor(UI: UiThread, context: Context) : super(context) {
            this.UI = UI
        }

        constructor(UI: UiThread, context: Context, attrs: AttributeSet) : super(context, attrs) {
            this.UI = UI
        }

        var columns = 80
        var ready = false
        private lateinit var mUpdateThread: Thread
        var mStarted: Boolean = false
        var UI: UiThread
        var WIDTH = 1440
        var HEIGHT = 2960
        private var WIDTHOLD = 1440
        private var HEIGHTOLD = 2960
        var hasline = false
        var reason = 0
        val reasonSizeZero = 1
        val reasonWontFit = 2
        val conditionSizeChange = 1
        val conditionTextChange = 2
        val conditionScaling = 3

        fun adjustTextSize(
            paint: Paint,
            text: String,
            numCharacters: Int,
            widthPixels: Int,
            heightPixels: Int
        ): Paint? {
            reason = 0
            Log.i(StackTraceInfo().invokingMethodName, "text: $text")
            Log.i(StackTraceInfo().invokingMethodName, "numCharacters: $numCharacters")
            Log.i(StackTraceInfo().invokingMethodName, "heightPixels: $heightPixels")
            Log.i(StackTraceInfo().invokingMethodName, "widthPixels: $widthPixels")
            if (numCharacters == 0 || numCharacters == 0 || widthPixels == 0 || heightPixels == 0) return null
            Log.i(StackTraceInfo().invokingMethodName, "text: \"$text\"")
            var width = paint.measureText(text) * numCharacters / numCharacters
            Log.i(StackTraceInfo().invokingMethodName, "width: $width")
            var newSize = (widthPixels / width * paint.textSize).toInt().toFloat()
            Log.i(StackTraceInfo().invokingMethodName, "newSize: $newSize")
            if (newSize == 0.0f) {
                reason = reasonSizeZero
                return null
            }
            paint.textSize = newSize

            // remeasure with font size near our desired result
            width = paint.measureText(text) * numCharacters / numCharacters
            Log.i(StackTraceInfo().invokingMethodName, "width: $width")
            Log.i(StackTraceInfo().invokingMethodName, "width per character: ${ width / numCharacters}")
            newSize = (widthPixels / width * paint.textSize).toInt().toFloat()
            Log.i(StackTraceInfo().invokingMethodName, "newSize: $newSize")
            if (newSize == 0.0f) {
                reason = reasonSizeZero
                return null
            }
            paint.textSize = newSize

            // Check height constraints
            val metrics = paint.fontMetricsInt
            Log.i(StackTraceInfo().invokingMethodName, "metrics.descent: " + metrics.descent.toString())
            Log.i(StackTraceInfo().invokingMethodName, "metrics.ascent: " + metrics.ascent.toString())
            val textHeight = (metrics.descent - metrics.ascent).toFloat()
            Log.i(StackTraceInfo().invokingMethodName, "textHeight: $textHeight")
            Log.i(StackTraceInfo().invokingMethodName, "heightPixels: $heightPixels")
            Log.i(StackTraceInfo().invokingMethodName, "line count: $lineCount")
            Log.i(StackTraceInfo().invokingMethodName, "textHeight * lineCount: ${textHeight * lineCount}")
            if ((textHeight * lineCount) > heightPixels) {
                reason = reasonWontFit
                return null
            }
            if (textHeight > heightPixels) {
                newSize = (newSize * (heightPixels / textHeight)).toInt().toFloat()
                Log.i(StackTraceInfo().invokingMethodName, "newSize: $newSize")
                if (newSize == 0.0f) {
                    reason = reasonSizeZero
                    return null
                }
                paint.textSize = newSize
            }
            return paint
        }

        fun DRAW(condition: Int): Boolean {
            if (!AutoFitDisabled) {
                if (AutoFitBasedOnUserCode) {
                    columns = AutoFitCode()
                }
            }
            if (HEIGHT == 0 || WIDTH == 0 || columns == 0 || lineCount == 0) return false
            var returnValue = false
            Log.i(StackTraceInfo().invokingMethodName, "DRAWTHREAD1 condition: $condition")
            ThreadWaitForCompletion(UI!!) {
                val Condition = condition
                var paint: Paint?
                Log.i(StackTraceInfo().invokingMethodName, "DRAW line count: ${getLineCount()}")
                Log.i(StackTraceInfo().invokingMethodName, "DRAW line count: $lineCount")
                Log.i(StackTraceInfo().invokingMethodName, "DRAW has line: $hasline")
                Log.i(StackTraceInfo().invokingMethodName, "DRAWTHREAD2 condition: $condition")
                Log.i(StackTraceInfo().invokingMethodName, "DRAWTHREAD2 Condition: $Condition")
                Log.i(StackTraceInfo().invokingMethodName, "DRAW text: $text")
                paint = adjustTextSize(getPaint(), " ".repeat(columns), columns, WIDTH, HEIGHT)
                Log.i(StackTraceInfo().invokingMethodName, "line count: ${getLineCount()}")
                if (paint == null) {
                    if (reason == reasonSizeZero) {
                        Log.i(
                            StackTraceInfo().invokingMethodName(4),
                            "rejected $columns: size resulted in 0"
                        )
                    } else if (
                        reason == reasonWontFit &&
                        (Condition == conditionTextChange || Condition == conditionSizeChange)
                    ) {
                        Log.i(
                            StackTraceInfo().invokingMethodName(4),
                            "rejected $columns: size will not fit for text $text"
                        )
                        AutoFitDisabled = true
                        columns++
                        returnValue = DRAW(condition)
                        AutoFitDisabled = false
                    }
                } else {
                    Log.i(StackTraceInfo().invokingMethodName(4), "accepted $columns")
                    Log.i(StackTraceInfo().invokingMethodName, "line count: ${getLineCount()}")
                    paint.textSize = paint.textSize.pxToSp(this.context)
                    Log.i(StackTraceInfo().invokingMethodName, "line count: ${getLineCount()}")
                    textSize = paint.textSize
                    Log.i(StackTraceInfo().invokingMethodName, "line count: ${getLineCount()}")
                    returnValue = true
                }
            }
            return returnValue
        }

        fun DRAWTHREAD(condition: Int) {
            if (text.isEmpty() || condition == 0) return
            Log.i(StackTraceInfo().invokingMethodName, "DRAWTHREAD0 condition: $condition")
            if (!mStarted) {
                mStarted = true
                // queue until layout height and width are known
                mUpdateThread = thread {
                    while (true) {
                        if (ready) {
                            ready = false
                            Log.i(StackTraceInfo().invokingMethodName, "DRAWTHREAD0 condition: $condition")
                            DRAW(condition)
                            break
                        }
                        Thread.sleep(16)
                    }
                }
            } else {
                DRAW(condition)
            }
        }

        override fun append(text: CharSequence?, start: Int, end: Int) {
            super.append(text, start, end)
        }

        override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
            super.onTextChanged(text, start, lengthBefore, lengthAfter)
            Log.i(
                StackTraceInfo().invokingMethodName,
                "CALLED FOR TEXT $text with conditionTextChange value of $conditionTextChange"
            )
            DRAWTHREAD(conditionTextChange)
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
                return if (child.DRAW(child.conditionScaling)) {
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
            child.DRAWTHREAD(child.conditionSizeChange)
            super.onSizeChanged(w, h, oldw, oldh)
        }


        override fun onTouchEvent(ev: MotionEvent): Boolean {
            // Let the ScaleGestureDetector inspect all events.
            mScaleDetector.onTouchEvent(ev)
            super.onTouchEvent(ev)
            return true
        }
    }

    inner class ConstraintLayoutFontFitTextView : ConstraintLayout {
        constructor(context: Context) : super(context)

        constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            Log.i(StackTraceInfo().currentMethodName, "CHANGED")
            val child = getChildAt(0) as FontFitTextView
            child.HEIGHT = h
            child.WIDTH = w
            child.ready = true
            child.DRAWTHREAD(child.conditionSizeChange) // this gets called when the layout is ready to be drawn
            super.onSizeChanged(w, h, oldw, oldh)
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

    internal fun termView(UI: UiThread, context: Context): Zoomable {
        val output = FontFitTextView(UI, context).also {
            it.layoutParams = parameters()
            it.setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
            it.hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NONE
            it.breakStrategy = Layout.BREAK_STRATEGY_SIMPLE
            it.setTextColor(android.graphics.Color.WHITE)
            it.isClickable = true
            it.isFocusable = true
            it.setTextIsSelectable(true)
        }
        val screen = Zoomable(context)
        screen.setBackgroundColor(android.graphics.Color.BLACK)
        screen.addView(output)
        screen.layoutParams = parameters()
        return screen
    }
}