@file:Suppress("unused")

package com.example.termview.Activities

import android.os.Bundle
import android.os.SystemClock.sleep
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.example.TermView.ConsoleSession
import com.example.TermView.ConsoleSessionInit
import com.example.termview.R
import kotlinx.android.synthetic.main.activity_console.*
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis


@Suppress("MemberVisibilityCanBePrivate", "KDocMissingDocumentation")
class Console : AppCompatActivity() {

    var console: ConsoleSession? = null

    @Suppress("KDocMissingDocumentation")
    fun clear(@Suppress("UNUSED_PARAMETER") view: View) {
        val popUp: PopupWindow = PopupWindow(this)
        val layout: LinearLayout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        popUp.contentView = layout
        popUp.showAtLocation(layout, Gravity.CENTER, 0, 0);
        popUp.update(50, 50, 1000, 1000)
        val console = ConsoleSessionInit(
            this,
            lifecycle,
            layout,
            ConsoleSession.Stability.STABLE
        )
        console.println("onCreate")
//        popUp.dismiss()
    }

    @Suppress("KDocMissingDocumentation")
    fun exe(@Suppress("UNUSED_PARAMETER") view: View): Any = try {
        // don't block the UI thread
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
        console = ConsoleSessionInit(
            this,
            lifecycle,
            Terminal,
            ConsoleSession.Stability.STABLE
        )
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
