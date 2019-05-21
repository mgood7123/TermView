@file:Suppress("unused")

package com.example.termview.activities

import android.os.Bundle
import android.os.SystemClock.sleep
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.termview.ConsoleSession
import com.example.termview.ConsoleSessionInit
import com.example.termview.R
import kotlinx.android.synthetic.main.activity_console.*
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis


@Suppress("MemberVisibilityCanBePrivate", "KDocMissingDocumentation")
class Console : AppCompatActivity() {

    var console: ConsoleSession? = null

    @Suppress("KDocMissingDocumentation")
    fun clear(@Suppress("UNUSED_PARAMETER") view: View) {
        popUp(this, 1)
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
        console = ConsoleSessionInit(this, Terminal)
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
        console?.println("onResume")
    }

    override fun onPause() {
        super.onPause()
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
