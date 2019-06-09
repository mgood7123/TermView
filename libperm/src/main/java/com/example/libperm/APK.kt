package com.example.libperm

import android.content.pm.PackageManager
import java.nio.file.Files.exists
import android.R.attr.versionCode
import android.R.attr.versionName
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.util.Log
import java.io.File


class APK {
    private fun getAllInstalledApkFiles(context: Context): HashMap<String, String> {
        val installedApkFilePaths = HashMap<String, String>()

        val packageManager = context.getPackageManager()
        val packageInfoList = packageManager.getInstalledPackages(PackageManager.GET_SIGNATURES)

        if (isValid(packageInfoList)) {
            for (packageInfo in packageInfoList) {
                val applicationInfo: ApplicationInfo

                try {
                    applicationInfo = getApplicationInfoFrom(packageManager, packageInfo)

                    val packageName = applicationInfo.packageName
                    val versionName = packageInfo.versionName
                    val versionCode = packageInfo.versionCode

                    val apkFile = File(applicationInfo.publicSourceDir)
                    if (apkFile.exists()) installedApkFilePaths.put(packageName, apkFile.getAbsolutePath())
                } catch (error: PackageManager.NameNotFoundException) {
                    error.printStackTrace()
                }

            }
        }

        return installedApkFilePaths
    }

    private fun isValid(packageInfos: List<PackageInfo>?): Boolean {
        return packageInfos != null && !packageInfos.isEmpty()
    }

    fun getApkFile(context: Context, packageName: String): File? {
        val installedApkFilePaths = getAllInstalledApkFiles(context)
        val apkFile = File(installedApkFilePaths[packageName]!!)
        return if (apkFile.exists()) {
            apkFile
        } else null
    }

    private fun getApplicationInfoFrom(packageManager: PackageManager, packageInfo: PackageInfo): ApplicationInfo {
        return packageInfo.applicationInfo
    }
}