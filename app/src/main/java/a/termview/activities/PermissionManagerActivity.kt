package a.termview.activities

import android.app.Activity
import android.os.Bundle
import utils.PermissionManager

class PermissionManagerActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val x = PermissionManager(this)
            .Permissions(
//                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                WRITE_EXTERNAL_STORAGE,
//                READ_CONTACTS,
                // CAMERA exists as both a group and an individual permission
            )
//        x.requestAllRemaining()
//        val y = x.checkAll()
//        val UI = Builder(this)
//        UI.row(1) { currentTextView(this).also {it.text = "y returned $y"} }
//        UI.build()

        x.GUI().make
    }
}