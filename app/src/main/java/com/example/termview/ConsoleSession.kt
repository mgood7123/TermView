@file:Suppress("unused")

package com.example.termview

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.example.termview.views.view.Terminal
import utils.`class`.extensions.ThreadWaitForCompletion
import kotlin.concurrent.thread

@Suppress("unused")
fun scrollDown(activity: Activity, scrollView: Terminal.Zoomable) = thread {
    activity.runOnUiThread {
        // in java, the code to be executed is required to be encapsulated in
        // new Runnable() { public void run() { <CODE> } }
        // in kotlin, this does NOT work and will fail
        // see https://kotlinlang.org/docs/reference/java-interop.html#sam-conversions
        // SAM stands for Single Abstract Method
        scrollView.post {
            scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }
}

fun ConsoleSessionInit(activity: Activity, viewGroup: ViewGroup): ConsoleSession {
    val t = Terminal().termView(activity)
    viewGroup.addView(t)
    return ConsoleSession(activity, t.getChildAt(0) as Terminal.FontFitTextView, t).also {
        it.output.columns = 32
    }
}

@Suppress("MemberVisibilityCanBePrivate")
class ConsoleSession(
    val activity: Activity,
    val output: Terminal.FontFitTextView,
    val screen: Terminal.Zoomable
) {

    inner class TTY() {
        val stdin = ""
    }

    fun CharSequence.update() = this.toString().update()
    fun CharSequence.updateOverwrite() = this.toString().updateOverwrite()

    fun String.update() {
        ThreadWaitForCompletion(activity) {
            output.append(this)
            scrollDown(activity, screen)
        }
    }

    fun String.updateOverwrite() {
        ThreadWaitForCompletion(activity) {
            output.text = this
            scrollDown(activity, screen)
        }
    }

    fun clear() = "".updateOverwrite()

    // loading on every print may produce large overhead on print intensive tasks,
    // such as printing a large document lne by line, heavy debug output
    fun print(message: String) = message.update()

    fun println(message: String) = print(message + "\n")
}