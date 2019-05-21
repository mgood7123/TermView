package com.example.termview.views.widget

import android.app.Activity
import android.util.Log
import android.view.Gravity
import android.widget.*
import com.example.termview.ConsoleSession
import com.example.termview.views.view.Terminal
import utils.Builder

@Suppress("unused")
fun popUp(activity: Activity, terminals: Int) {

    // width and height shall be fixed to 1000 pixels
    val maxWidth = 1000
    val maxHeight = 1000

    val popUp: PopupWindow = PopupWindow(activity)
    val layout = AbsoluteLayout(activity)
    popUp.contentView = layout
    popUp.showAtLocation(layout, Gravity.CENTER, 0, 0);
    popUp.update(0, 0, maxWidth, maxHeight)

    val BUILD = Builder(maxHeight, maxWidth)

    BUILD.row(1) {
        val b = Button(activity)
        b.setOnClickListener { popUp.dismiss() }
        b.text = "dismiss"
        b
    }

    val tableLayout =
        Builder.ConvertCellsToValidGrid().convertCellsToValidGrid(terminals)!!
    tableLayout.forEach {
        Log.e("T", "adding a row with ${it.columns} columns")
        BUILD.row(it.columns) {
            val t = Terminal().termView(activity)
            val console = ConsoleSession(activity, t.getChildAt(0) as Terminal.FontFitTextView, t).also {
                it.output.columns = 32
            }
            console.println("test message")
            t
        }
    }
    BUILD.build(layout)
}