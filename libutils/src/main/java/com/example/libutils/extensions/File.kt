package com.example.libutils.extensions

import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer

/**
 * converts a [File] into a [ByteBuffer]
 * @return the resulting conversion
 * @see String.toByteArray
 * @see File.toByteArray
 */
fun File.toByteBuffer(): ByteBuffer {
    val file = RandomAccessFile(this, "r")
    val fileChannel = file.channel
    val buffer = ByteBuffer.allocate(fileChannel.size().toInt())
    fileChannel.read(buffer)
    buffer.flip()
    return buffer
}

/**
 * converts a [File] into a [ByteArray]
 * @return the resulting conversion
 * @see String.toByteBuffer
 * @see File.toByteBuffer
 */
fun File.toByteArray(): ByteArray {
    return this.toByteBuffer().array()
}
