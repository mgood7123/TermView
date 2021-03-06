package a.termview.activities


import a.termview.R
import a.termview.floating.hover.DemoHoverMenuService
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import com.example.libperm.PermissionManagerActivity
import com.github.johnkal.appcrashhandlerlibrary.AppCrashHandler
import java.lang.Thread.setDefaultUncaughtExceptionHandler

/**
 * set to false to allow crashes to be handled by AppCrashHandler
 */
const val IDE_DEBUGGING: Boolean = true

class ConsoleV2 : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!IDE_DEBUGGING) setDefaultUncaughtExceptionHandler(
            AppCrashHandler.Builder()
                .setActivity(this)
                .setTitle("Error")
                .setDescription("An error occured. Do you want to send report to developer?")
                .setBackgroundColor(R.color.background_material_dark)
                .setTitleColor(R.color.white)
                .setDescriptionColor(R.color.white)
                .setIcon(R.drawable.ic_bug_report_black_24dp)
                .setPositiveText(R.string.send)
                .setNegativeText(R.string.cancel)
                .setTakeScreenshot(false)
                .setEmailTo("smallville7123@gmail.com")
                .setEmailSubject("App Crash")
                .create()
        )
        startActivity(Intent(applicationContext, PermissionManagerActivity::class.java))
//        launch()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun launch() {
        if (Settings.canDrawOverlays(this)) {

            // Launch service right away - the user has already previously granted permission
            launchMainService()
        } else {
            Toast.makeText(
                this,
                "Sorry. Can't draw overlays without permission, trying again in 5 seconds",
                Toast.LENGTH_LONG
            ).show()
            // Check that the user has granted permission, and prompt them if not
            checkDrawOverlayPermission()
        }
    }

    private fun launchMainService() {
        applicationContext.startService(Intent(applicationContext, DemoHoverMenuService::class.java))
    }

    private fun checkDrawOverlayPermission() {

        // Checks if app already has permission to draw overlays
        if (!Settings.canDrawOverlays(this)) {

            // Launch Intent, with the supplied request code
            startActivityForResult(
                Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                ), REQUEST_CODE
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // Check if a request code is received that matches that which we provided for the overlay draw request
        if (requestCode == REQUEST_CODE) {

            // Double-check that the user granted it, and didn't just dismiss the request
            if (Settings.canDrawOverlays(this)) {

                // Launch the service
                launchMainService()
            }
        }
    }

    companion object {

        val REQUEST_CODE = 10101
    }
}
