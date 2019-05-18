@file:Suppress("unused")

package com.example.TermView

import android.os.Bundle
import android.os.SystemClock.sleep
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.termview.R
import com.example.termview.utils.`class`.extensions.minute
import com.example.termview.utils.`class`.extensions.second
import com.utils.`class`.extensions.ThreadWaitForCompletion
import kotlinx.android.synthetic.main.activity_console.*
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis


@Suppress("MemberVisibilityCanBePrivate")
class Console : AppCompatActivity() {

    var console: ConsoleSession? = null

    fun clear(@Suppress("UNUSED_PARAMETER") view: View) = console?.clear()
    @Suppress("SpellCheckingInspection")
    fun exe(@Suppress("UNUSED_PARAMETER") view: View) = try {
        // dont block the UI thread
        thread {
            fun sample(code: () -> Unit) {
                val time = measureTimeMillis {
                    code()
                }
                console?.println("printing sample took $time milliseconds")
            }

            fun sampleLoop(itterations: Int, code: (itteration: Int) -> Unit) {
                val loopTime = measureTimeMillis {
                    for (i in 1..itterations) {
                        code(i)
                    }
                }
                console?.println("printing $itterations itterations without delay took $loopTime milliseconds")
            }

            fun sampleLoop(itterations: Int, delay: Int, code: (itteration: Int) -> Unit) {
                val loopTime = measureTimeMillis {
                    for (i in 1..itterations) {
                        code(i)
                        sleep(delay.toLong())
                    }
                }
                console?.println("printing $itterations itterations with a $delay ms delay after every iteration took $loopTime milliseconds")
            }
            sampleLoop(1000) { console?.println("sample number $it") }
        }
//        val cmd = "stty -a"
//        val cmdwhite = "printf \\\\e[?5h"
//        val cmdblack = "printf \\\\e[?5l"
//        console?.println("executing command: $cmd")
//        val p = ProcessBuilder("myCommand", "myArg")
//        val pro = p.start()
//        Runtime.getRuntime().exec("stty -a").also {
//            it.inputStream.bufferedReader().useLines { it.forEach { console?.println("stdout: $it") } }
//            it.errorStream.bufferedReader().useLines { it.forEach { console?.println("stderr: $it") } }
//        }
    } catch (e: InterruptedException) {
        e.printStackTrace()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_console)

        val t = Terminal().termView(this)
        Terminal.addView(t)
        if (console == null) {
            console = ConsoleSession(t.getChildAt(0) as Terminal.FontFitTextView, t)
            console!!.stability = ConsoleSession.Stability.NORMAL
            lifecycle.addObserver(ConsoleUpdater(console!!))
        }
        val consoleSession = console!!
        if (consoleSession.stability == ConsoleSession.Stability.FAST ||
            consoleSession.stability == ConsoleSession.Stability.NORMAL) {
            consoleSession.load()
        }
        console?.println("onCreate")
    }

    override fun onStart() {
        super.onStart()
        console?.println("onStart")
    }

    override fun onRestart() {
        super.onRestart()
        console?.println("onRestart")
    }

    override fun onResume() {
        super.onResume()
        val consoleSession = console!!
        if (consoleSession.stability == ConsoleSession.Stability.FAST ||
            consoleSession.stability == ConsoleSession.Stability.NORMAL) {
            consoleSession.load()
        }
        console?.println("onResume")
    }

    override fun onPause() {
        super.onPause()
        val consoleSession = console!!
        if (consoleSession.stability == ConsoleSession.Stability.FAST ||
            consoleSession.stability == ConsoleSession.Stability.NORMAL) {
            consoleSession.save()
            consoleSession.unload()
        }
        console?.println("onPause")
    }

    override fun onStop() {
        super.onStop()
        console?.println("onStop")
        // this is also called when entering/exiting Pop-Up view on samsung devices
    }

    override fun onDestroy() {
        super.onDestroy()
        console?.println("onDestroy")
        // this is also called when entering/exiting Pop-Up view on samsung devices
    }
}
