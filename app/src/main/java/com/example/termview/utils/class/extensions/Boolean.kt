@file:Suppress("unused")

package preprocessor.utils.`class`.extensions

fun <T, R> T.ifTrue(condition:Boolean, code: (condition:T) -> R): R = if (condition) code(this) else this as R

fun <T> T.ifTrueReturn(condition:Boolean, code: (T) -> Unit): Boolean {
    if (condition) code(this)
    return condition
}

fun <T, R> T.ifConditionReturn(condition:Boolean, TRUE: () -> R, FALSE: () -> R): R =
    if (condition) TRUE() as R
    else FALSE() as R

fun <R> Boolean.ifConditionReturn(TRUE: () -> R, FALSE: () -> R): R =
    if (this) TRUE() as R
    else FALSE() as R

fun Boolean.ifTrueReturn(code: () -> Unit): Boolean {
    if (this) code()
    return this
}

fun <T> T.ifFalseReturn(condition:Boolean, code: (T) -> Unit): Boolean {
    if (!condition) code(this)
    return condition
}

fun Boolean.ifFalseReturn(code: () -> Unit): Boolean {
    if (!this) code()
    return this
}

fun <T> T.ifUnconditionalReturn(condition:Boolean, code: (T) -> Unit): Boolean {
    code(this)
    return condition
}

fun <T, R> T.executeIfTrue(condition:Boolean, code: (condition:T) -> R): R = ifTrue(condition) { code(this) }
fun <T> T.executeIfTrueAndReturn(condition:Boolean, code: (T) -> Unit): Boolean = ifTrueReturn(condition, code)
fun Boolean.executeIfTrueAndReturn(code: () -> Unit): Boolean = ifTrueReturn(code)
fun <T> T.executeIfFalseAndReturn(condition:Boolean, code: (T) -> Unit): Boolean = ifFalseReturn(condition, code)
fun <T> T.executeUnconditionallyAndReturn(condition:Boolean, code: (T) -> Unit): Boolean = ifUnconditionalReturn(condition, code)
fun <R> Boolean.executeConditionallyAndReturn(condition:Boolean, TRUE: () -> R, FALSE: () -> R): R = ifConditionReturn(condition, TRUE, FALSE)
fun <T, R> T.executeConditionallyAndReturn(condition:Boolean, TRUE: () -> R, FALSE: () -> R): R = ifConditionReturn(condition, TRUE, FALSE)

private fun test() {
    var x = "abc"
    println("x = $x") // x = abc
    val y = x.ifTrue(x.startsWith('a')) {
        it.length
    }
    println("y = $y") // y = 3
    println("x = $x") // x = abc
    val yx = x.ifTrueReturn(x.startsWith('a')) {
        x = it.drop(1) // i want "it" to modify "x" itself
    }
    println("yx = $yx") // yx = true
    println("x = $x") // x = abc // should be "bc"
}
