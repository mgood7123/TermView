package com.example.libutils.core

/**
 * a wrapper for Exception, Default message is **Aborted**
 *
 * if gradle is used, abort using the following
 *
 * import org.gradle.api.GradleException
 *
 * ...
 *
 * throw GradleException(e)
 */
fun abort(e: String = "Aborted"): Nothing {
    throw Exception(e).also {ex ->
        println("stack trace:").also {
            ex.printStackTrace()
        }
    }
}
