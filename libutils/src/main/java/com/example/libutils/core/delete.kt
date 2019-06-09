package com.example.libutils.core

import android.util.Log
import java.io.File

/**
 * deletes **src**
 * @return true if the operation succeeds, otherwise false
 */
fun delete(src: File): Boolean {
    if (!src.exists()) {
        Log.i("delete", "deletion of ${src.path} failed: file or directory does not exist")
        return false
    }
    if (!src.delete()) {
        Log.i("delete", "deletion of \"${src.path}\" failed")
        return false
    }
    return true
}
