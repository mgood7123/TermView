package com.example.TermView

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.view.View
import kotlin.concurrent.thread

@Suppress("unused")
fun scrollDown(scrollView: Terminal.Zoomable) = thread {
    scrollView.mainThread?.runOnUiThread {
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

@Suppress("unused", "RedundantExplicitType", "UNUSED_VARIABLE")
class ConsoleUpdater(
    private val console: Console
) : LifecycleObserver
{
    lateinit var mUpdateThread : Thread
    var mStopThread : Boolean = false

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun start()
    {
        console.console?.println("threadStart")
        mUpdateThread = thread {
            while(true) {
                if (mStopThread) {
                    mStopThread = false
                    break
                }
                Thread.sleep(16)
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun pause()
    {
        console.console?.println("threadStop")
        this.mStopThread = true
    }
}