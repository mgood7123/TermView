package com.example.libutils.core

import android.util.Log
import java.io.File
import java.io.IOException

/**
 * moves one file to another, optionally overwriting it
 * @return true if the operation succeeds, otherwise false
 * @see cp
 */
fun mv(src: String, dest: String, verbose: Boolean = false, overwrite: Boolean = false): Boolean {
    return try {
        File(src).copyTo(File(dest), overwrite)
        if (verbose) Log.i("mv", "$src -> $dest")
        delete(File(src))
    } catch (e: IOException) {
        Log.i("mv", "failed to move $src to $dest")
        false
    }
}
