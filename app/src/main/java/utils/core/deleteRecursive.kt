package utils.core

import android.util.Log
import java.io.File

/**
 * deletes **src** and all sub directories
 * @return true if the operation succeeds, otherwise false
 */
fun deleteRecursive(src: File): Boolean {
    if (!src.exists()) {
        Log.i("deleteRecursively", "deletion of ${src.path} failed: file or directory does not exist")
        return false
    }
    if (!src.deleteRecursively()) {
        Log.i("deleteRecursively", "deletion of \"${src.path}\" failed")
        return false
    }
    return true
}
