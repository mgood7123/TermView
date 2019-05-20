package com.example.termview

import android.app.Activity
import android.util.Log
import com.example.TermView.*
import com.example.termview.utils.baseName
import com.utils.StackTraceInfo
import io.realm.Realm
import io.realm.RealmConfiguration
import java.io.File

@Suppress("KDocMissingDocumentation", "unused", "UNUSED_VARIABLE")
class SessionManager(val activity: Activity) {
    val TAG = StackTraceInfo().currentClassName
    val initialized = false
    fun newSession() {

    }

    val FILESDIR = activity.filesDir
    val FILESDIRASASTRING = FILESDIR.toString()

    val prefix: String = "Session"

    val sessions: MutableList<File>
        get() {
            var id = 0
            val sessions = mutableListOf<File>()
            FILESDIR.listFiles().forEach {
                if (it.extension.isEmpty()) {
                    val base = File(baseName(it.toString()))
                    Log.i(TAG, "found $base at $it")
                    sessions.add(base)
                }
            }
            return sessions
        }

    val sessionsRunning: MutableList<File>
        get() {
            val sessionsRunning = mutableListOf<File>()
            sessions.forEach {
                val base = "$it.running"
                val full = "$FILESDIRASASTRING/$base"
                if (File(full).exists()) {
                    Log.i(TAG, "$base is running")
                    sessionsRunning.add(it)
                } else {
                    Log.i(TAG, "$base is not running")
                }
            }
            return sessionsRunning
        }

    val sessionsFree: MutableList<File>
        get() {
            val sessionsFree = mutableListOf<File>()
            sessions.forEach {
                val base = "$it.running"
                val full = "$FILESDIRASASTRING/$base"
                if (File(full).exists()) {
                    Log.i(TAG, "$base is running")
                } else {
                    Log.i(TAG, "$base is not running")
                    sessionsFree.add(it)
                }
            }
            return sessionsFree
        }

    val sessionsNextAvailable: String
        get() {
            var next = 0
            var ret: String? = null
            for (it in sessions) {
                val base = "${it.toString().dropLast(1)}$next"
                val full = "$FILESDIRASASTRING/$base"
                if (!File(full).exists()) {
                    ret = baseName(full)
                    break
                }
                next++
            }
            if (ret == null) {
                ret = "$prefix$next"
            }
            return ret
        }

    var sessionCurrent: String? = null

    fun sessionIsRunning(session: String?): Boolean = if (session != null)
        File("$FILESDIRASASTRING/$session.running").exists()
    else false

    fun load(): ConsoleRealmObject {
        val s = sessions
        lateinit var c: String
        if (s.isEmpty()) {
            c = "${prefix}0"
            var config = RealmConfiguration.Builder()
                .name(c)
                .schemaVersion(ConsoleRealmObjectVersion)
                .build()
            Realm.setDefaultConfiguration(config!!)
            Log.i(TAG, "configuration created: $c at $FILESDIRASASTRING/$c")
        } else {
            val fs = sessionsFree
            if (fs.isEmpty()) {
                Log.i(TAG, "no free sessions found")
                c = sessionsNextAvailable
                var config = RealmConfiguration.Builder()
                    .name(c)
                    .schemaVersion(ConsoleRealmObjectVersion)
                    .build()
                Realm.setDefaultConfiguration(config!!)
                Log.i(TAG, "configuration created: $c at $FILESDIRASASTRING/$c")
            } else c = fs.last().toString()
            Log.i(TAG, "using session '$c'")
            val config = RealmConfiguration.Builder()
                .name(c)
                .schemaVersion(ConsoleRealmObjectVersion)
                .migration(MigrateConsole())
                .build()

            Realm.setDefaultConfiguration(config!!)
            Log.i(TAG, "configuration loaded: $c at $FILESDIRASASTRING/$c")
        }
        File("$FILESDIRASASTRING/$c.running").createNewFile()
        sessionCurrent = c
        return consoleRealmObjectInstance(Realm.getDefaultInstance())
    }

    fun save(CRO: ConsoleRealmObject, output: Terminal.FontFitTextView) {
        if (sessionIsRunning(sessionCurrent)) {
            Log.i(TAG, "save started")
            val consoleRealm = CRO.realm
            consoleRealm?.beginTransaction()
//            Log.i(TAG, "saving '${output.text.toString()}' to configuration")
            CRO.stdout = output.text.toString()
            consoleRealm?.commitTransaction()
            Log.i(TAG, "save finished")

        }
//        if (!initialized) {
//            Log.i(TAG, "tried to save when already unloaded")
//            return
//        }
//
//        if (consoleRealm!!.isClosed) {
//            Log.i(
//                TAG,
//                initialized.ifConditionReturn(
//                    TRUE = { "tried to save when initialized however the current REALM is unloaded" },
//                    FALSE = { "tried to save when already unloaded" })
//            )
//            return
//        }
    }

    fun unload(CRO: ConsoleRealmObject) {
//        if (!initialized) {
//            Log.i(TAG, "tried to unload when already unloaded")
//            return
//        }
//
//        if (consoleRealm!!.isClosed) {
//            Log.i(
//                TAG,
//                initialized.ifConditionReturn(
//                    TRUE = { "tried to unload when initialized however the current REALM is unloaded" },
//                    FALSE = { "tried to unload when already unloaded" })
//            )
//            return
//        }
        if (sessionIsRunning(sessionCurrent)) {
            Log.i(TAG, "unload started")
            CRO.realm.close()
            File("$FILESDIRASASTRING/${sessionCurrent!!}.running").delete()
            sessionCurrent = null
            Log.i(TAG, "unload finished")
        }
    }

}