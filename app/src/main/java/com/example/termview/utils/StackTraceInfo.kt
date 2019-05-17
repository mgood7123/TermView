@file:Suppress("unused")

package com.utils

import android.util.Log
import preprocessor.utils.`class`.extensions.ifConditionReturn

/** Utility class: Getting the name of the current executing method
 * https://stackoverflow.com/questions/442747/getting-the-name-of-the-current-executing-method
 *
 * Provides:
 *
 *      currentClassName()
 *      currentMethodName()
 *      currentFileName()
 *
 *      invokingClassName()
 *      invokingMethodName()
 *      invokingFileName()
 *
 * Nb. Using StackTrace's to get this info is expensive. There are more optimised ways to obtain
 * method names. See other stackoverflow posts eg. https://stackoverflow.com/questions/421280/in-java-how-do-i-find-the-caller-of-a-method-using-stacktrace-or-reflection/2924426#2924426
 *
 * 29/09/2012 (lem) - added methods to return (1) fully qualified names and (2) invoking class/method names
 */
class StackTraceInfo {
    /* (Lifted from virgo47's stackoverflow answer) */
    private val CLIENT_CODE_STACK_INDEX: Int

    // making additional overloaded method call requires +1 offset
    val currentMethodName: String
        get() = currentMethodName(1)

    // making additional overloaded method call requires +1 offset
    val currentClassName: String
        get() = currentClassName(1)

    // making additional overloaded method call requires +1 offset
    val currentFileName: String
        get() = currentFileName(1)

    val invokingMethodName: String
        get() = invokingMethodName(2)

    val invokingClassName: String
        get() = invokingClassName(2)

    val invokingFileName: String
        get() = invokingFileName(2)

    val currentMethodNameWithFullClassName: String
        get() = currentMethodNameWithFullClassName(1)

    val currentFileNameWithFullMethodName: String
        get() {
            val CurrentMethodNameFull = currentMethodNameWithFullClassName(1)
            val currentFileName = currentFileName(1)

            return "$CurrentMethodNameFull($currentFileName)"
        }

    val invokingMethodNameWithFullClassName: String
        get() = invokingMethodNameWithFullClassName(2)

    val invokingFileNameWithFullMethodName: String
        get() {
            val invokingMethodNameFull = invokingMethodNameWithFullClassName(2)
            val invokingFileName = invokingFileName(2)

            return "$invokingMethodNameFull($invokingFileName)"
        }

    init {
        // Finds out the index of "this code" in the returned stack trace - funny but it differs in JDK 1.5 and 1.6
        var i = 0
        for (ste in Thread.currentThread().stackTrace) {
            i++
            if (ste.className == StackTraceInfo::class.java.name) {
                break
            }
        }
        CLIENT_CODE_STACK_INDEX = i
    }

    fun currentMethodName(offset: Int): String {
        return Thread.currentThread().stackTrace[CLIENT_CODE_STACK_INDEX + offset].methodName
    }

    fun currentClassName(offset: Int): String {
        return Thread.currentThread().stackTrace[CLIENT_CODE_STACK_INDEX + offset].className
    }

    fun currentFileName(offset: Int): String {
        val filename = Thread.currentThread().stackTrace[CLIENT_CODE_STACK_INDEX + offset].fileName
        val lineNumber = Thread.currentThread().stackTrace[CLIENT_CODE_STACK_INDEX + offset].lineNumber

        return "$filename:$lineNumber"
    }

    fun invokingMethodName(offset: Int): String {
        return currentMethodName(offset + 1)    // re-uses currentMethodName() with desired index
    }

    fun invokingClassName(offset: Int): String {
        return currentClassName(offset + 1)     // re-uses currentClassName() with desired index
    }

    fun invokingFileName(offset: Int): String {
        return currentFileName(offset + 1)     // re-uses currentFileName() with desired index
    }

    fun currentMethodNameWithFullClassName(offset: Int): String {
        val currentClassName = currentClassName(offset + 1)
        val currentMethodName = currentMethodName(offset + 1)

        return "$currentClassName.$currentMethodName"
    }

    fun invokingMethodNameWithFullClassName(offset: Int): String {
        val invokingClassName = invokingClassName(offset + 1)
        val invokingMethodName = invokingMethodName(offset + 1)

        return "$invokingClassName.$invokingMethodName"
    }

    fun print() {
        Log.i("STACK", "BEGIN")
        var trace = "STACK TRACE:\n"
        Thread.currentThread().stackTrace.forEach {
            trace = trace +
                    "    at " +
                    it.className +
                    "." +
                    it.methodName +
                    "(" +
                    it.fileName +
                    ":" +
                    it.lineNumber +
                    ")" +
                    "\n"
        }
        Log.i("STACK", trace)
        Log.i("STACK", "END")
    }
}
