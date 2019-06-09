package com.example.libutils.extensions

import java.nio.ByteBuffer
import com.example.libparser.Parser
import com.example.libutils.algorithms.Stack
import kotlin.math.max


// find         : \{\n    return if \(this \=\= null\) null\n    else this\.(.*)\n\}
// replace with : = this\?\.$1

/**
 * splits the **string** into **tokens** delimited by a sequence of [delimiters]
 *
 * includes the [delimiters] in the output if [returnDelimiters] is **true**
 * @param delimiters a sequence of **delimiters**, given in the form `"delim1", "delim2", "delim3"` and so on
 * @param returnDelimiters if **true**, the sequence of [delimiters] are included as part of the output, otherwise they are omitted
 * @see split
 * @see tokenize
 */
fun String.tokenizeVararg(vararg delimiters: String, returnDelimiters: Boolean = false): List<String> {
    var res = listOf(this)
    delimiters.forEach { str ->
        res = res.flatMap {
            it.split(str).flatMap {
                listOf(it, str)
            }.dropLast(1).filterNot {
                it.isEmpty()
            }.filterNot {
                !returnDelimiters && it == str
            }
        }
    }
    return res
}

/**
 * splits the **string** into **tokens** delimited by a sequence of [delimiters]
 *
 * includes the [delimiters] in the output if [returnDelimiters] is **true**
 *
 * the [delimiters] are specified on a per-character basis
 *
 * meaning that **tokenize("abc")** is equivalent to **[tokenizeVararg]("a", "b", "c")**
 *
 * @param delimiters a sequence of **delimiters**
 * @param returnDelimiters if **true**, the sequence of [delimiters] are included as part of the output, otherwise they are ommitted
 * @see split
 * @see tokenizeVararg
 */
fun String.tokenize(delimiters: String, returnDelimiters: Boolean = false): List<String> {
    var res = listOf(this)
    val d = delimiters.toStack()
    d.forEach { str ->
        res = res.flatMap {
            it.split(str!!).flatMap {
                listOf(it, str)
            }.dropLast(1).filterNot {
                it.isEmpty()
            }.filterNot {
                !returnDelimiters && it == str
            }
        }
    }
    return res
}

/**
 * collapses the string based on the specified **token**
 *
 * if one or more of **token** is subsequently present in the current string, it is replaced with a single **token**
 *
 * for example:
 *
 * **"aaaa".collapse("a")** will be replaced by **"a"**
 *
 * **"aaaabbbbaaaa".collapse("a")** will be replaced by **"abbbba"**
 *
 * **"aaaabbbbaaaa".collapse("b")** will be replaced by **"aaaabaaaa"**
 *
 * **"aaaabbbbaaaa".collapse("a").collapse("b")** will be replaced by **"aba"**
 *
 * **"a    b".collapse(" ", "|rabbit|")** will be replaced by **"a|rabbit|b"**
 * @see expand
 */
fun String.collapse(token: String): String {
    val str = StringBuilder()
    val parser = Parser(this) { it.toStack() }
    val t = parser.IsSequenceOneOrMany(token)
    while (parser.peek() != null) {
        if (t.peek()) {
            t.pop()
            str.append(token)
        } else str.append(parser.pop()!!)
    }
    return str.toString()
}

/**
 * collapses the string based on the specified **token**, replacing **token** with **replaceWith**
 *
 * if one or more of **token** is subsequently present in the current string, it is replaced with a single **replaceWith**
 *
 * for example:
 *
 * **"aaaa".collapse("a", "Carrot")** will be replaced by **"Carrot"**
 *
 * **"aaaabbbbaaaa".collapse("a", "Carrot")** will be replaced by **"CarrotbbbbCarrot"**
 *
 * **"aaaabbbbaaaa".collapse("b", "Carrot")** will be replaced by **"aaaaCarrotaaaa"**
 *
 * **"aaaabbbbaaaa".collapse("a", "Carrot").collapse("b", "Carrot")** will be replaced by **"CarrotCarrotCarrot"**
 *
 * **"aaaabbbbaaaa".collapse("b", "Carrot").collapse("a", "Carrot")** will be replaced by **"CarrotCCarrotrrotCarrot"**,
 *
 *
 *
 *
 * note that in the case of **"aaaabbbbaaaa".collapse("b", "Carrot").collapse("a", "Carrot")** you would normally expect **"CarrotCarrotCarrot"**, **however** due to call ordering this is not the
 * case: when **b** is first collapsed, the string results in **"aaaaCarrotaaaa"**, then when **a** is collapsed
 * it is collapsing on the string **"aaaaCarrotaaaa"** and not **"aaaaabbbbaaaa"**
 *
 * @see expand
 */
fun String.collapse(token: String, replaceWith: String): String {
    val str = StringBuilder()
    val parser = Parser(this) { it.toStack() }
    val t = parser.IsSequenceOneOrMany(token)
    while (parser.peek() != null) {
        if (t.peek()) {
            t.pop()
            str.append(replaceWith)
        } else str.append(parser.pop()!!)
    }
    return str.toString()
}

/**
 * expands the string based on the specified **token**, replacing **token** with **to**
 *
 * if **token** is present in the current string, it is replaced with a **to**
 *
 * for example:
 *
 * **"aaaa".expand("a", "Carrot")** will be replaced by **"CarrotCarrotCarrotCarrot"**
 *
 * **"a    b".expand(" ", "|rabbit")** will be replaced by **"a|rabbit|rabbit|rabbit|rabbitb"**
 *
 * @see collapse
 */
fun String.expand(token: String, to: String): String {
    val str = StringBuilder()
    val parser = Parser(this) { it.toStack() }
    val t = parser.IsSequenceOnce(token)
    while (parser.peek() != null) {
        if (t.peek()) {
            t.pop()
            str.append(to)
        } else str.append(parser.pop()!!)
    }
    return str.toString()
}

/**
 * expands the string based on the specified **token**,
 *
 * if **token** is present in the current string, it is replaced with **to**
 * except on the last occurrence in which case is replaced with **last**
 *
 * for example:
 *
 * **"aaaa".expand("a", "Carrot", "Stew")** will be replaced by **"CarrotCarrotCarrotStew"**
 *
 * **"a    b".expand(" ", "|rabbit", "|")** will be replaced by **"a|rabbit|rabbit|rabbit|b"**
 *
 * @see collapse
 */
fun String.expand(token: String, to: String, last: String): String {
    val str = StringBuilder()
    val parser = Parser(this) { it.toStack() }
    val t = parser.IsSequenceOnce(token)
    while (parser.peek() != null) {
        if (t.peek()) {
            t.pop()
            str.append(if (!t.peek()) last else to)
        } else str.append(parser.pop()!!)
    }
    return str.toString()
}

/**
 * converts a [String] into a [Stack]
 * @see Stack.toStringConcat
 * @return the resulting conversion
 */
fun String.toStack(): Stack<String> {
    val deq = Stack<String>()
    var i = 0
    while (i < this.length) deq.addLast(this[i++].toString())
    return deq
}

/**
 * converts a [String] into a [ByteArray]
 * @return the resulting conversion
 * @see fileToByteArray
 */
fun String.toByteArray(): ByteArray {
    val b = ByteArray(this.length)
    val fi = this.iterator()
    var i = 0
    while (fi.hasNext()) {
        val B = fi.next().toByte()
        b.set(i, B)
        i++
    }
    return b
}

fun String.toStringBuilder(): StringBuilder = StringBuilder(this)

fun String.toStringBuilder(capacity: Int): StringBuilder = StringBuilder(capacity).append(this)

fun String.padExtendEnd(to: Int, str: String) = if (to - this.length isGreaterThan 0) {
    val build = this.toStringBuilder(max(this.length, to));
    val m = str.lastIndex
    var i = 0
    while (build.length isLessThan to) {
        build.append(str[i])
        if (i isEqualTo m) i = 0 else i++
    }
    build.toString()
} else if (to - this.length isEqualTo 0) this else {
    this.padShrinkEnd(to, str)
}

fun String.padExtendEnd(to: Int, char: Char): String = this.padExtendEnd(to, char.toString())

fun String.padExtendEnd(to: Int): String = this.padExtendEnd(to, this[0])

fun String.padExtendStart(to: Int, str: String): String = if (to - this.length isGreaterThan 0) {
    val build = this.reversed().toStringBuilder()
    val m = str.lastIndex
    var i = 0
    while (build.length isLessThan to) {
        build.append(str[i])
        if (i isEqualTo m) i = 0 else i++
    }
    build.reverse().toString()
} else if (to - this.length isEqualTo 0) this else {
    this.padShrinkStart(to, str)
}

fun String.padExtendStart(to: Int, char: Char): String = this.padExtendStart(to, char.toString())

fun String.padExtendStart(to: Int): String = this.padExtendStart(to, this[0])

fun String.padShrinkEnd(to: Int, str: String): String = if (this.length - to isGreaterThan 0) {
    val thisLength = this.length
    val build = this.take(to).toStringBuilder()
    val m = str.lastIndex
    var i = 0
    while (build.length isLessThan thisLength) {
        build.append(str[i])
        if (i isEqualTo m) i = 0 else i++
    }
    build.toString()
} else if (this.length - to isEqualTo 0) this else {
    this.padExtendEnd(to, str)
}

fun String.padShrinkEnd(to: Int, char: Char): String = this.padShrinkEnd(to, char.toString())

fun String.padShrinkEnd(to: Int): String = this.padShrinkEnd(to, this[0])

fun String.padShrinkStart(to: Int, str: String): String = if (this.length - to isGreaterThan 0) {
    val thisLength = this.length
    val build = this.reversed().take(to).toStringBuilder()
    val m = str.lastIndex
    var i = 0
    while (build.length isLessThan thisLength) {
        build.append(str[i])
        if (i isEqualTo m) i = 0 else i++
    }
    build.reverse().toString()
} else if (this.length - to isEqualTo 0) this else {
    this.padExtendStart(to, str)
}

fun String.padShrinkStart(to: Int, char: Char): String = this.padShrinkStart(to, char.toString())

fun String.padShrinkStart(to: Int): String = this.padShrinkStart(to, this[0])


/**
 * converts a [String] into a [ByteBuffer]
 * @return the resulting conversion
 * @see File.toByteBuffer
 */
fun String.toByteBuffer(): ByteBuffer {
    return ByteBuffer.wrap(this.toByteArray())
}