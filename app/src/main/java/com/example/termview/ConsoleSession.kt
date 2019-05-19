@file:Suppress("unused")

package com.example.TermView

import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.util.Log
import android.view.ViewGroup
import android.widget.ScrollView
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
    console!!.stability = stability
    lifecycle.addObserver(ConsoleUpdater(console))
    if (console.stability == ConsoleSession.Stability.FAST ||
        console.stability == ConsoleSession.Stability.NORMAL) {
        console.load()
    }
    return console
}

@Suppress("MemberVisibilityCanBePrivate")
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

    var session_id = mutableListOf(0)

    var config: RealmConfiguration? = null

    var session = "session"

    var consoleRealm: Realm? = null

    var initialized = false

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
                if (!initialized) {
                    tmp = true
                    load()
                }
                "".updateOverwrite()
                if (tmp) {
                    save()
                    unload()
                }
                Thread.sleep(16)
            }
        } else if (stability == Stability.NORMAL || stability == Stability.STABLE) {
            var tmp = false
            if (stability == Stability.STABLE || !initialized) {
                if (!initialized) tmp = true
                load()
            }
            "".updateOverwrite()
            if (stability == Stability.STABLE || tmp) {
                save()
                unload()
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
                if (!initialized) {
                    tmp = true
                    load()
                }
                message.update()
                if (tmp) {
                    save()
                    unload()
                }
                Thread.sleep(16)
            }
        } else if (stability == Stability.NORMAL || stability == Stability.STABLE) {
            var tmp = false
            if (stability == Stability.STABLE || !initialized) {
                if (!initialized) tmp = true
                load()
            }
            message.update()
            if (stability == Stability.STABLE || tmp) {
                save()
                unload()
            }
        }
    }

    fun println(message: String) = print(message + "\n")

    fun load() {
        // do not load multiple times
        if (initialized) return
        Log.i("REALM", "load started")
        // print cannot be used in this call
        if (!initialized) Realm.init(activity.applicationContext)
        for (it in session_id) {
            val path = "${activity.filesDir}/$session$it"
            if (File(path).exists()) {
                Log.i("REALM", "configuration exists")
                config = RealmConfiguration.Builder()
                    .name("$session$it")
                    .schemaVersion(ConsoleRealmObjectVersion)
                    .migration(MigrateConsole())
                    .build()

                Realm.setDefaultConfiguration(config!!)
                consoleRealm = Realm.getDefaultInstance()
                consoleRealmObjectInstance(consoleRealm!!).stdout.updateOverwrite()
                initialized = true
                Log.i("REALM", "configuration initialized: $session$it at $path")
                break
            }
        }
        if (config == null) {
            Log.i("REALM", "configuration does not exist")
            config = RealmConfiguration.Builder()
                .name("$session${session_id[0]}")
                .schemaVersion(ConsoleRealmObjectVersion)
                .build()
            Realm.setDefaultConfiguration(config!!)
            consoleRealm = Realm.getDefaultInstance()
            consoleRealmObjectInstance(consoleRealm!!).stdout.updateOverwrite()
            initialized = true
            Log.i(
                "REALM",
                "configuration created: $session${session_id[0]} at ${activity.filesDir.toString()}/$session${session_id[0]}"
            )
        }
        Log.i("REALM", "load finished")
    }

    fun save() {
        if (!initialized) {
            Log.i("REALM", "tried to save when already unloaded")
            return
        }

        if (consoleRealm!!.isClosed) {
            Log.i(
                "REALM",
                initialized.ifConditionReturn(
                    TRUE = { "tried to save when initialized however the current REALM is unloaded" },
                    FALSE = { "tried to save when already unloaded" })
            )
            return
        }
        Log.i("REALM", "save started")
        val consoleRealmObject = consoleRealmObjectInstance(consoleRealm!!)
        consoleRealm?.beginTransaction()
        Log.i("REALM", "saving '${output.text.toString()}' to configuration")
        consoleRealmObject.stdout = output.text.toString()
        consoleRealm?.commitTransaction()
        Log.i("REALM", "save finished")
    }

    fun unload() {
        if (!initialized) {
            Log.i("REALM", "tried to unload when already unloaded")
            return
        }

        if (consoleRealm!!.isClosed) {
            Log.i(
                "REALM",
                initialized.ifConditionReturn(
                    TRUE = { "tried to unload when initialized however the current REALM is unloaded" },
                    FALSE = { "tried to unload when already unloaded" })
            )
            return
        }
        Log.i("REALM", "unload started")
        consoleRealm?.close()
        initialized = false
        Log.i("REALM", "unload finished")
    }

}