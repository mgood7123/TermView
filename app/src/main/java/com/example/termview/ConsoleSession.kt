@file:Suppress("unused")

package com.example.TermView

import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.util.Log
import android.view.ViewGroup
import android.widget.ScrollView
import com.example.termview.SessionManager
import com.utils.`class`.extensions.ThreadWaitForCompletion
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.activity_console.*
import preprocessor.utils.`class`.extensions.ifConditionReturn
import java.io.File
import kotlin.concurrent.thread

fun ConsoleSessionInit(activity: Activity, lifecycle: Lifecycle, viewGroup: ViewGroup, stability: Int): ConsoleSession {
    val t = Terminal().termView(activity)
    viewGroup.addView(t)
    val console = ConsoleSession(activity, t.getChildAt(0) as Terminal.FontFitTextView, t)
    console.stability = stability
    lifecycle.addObserver(ConsoleUpdater(console))
    Realm.init(activity)
    if (console.stability == ConsoleSession.Stability.FAST ||
        console.stability == ConsoleSession.Stability.NORMAL) {
        console.SM.load()
    }
    return console
}

@Suppress("MemberVisibilityCanBePrivate", "KDocMissingDocumentation")
class ConsoleSession(
    val activity: Activity,
    val output: Terminal.FontFitTextView,
    val screen: Terminal.Zoomable
) {

    open class Stability {

        companion object : Stability()

        val STABLE get() = 0
        val NORMAL get() = 1
        val FAST get() = 2
    }

    var stability = Stability.NORMAL

    inner class TTY() {
        val stdin = ""
    }

    val SM = SessionManager(activity)

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

    fun clear() {
        if (stability == Stability.FAST) {
            thread {
                // make sure we can still safely print if the REALM is unloaded
                var tmp = false
                if (!SM.sessionIsRunning(SM.sessionCurrent)) {
                    tmp = true
                    SM.load()
                }
                "".updateOverwrite()
                if (tmp) {
                    SM.save()
                    SM.unload()
                }
                Thread.sleep(16)
            }
        } else if (stability == Stability.NORMAL || stability == Stability.STABLE) {
            var tmp = false
            if (stability == Stability.STABLE || !SM.sessionIsRunning(SM.sessionCurrent)) {
                if (!SM.sessionIsRunning(SM.sessionCurrent)) tmp = true
                SM.load()
            }
            "".updateOverwrite()
            if (stability == Stability.STABLE || tmp) {
                SM.save()
                SM.unload()
            }
        }
    }

    // loading on every print may produce large overhead on print intensive tasks,
    // such as printing a large document lne by line, heavy debug output
    fun print(message: String) {
        // make sure we can still safely print if the REALM is unloaded


        // using a thread here directly can speed this up significantly however it causes Out-Of-Order execution in the
        // thread, or race condition, as well as INTENSE CPU load
        if (stability == Stability.FAST) {
            thread {
                // make sure we can still safely print if the REALM is unloaded
                var tmp = false
                if (!SM.sessionIsRunning(SM.sessionCurrent)) {
                    tmp = true
                    SM.load()
                }
                message.update()
                if (tmp) {
                    SM.save()
                    SM.unload()
                }
                Thread.sleep(16)
            }
        } else if (stability == Stability.NORMAL || stability == Stability.STABLE) {
            var tmp = false
            if (stability == Stability.STABLE || !SM.sessionIsRunning(SM.sessionCurrent)) {
                if (!SM.sessionIsRunning(SM.sessionCurrent)) tmp = true
                SM.load()
            }
            message.update()
            if (stability == Stability.STABLE || tmp) {
                SM.save()
                SM.unload()
            }
        }
    }

    fun println(message: String) = print(message + "\n")

}