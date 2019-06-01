package a.termview.views.widget

import android.app.Activity
import android.util.Log
import android.view.Gravity
import android.widget.*
import a.termview.ConsoleSession
import a.termview.ConsoleSessionInit
import a.termview.views.view.Terminal
import android.os.Handler
import utils.Builder
import utils.UiThread

@Suppress("unused")
fun popUp(UI: UiThread, activity: Activity, terminals: Int) {

    // width and height shall be fixed to 1000 pixels
    val maxWidth = 1000
    val maxHeight = 1000
    val UI: UiThread = UI
    val popUp: PopupWindow = PopupWindow(activity)
    val layout = AbsoluteLayout(activity)
    popUp.contentView = layout
    popUp.showAtLocation(layout, Gravity.CENTER, 0, 0)
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
            val x = ConsoleSessionInit(UI, activity.applicationContext)
            x.output.columns = 13
            x.consoleSession.println("test message")
            x.screen
        }
    }
    BUILD.build(layout)
}