package com.example.libutils.extensions

import com.example.liblayout.UiThread


fun ThreadWaitForCompletion(UI: com.example.liblayout.UiThread, code: () -> Unit) {
    var CONTINUE = false
    UI.runOnUiThread {
        code()
        CONTINUE = true
    }
    while (!CONTINUE) Thread.sleep(16)
}