package utils.`class`.extensions

import android.app.Activity
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.os.Messenger
import utils.UiThread


fun ThreadWaitForCompletion(UI: UiThread, code: () -> Unit) {
    var CONTINUE = false
    UI.runOnUiThread {
        code()
        CONTINUE = true
    }
    while (!CONTINUE) Thread.sleep(16)
}