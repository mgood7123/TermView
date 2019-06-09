package com.example.libutils.core

/**
 * returns the basename of a string, if the string is **null* then returns **null**
 */
fun basename(s: Any?): String? {
    return when {
        s == null -> null
        s.toString().contains('/') -> s.toString().substringAfterLast('/')
        else -> s.toString()
    }
}
