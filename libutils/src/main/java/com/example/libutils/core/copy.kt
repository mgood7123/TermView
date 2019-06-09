package com.example.libutils.core

import android.util.Log
import java.io.File
import java.io.IOException

/**
 * copy one file to another, optionally overwriting it
 * @return true if the operation succeeds, otherwise false
 * @see mv
 */
fun cp(src: String, dest: String, verbose: Boolean = false, overwrite: Boolean = false): Boolean {
    return try {
        File(src).copyTo(File(dest), overwrite)
        if (verbose) Log.i("cp", "$src -> $dest")
        true
    } catch (e: IOException) {
        Log.i("cp", "failed to copy file $src to $dest")
        false
    }
}