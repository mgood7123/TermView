package com.example.libutils.extensions

import java.io.File

/**
 * converts an [Array][kotlin.Array] into a [String][kotlin.String]
 * @return the resulting conversion
 * @see Array.toByteArray
 */
fun <T> Array<T>.string(): String {
    val result = StringBuffer()
    for ((index, it) in this.withIndex()) result.append(it.toString() + if (index == this.size) "" else " ")
    return result.toString()
}

/**
 * converts an [Array][kotlin.Array] into a [ByteArray]
 * @return the resulting conversion
 * @see Array.string
 */
fun <T> Array<T>.toByteArray(): ByteArray = this.string().toByteArray()

/**
 * writes an [Array][kotlin.Array] into a temporary [File]
 * @return the resulting [File]
 */
fun <T> Array<T>.ToFile(prefix: String) = File.createTempFile(prefix, null).also {
    it.outputStream().also {
        it.write(this.toByteArray())
        it.close()
    }
}

/**
 * @see kotlin.Array.drop
 */
fun javaArrayStringDropOne(javaArray: Array<String>): Array<String> {
    return javaArray.drop(1).toTypedArray()
}
