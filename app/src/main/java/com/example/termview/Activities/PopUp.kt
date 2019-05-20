package com.example.termview.Activities

import android.widget.LinearLayout
import android.view.Gravity
import android.widget.TextView
import android.widget.PopupWindow
import android.os.Bundle
import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import android.widget.Button


class PopUp : Activity() {


    public fun onCreate(context: Context) {
//        super.onCreate(savedInstanceState)
        lateinit var popUp: PopupWindow
        lateinit var layout: LinearLayout
        lateinit var tv: TextView
        lateinit var params: ViewGroup.LayoutParams
        lateinit var mainLayout: LinearLayout
        lateinit var but: Button
        var click = true
        popUp = PopupWindow(this)
        layout = LinearLayout(this)
        mainLayout = LinearLayout(this)
        tv = TextView(this)
        but = Button(this)
        but.text = "Click Me"
        but.setOnClickListener {
            click = if (click) {
                popUp.showAtLocation(layout, Gravity.BOTTOM, 10, 10)
                popUp.update(50, 50, 300, 80)
                false
            } else {
                popUp.dismiss()
                true
            }
        }
        params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layout.orientation = LinearLayout.VERTICAL
        tv.text = "Hi this is a sample text for popup window"
        layout.addView(tv, params)
        popUp.contentView = layout
        // popUp.showAtLocation(layout, Gravity.BOTTOM, 10, 10);
        mainLayout.addView(but, params)
        setContentView(mainLayout)
    }
}