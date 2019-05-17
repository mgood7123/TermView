package com.utils.`class`.extensions

import android.app.Activity

fun ThreadWaitForCompletion(activity: Activity, code: () -> Unit) {
    var CONTINUE = false
    activity.runOnUiThread {
        code()
        CONTINUE = true
    }
    while (!CONTINUE) Thread.sleep(16)

}