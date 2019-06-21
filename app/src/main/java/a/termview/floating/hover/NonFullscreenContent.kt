package a.termview.floating.hover

import a.termview.ConsoleData
import a.termview.ConsoleSessionInit
import android.content.Context
import android.os.Environment.getExternalStorageDirectory
import android.view.View
import android.widget.AbsoluteLayout
import android.widget.Button
import io.mattcarroll.hover.Content
import com.example.liblayout.Builder
import com.example.libperm.PermissionManager
import com.example.liblayout.UiThread
import java.io.File

class NonFullscreenContent(val UI: UiThread) : Content { // DemoHoverMenu

    private val mContext: Context = UI.context.applicationContext
    private var mContent: View? = null

    override fun getView(): View {
        if (null == mContent) {
            mContent = AbsoluteLayout(mContext).also {
                val BUILD = Builder(UI, it)

                BUILD.row(1) {
                    val b = Button(mContext)
                    b.text = "press this button to send a message to the terminal below"
                    b.setOnClickListener {
                        val fifoPath = getExternalStorageDirectory().path +
                                "/" +
                                UI.context.applicationInfo.packageName
                        (UI.DATA[0] as ConsoleData).consoleSession.println("fifoPath = $fifoPath")
                        val fifoNumber = (UI.DATA[1] as Int).toString()
                        (UI.DATA[0] as ConsoleData).consoleSession.println("fifoNumber = $fifoNumber")
                        val fifoLocation = File(fifoPath + "/" + fifoNumber)
                        (UI.DATA[0] as ConsoleData).consoleSession.println("fifoLocation = $fifoLocation")
                        val fifoDirectory = File(fifoPath)
                        (UI.DATA[0] as ConsoleData).consoleSession.println("fifoDirectory = $fifoDirectory")
                        if (fifoDirectory.exists()) {
                            (UI.DATA[0] as ConsoleData).consoleSession.println("fifoDirectory exists")
                        } else {
                            (UI.DATA[0] as ConsoleData).consoleSession.println("fifoDirectory does not exist")
                            (UI.DATA[0] as ConsoleData).consoleSession.println(
                                "attempting to create fifoDirectory"
                            )
                            val x = fifoDirectory.mkdir()
                            (UI.DATA[0] as ConsoleData).consoleSession.println(
                                "fifoDirectory.mkdirs() returned $x"
                            )
                            if (fifoDirectory.exists()) {
                                (UI.DATA[0] as ConsoleData).consoleSession.println("fifoDirectory exists")
                            } else {
                                (UI.DATA[0] as ConsoleData).consoleSession.println(
                                    "fifoDirectory still does not exist"
                                )
                            }
                        }
//                        if (fifoLocation.exists()) {
//                            (UI.DATA[0] as ConsoleData).consoleSession.println(
//                                "Fifo $fifoLocation already exists"
//                            )
//                        } else {
//                            Os.mkfifo(fifoLocation.path, OsConstants.S_IFIFO)
//                            Os.chmod(
//                                fifoLocation.path,
//                                OsConstants.S_IRUSR or
//                                        OsConstants.S_IWUSR or
//                                        OsConstants.S_IRGRP or
//                                        OsConstants.S_IWGRP or
//                                        OsConstants.S_IROTH or
//                                        OsConstants.S_IWOTH
//                            )
//                            (UI.DATA[0] as ConsoleData).consoleSession.println(
//                                "Created Fifo at $fifoLocation"
//                            )
//                        }
                        UI.DATA[1] = (UI.DATA[1] as Int) + 1
                    }
                    b
                }
                BUILD.row(1) {
                    val x = ConsoleSessionInit(UI, mContext)
                    UI.DATA.add(x)
                    UI.DATA.add(0)
                    x.output.columns = 30
                    x.screen
                }
                BUILD.build()
            }
        }
        return mContent!!
    }

    override fun isFullscreen() = true

    override fun onShown() {
        // No-op.
    }

    override fun onHidden() {
        // No-op.
    }
}
