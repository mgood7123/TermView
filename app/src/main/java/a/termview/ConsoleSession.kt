@file:Suppress("unused")

package a.termview

import android.view.View
import android.view.ViewGroup
import android.content.Context
import com.example.libutils.extensions.ThreadWaitForCompletion
import com.example.liblayout.UiThread
import com.example.libperm.Terminal
import kotlin.concurrent.thread

@Suppress("unused")
fun scrollDown(scrollView: Terminal.Zoomable) = thread {
    // in java, the code to be executed is required to be encapsulated in
    // new Runnable() { public void run() { <CODE> } }
    // in kotlin, this does NOT work and will fail
    // see https://kotlinlang.org/docs/reference/java-interop.html#sam-conversions
    // SAM stands for Single Abstract Method
    scrollView.post {
        scrollView.fullScroll(View.FOCUS_DOWN)
    }
}

fun ConsoleSessionInit(UI: UiThread, context: Context, viewGroup: ViewGroup): ConsoleSession {
    val t = Terminal().termView(UI, context)
    viewGroup.addView(t)
    val c = ConsoleSession(UI, t.getChildAt(0) as Terminal.FontFitTextView, t).also {
        it.output.columns = 32
    }
    return c
}

data class ConsoleData(val screen: Terminal.Zoomable, val output: Terminal.FontFitTextView, val consoleSession: ConsoleSession)

fun ConsoleSessionInit(UI: UiThread, context: Context): ConsoleData {
    val screen= Terminal().termView(UI, context)
    val output = screen.getChildAt(0) as Terminal.FontFitTextView
    return ConsoleData(
        screen,
        output,
        ConsoleSession(
            UI,
            output,
            screen
        ).also {
            it.output.columns = 32
        }
    )
}

@Suppress("MemberVisibilityCanBePrivate")
class ConsoleSession(
    val UI: UiThread,
    val output: Terminal.FontFitTextView,
    val screen: Terminal.Zoomable
) {

    inner class TTY() {
        val stdin = ""
    }

    fun CharSequence.update() = this.toString().update()
    fun CharSequence.updateOverwrite() = this.toString().updateOverwrite()

    fun String.update() {
        ThreadWaitForCompletion(UI) {
            output.append(this)
            scrollDown(screen)
        }
    }

    fun String.updateOverwrite() {
        ThreadWaitForCompletion(UI) {
            output.text = this
            scrollDown(screen)
        }
    }

    fun clear() = "".updateOverwrite()

    // loading on every print may produce large overhead on print intensive tasks,
    // such as printing a large document lne by line, heavy debug output
    fun print(message: String) = message.update()

    fun println(message: String) = print(message + "\n")
}