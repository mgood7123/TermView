package a.termview.floating.hover

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.view.ContextThemeWrapper
import io.mattcarroll.hover.HoverMenu
import io.mattcarroll.hover.HoverView
import io.mattcarroll.hover.window.HoverMenuService
import android.widget.Toast
import com.example.liblayout.UiThread


class DemoHoverMenuService : HoverMenuService() {
    val UI = UiThread(this)

    fun showFloatingMenu(context: Context) {
        context.startService(Intent(context, DemoHoverMenuService::class.java))
    }

    fun destroyFloatingMenu() {
        stopSelf()
    }

    override fun onCreate() {
        super.onCreate()
        if(!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED)) {
            Toast.makeText(
                this,
                "This Service requires WRITE_EXTERNAL_STORAGE to be granted",
                Toast.LENGTH_LONG
            ).show()
            destroyFloatingMenu()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyFloatingMenu()
    }

    override fun getContextForHoverMenu(): Context {
        return ContextThemeWrapper(this, a.termview.R.style.AppTheme)
    }

    override fun onHoverMenuLaunched(intent: Intent, hoverView: HoverView) {
        hoverView.setMenu(createHoverMenu())
        hoverView.collapse()
    }

    private fun createHoverMenu(): HoverMenu {
        return DemoHoverMenu(UI, "Terminal")
    }

    companion object {

        private val TAG = "DemoHoverMenuService"
    }
}
