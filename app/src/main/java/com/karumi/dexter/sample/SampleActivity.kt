/*
 * Copyright (C) 2015 Karumi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.karumi.dexter.sample

import a.termview.R
import android.Manifest
import android.Manifest.permission.*
import android.app.Activity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.sample_activity.*
import utils.Builder
import utils.PermissionManager


/**
 * Sample activity showing the permission request process with Dexter.
 */
class SampleActivity : Activity() {
    internal var contentView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val activity = this
        super.onCreate(savedInstanceState)
//        setContentView(a.termview.R.layout.sample_activity)
//        audio_permission_button.setOnClickListener {
//            Thread {
//            }.start()
//        }
//        camera_permission_button.setOnClickListener {
//            Thread {
//            }.start()
//        }
//        contacts_permission_button.setOnClickListener {
//            Thread {
//            }.start()
//        }
//
//        all_permissions_button.setOnClickListener {
//        }
//        createPermissionListeners()
//        PermissionManager(this)
//            .Permissions(WRITE_EXTERNAL_STORAGE)
//            .GUI()
//            .make
        val UI = Builder(this)
        UI.row(4) { Button(this) }
        UI.build()
    }

//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
//    fun showPermissionRationale(token: PermissionToken) {
//        AlertDialog.Builder(this).setTitle(R.string.permission_rationale_title)
//            .setMessage(R.string.permission_rationale_message)
//            .setNegativeButton(android.R.string.cancel) { dialog, which ->
//                dialog.dismiss()
//                token.cancelPermissionRequest()
//            }
//            .setPositiveButton(android.R.string.ok) { dialog, which ->
//                dialog.dismiss()
//                token.continuePermissionRequest()
//            }
//            .setOnDismissListener { token.cancelPermissionRequest() }
//            .show()
//    }

    fun showPermissionGranted(permission: String) {
        val feedbackView = getFeedbackViewForPermission(permission)
        feedbackView.setText(a.termview.R.string.permission_granted_feedback)
        feedbackView.setTextColor(ContextCompat.getColor(this, a.termview.R.color.permission_granted))
    }

    fun showPermissionDenied(permission: String, isPermanentlyDenied: Boolean) {
        val feedbackView = getFeedbackViewForPermission(permission)
        feedbackView.setText(
            if (isPermanentlyDenied)
                a.termview.R.string.permission_permanently_denied_feedback
            else
                a.termview.R.string.permission_denied_feedback
        )
        feedbackView.setTextColor(ContextCompat.getColor(this, a.termview.R.color.permission_denied))
    }

    private fun createPermissionListeners() {
/*
        val feedbackViewPermissionListener = SamplePermissionListener(this)
        val feedbackViewMultiplePermissionListener = SampleMultiplePermissionListener(this)

        allPermissionsListener = CompositeMultiplePermissionsListener(
            feedbackViewMultiplePermissionListener,
            SnackbarOnAnyDeniedMultiplePermissionsListener.Builder.with(
                findViewById(android.R.id.content),
                R.string.all_permissions_denied_feedback
            )
                .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
                .build()
        )
        contactsPermissionListener = SampleBackgroundThreadPermissionListener(this)
//        CompositePermissionListener(
//            feedbackViewPermissionListener,
//            SnackbarOnDeniedPermissionListener.Builder.with(
//                findViewById(android.R.id.content),
//                R.string.contacts_permission_denied_feedback
//            )
//                .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
//                .withCallback(object : Snackbar.Callback() {
//                    override fun onShown(snackbar: Snackbar?) {
//                        super.onShown(snackbar)
//                    }
//
//                    override fun onDismissed(snackbar: Snackbar?, event: Int) {
//                        super.onDismissed(snackbar, event)
//                    }
//                })
//                .build()
//        )

        val dialogOnDeniedPermissionListener = DialogOnDeniedPermissionListener.Builder.withContext(this)
            .withTitle(R.string.audio_permission_denied_dialog_title)
            .withMessage(R.string.audio_permission_denied_dialog_feedback)
            .withButtonText(android.R.string.ok)
            .withIcon(R.mipmap.ic_logo_karumi)
            .build()

        cameraPermissionListener = SampleBackgroundThreadPermissionListener(this)

        audioPermissionListener = CompositePermissionListener(
            SampleBackgroundThreadPermissionListener(this),
            dialogOnDeniedPermissionListener
        )

        errorListener = SampleErrorListener()
*/
    }

    private fun getFeedbackViewForPermission(name: String): TextView {
        val feedbackView: TextView

        when (name) {
            CAMERA -> feedbackView = camera_permission_feedback
            READ_CONTACTS -> feedbackView = contacts_permission_feedback
            RECORD_AUDIO -> feedbackView = audio_permission_feedback
            else -> throw RuntimeException("No feedback view for this permission")
        }

        return feedbackView
    }
}
