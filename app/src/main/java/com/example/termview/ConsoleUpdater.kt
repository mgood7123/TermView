package com.example.TermView

import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.view.View
import android.widget.ScrollView
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

@Suppress("unused", "RedundantExplicitType", "UNUSED_VARIABLE")
class ConsoleUpdater(
    private val consoleSession: ConsoleSession
) : LifecycleObserver {
    lateinit var mUpdateThread: Thread
    var mStopThread: Boolean = false

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun create() = resume()

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun start() = resume()

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun resume() {
//        if (consoleSession.stability == ConsoleSession.Stability.FAST ||
//            consoleSession.stability == ConsoleSession.Stability.NORMAL) {
//            consoleSession.load()
//        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun pause() {
//        if (consoleSession.stability == ConsoleSession.Stability.FAST ||
//            consoleSession.stability == ConsoleSession.Stability.NORMAL) {
//            consoleSession.save()
//            consoleSession.unload()
//        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() = pause()

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() = pause()
}