package utils

import a.termview.views.view.Terminal
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.provider.Settings
import android.support.constraint.ConstraintLayout
import android.util.Log
import android.widget.Toast
import github.nisrulz.packagehunter.PackageHunter


//import android.content.pm.PackageManager
//import org.xmlpull.v1.XmlPullParserException
//import sun.security.pkcs11.wrapper.Functions.getAttributeName
//import org.xmlpull.v1.XmlPullParser
//import android.content.res.XmlResourceParser
//import sun.invoke.util.VerifyAccess.getPackageName
//import android.content.res.AssetManager
//fun getListOfPermissions(context: Context): String {
//    var _permissions = ""
//
//    try {
//        val _am = context.createPackageContext(context.getPackageName(), 0).getAssets()
//        val _xmlParser = _am.openXmlResourceParser(0, "AndroidManifest.xml")
//        var _eventType = _xmlParser.getEventType()
//        while (_eventType != XmlPullParser.END_DOCUMENT) {
//            if (_eventType == XmlPullParser.START_TAG && "uses-permission" == _xmlParser.getName()) {
//                for (i in 0 until _xmlParser.getAttributeCount()) {
//                    if (_xmlParser.getAttributeName(i) == "name") {
//                        _permissions += _xmlParser.getAttributeValue(i) + "\n"
//                    }
//                }
//            }
//            _eventType = _xmlParser.nextToken()
//        }
//        _xmlParser.close() // Pervents memory leak.
//    } catch (exception: XmlPullParserException) {
//        exception.printStackTrace()
//    } catch (exception: PackageManager.NameNotFoundException) {
//        exception.printStackTrace()
//    } catch (exception: IOException) {
//        exception.printStackTrace()
//    }
//
//    return _permissions
//}



class PermissionManager(
    private val activity: Activity
) {

    val permission = PermissionData()
    val protection = permission.dataByProtection
    var data = protection.none
    var permissionLevel: Int = permission.protectionLevelNone
    var page = 0
    var previousSize = 0
    var size = 4
    var hasNextPage = true
    var total = 0
    var increaseBy = 0

    inner class Permissions(private vararg val permissionArray: String) {
        inner class GUI {
            val make: Unit
                get() {
                    size = 4
                    val LAYOUT = ConstraintLayout(activity.applicationContext)

                    data class X(
                        val screen: Terminal.ConstraintLayoutFontFitTextView,
                        val output: Terminal.FontFitTextView
                    )

                    fun console(): X {
                        val output = Terminal().FontFitTextView(UiThread(activity), activity).also {
                            it.layoutParams = Terminal().parameters()
                            it.AutoFitBasedOnUserCode = true
                            it.AutoFitCode = {
                                it.text.toString().longestWord().length + 1
                            }
                        }
                        val screen = Terminal().ConstraintLayoutFontFitTextView(activity)
                        screen.addView(output)
                        screen.layoutParams = Terminal().parameters()
                        screen.requestDisallowInterceptTouchEvent(true)
                        return X(screen, output)
                    }

                    val UI = Builder(activity)
                    fun button() = a.termview.views.view.Button(
                        context = activity,
                        view = console(),
                        ViewRetrievalMethod = {
                            (it as X).screen
                        },
                        TextViewRetrievalMethod = {
                            (it as X).output
                        }
                    )
                    UI
                        .row()
                        .column {
                            button().also {
                                it.text = "ADD CALL LOG PERMISSION"
                                it.setOnClickListener {
                                    // find apk
                                    val pkg = "a.termview"
                                    Log.i("pkg", "pkg = $pkg")
                                    val apk = APK().getApkFile(
                                        activity.applicationContext,
                                        pkg
                                    )
                                    Log.i("apk", "apk = $apk")
                                    // extract apk
//                                    brut.apktool.Main.main(arrayOf("decode $apk -o /data/local/tmp/$pkg"))
                                    // modify manifest
                                    // rebuild apk
                                    // sign apk
                                    // uninstall original apk
                                    // install modified apk


                                    // location=/data/local/tmp
                                    // curl https://github.com/jakev/android-binaries/raw/master/unzip -o unzip -L
                                    // curl https://bitbucket.org/iBotPeaches/apktool/downloads/apktool_2.4.0.jar -o apktool_2.4.0.jar -L

                                    // chmod +x unzip

                                    // find package path
                                    // package="a.termview"
                                    // path=$(pm path $package | sed s/package\://)
                                    // echo path of $package is $path
                                }
                            }
                        }
                        .row()
                        .column {
                            button().also {
                                it.text = "PREVIOUS PERMISSION LEVEL"
                                it.setOnClickListener {
                                    page = 0
                                    previousSize = 0
                                    total = 0
                                    permissionLevel = when (permissionLevel) {
                                        permission.protectionLevelNone -> {
                                            data = protection.dangerous
                                            permission.protectionLevelDangerous
                                        }
                                        permission.protectionLevelNormal -> {
                                            data = protection.none
                                            permission.protectionLevelNone
                                        }
                                        permission.protectionLevelSignature -> {
                                            data = protection.normal
                                            permission.protectionLevelNormal
                                        }
                                        permission.protectionLevelPrivileged -> {
                                            data = protection.signature
                                            permission.protectionLevelSignature
                                        }
                                        permission.protectionLevelDangerous -> {
                                            data = protection.privileged
                                            permission.protectionLevelPrivileged
                                        }
                                        else -> throw ArithmeticException()
                                    }
                                    make
                                }
                            }
                        }
                        .column {
                            button().also {
                                val level = permission.permissionLevelToString(permissionLevel)
                                it.text = "PERMISSION LEVEL: $level"
                                // if clicked, attempt to grant all permissions of this type
                                it.setOnClickListener {
                                    Toast.makeText(
                                        activity.applicationContext,
                                        "grant all permissions for permission level $level",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                        .column {
                            button().also {
                                it.text = "NEXT PERMISSION LEVEL"
                                it.setOnClickListener {
                                    page = 0
                                    previousSize = 0
                                    total = 0
                                    permissionLevel = when (permissionLevel) {
                                        permission.protectionLevelNone -> {
                                            data = protection.normal
                                            permission.protectionLevelNormal
                                        }
                                        permission.protectionLevelNormal -> {
                                            data = protection.signature
                                            permission.protectionLevelSignature
                                        }
                                        permission.protectionLevelSignature -> {
                                            data = protection.privileged
                                            permission.protectionLevelPrivileged
                                        }
                                        permission.protectionLevelPrivileged -> {
                                            data = protection.dangerous
                                            permission.protectionLevelDangerous
                                        }
                                        permission.protectionLevelDangerous -> {
                                            data = protection.none
                                            permission.protectionLevelNone
                                        }
                                        else -> throw ArithmeticException()
                                    }
                                    make
                                }
                            }
                        }
                        .row()
                        .column {
                            button().also {
                                (it.currentView as X).output.AutoFitCode = {
                                    it.text.toString().longestLine().length
                                }
                                it.text = "PREVIOUS PAGE"
                                it.setOnClickListener {
                                    if (page != 0) {
                                        page--
                                        make
                                    }
                                }
                            }
                        }
                        .column {
                            button().also {
                                (it.currentView as X).output.AutoFitCode = {
                                    it.text.toString().longestLine().length
                                }
                                it.text = "page: $page"
                                // if clicked, attempt to grant all permissions in this tab
                                it.setOnClickListener {
                                    Toast.makeText(
                                        activity.applicationContext,
                                        "grant all permissions on page $page",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                        .column {
                            button().also {
                                (it.currentView as X).output.AutoFitCode = {
                                    it.text.toString().longestLine().length
                                }
                                it.text = "NEXT PAGE"
                                it.setOnClickListener {
                                    if (hasNextPage) {
                                        page++
                                        make
                                    }
                                }
                            }
                        }
                        .row()
                        .height(75)
                        .column(LAYOUT)
                        .build()
                    // TODO: tabs
                    // for tabs, the available space will be calculated to fit in a 3*3 (9) or 4*4 (16) grid view
                    // as pages
                    val R = Builder(UiThread(activity.applicationContext), LAYOUT, false)
                    val sz = data.size - 1
                    if (sz < (4*4)) size = 3
                    if (sz < (size*size)) hasNextPage = false
                    Log.e("TAG", "previousSize = $previousSize")
                    val cs = if (previousSize != 0) {
                        var counter = 0 + (page * previousSize)
                        var counterNext = 0 + ((page+1) * previousSize)
                        Log.e("TAG", "sz = $sz")
                        Log.e("TAG", "counter = $counter")
                        Log.e("TAG", "counterNext = $counterNext")
                        Log.e("TAG", "data.size - counter = ${data.size - counter}")
                        Log.e("TAG", "total = $total")
                        Log.e("TAG", "increaseBy = $increaseBy")
                        Log.e("TAG", "total + increaseBy = ${total + increaseBy}")
                        Log.e("TAG", "data.size - total = ${data.size - total}")
                        if (sz < counterNext) {
                            hasNextPage = false
                            data.size - counter
//                        } else {
//                            hasNextPage = false
//                            data.size - total
//                        }
//                        if (sz < (total + increaseBy)) {
//                            if (data.size - counter == 13) { // (3*3) + (2*2) == 9 + 4 = 13
//                                hasNextPage = true
//                                size = 3
//                                size * size
//                            } else {
//                                hasNextPage = false
//                                data.size - total
//                            }
                        } else {
                            hasNextPage = true
                            size * size
                        }
                    } else {
                        hasNextPage = true
                        size * size
                    }
                    Log.e("TAG", "previous total = $total")
                    total += cs
                    increaseBy = cs
                    Log.e("TAG", "next total = $total")
                    Log.e("TAG", "cs = $cs")
                    val x = Builder
                        .ConvertCellsToValidGrid()
                        .convertCellsToValidGrid(cs)!!
                    previousSize = size * size
                    var counter = 0 + (page * previousSize)
                    Log.e("TAG", "counter = $counter")
                    Log.e("TAG", "counter = ${0 + (page * previousSize)}")
                    Log.e("TAG", "counter = ${0 + (page * (total - increaseBy))}")
                    x.forEach { row ->
                        R.row()
                        repeat(row.columns) {
                            R.column {
                                Log.e("TAG", "using data[$counter]")
                                val currentPermission = data[counter]
                                button().also {
                                    it.text = currentPermission.nameEnglish
                                    it.setOnClickListener { _ ->
                                        Toast.makeText(
                                            activity.applicationContext,
                                            "attempting to grant permission ${currentPermission.name} " +
                                                    "lineCount is ${(it.currentView as X).output.lineCount}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        request(currentPermission.name, 123)
                                    }
                                }
                            }
                            counter++
                        }
                    }
                    R.build()
                }
        }

        private fun stage1(permission: String, id: Int, permissionArray: List<String>) {
            when (permissionArray[0]) {
                "android", "com" -> stage2(permission, id, permissionArray)
                else -> throw Exception("request: Unknown permission type: ${permissionArray[0]} ($permission)")
            }
        }

        private fun stage2(permission: String, id: Int, permissionArray: List<String>) {
            when (permissionArray[1]) {
                "permission", "permission-group" -> activity.requestPermissions(arrayOf(permission), id)
                "settings", "android" -> stage3(permission, id, permissionArray)
                else -> throw Exception("request: Unknown permission type: ${permissionArray[1]} ($permission)")
            }
        }

        private fun stage3(permission: String, id: Int, permissionArray: List<String>) {
            when (permissionArray[2]) {
                "action" -> {
                    val URI = Uri.parse("package:${activity.packageName}")
                    activity.startActivityForResult(Intent(permission, URI), 1)
                }
                "launcher", "alarm", "voicemail" -> stage4(permission, id, permissionArray)
                else -> throw Exception("request: Unknown permission type: ${permissionArray[2]} ($permission)")
            }
        }

        private fun stage4(permission: String, id: Int, permissionArray: List<String>) {
            when (permissionArray[3]) {
                "permission", "permission-group" -> activity.requestPermissions(arrayOf(permission), id)
                else -> throw Exception("request: Unknown permission type: ${permissionArray[3]} ($permission)")
            }
        }

        fun request(permission: String, id: Int) {
            stage1(permission, id, permission.split('.'))
        }

        fun requestAllRemaining(): Boolean {
            for ((index, it) in permissionArray.withIndex()) {
                if (!check(it)) {
                    request(it, index)
//                    if (!check(it)) return false
                }
            }
            return true
        }

        fun check(permission: String): Boolean {
            val permissionArray = permission.split('.')
            return when (permissionArray[0]) {
                "android" -> when (permissionArray[1]) {
                    "permission", "permission-group" -> activity.checkSelfPermission(permission) == PERMISSION_GRANTED
                    "settings" -> when (permissionArray[2]) {
                        "action" -> when (permissionArray[3]) {
                            "MANAGE_OVERLAY_PERMISSION" -> Settings.canDrawOverlays(activity.applicationContext)
                            else -> throw Exception("check: Unknown permission: ${permissionArray[3]} ($permission)")
                        }
                        else -> throw Exception("check: Unknown permission type: ${permissionArray[2]} ($permission)")
                    }
                    else -> throw Exception("check: Unknown permission type: ${permissionArray[1]} ($permission)")
                }
                else -> throw Exception("check: Unknown permission type: ${permissionArray[0]} ($permission)")
            }
        }

        fun checkAll(): Boolean {
            permissionArray.forEach {
                if (!check(it)) return false
            }
            return true
        }

        fun getpermissions(pkg: String) {
            val ph = PackageHunter(activity.applicationContext)
            val pkgprm = ph.getPermissionForPkg(pkg)
            pkgprm.forEach {
                Log.i("pkgprm", "pkgprm contains $it")
            }
        }

    }
}

fun String.longestLine(): String = this.longestSubString('\n')
fun String.longestWord(): String = this.longestSubString(' ')

fun String.longestSubString(delimiter: Char): String {
    var longest = 0
    var line: String = ""
    this.split(delimiter).forEach {
        it.trim()
        if (it.length > longest) {
            longest = it.length
            line = it
        }
    }
    return line
}
