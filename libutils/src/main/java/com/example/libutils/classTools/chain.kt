package preprocessor.utils.core.classTools

import com.example.libutils.core.abort

/**
 * returns a [MutableList] of classes parenting the current class
 *
 * the top most class is always the last index
 *
 * the last class is always the first index
 *
 * for example: `up(f[0].a[0].a[0])` returns 3 indexes (0, 1, and 2) consisting of the following:
 *
 * index 0 = `f[0].a[0].a[0]`
 *
 * index 1 = `f[0].a[0]`
 *
 * index 2 = `f[0]`
 *
 * @param a the current class
 * @param m used internally to build up a list of classes
 * @param debug if true, debug output will be printed
 * @see getDeclaringUpperLevelClassObject
 */
fun chain(a: Any, m: MutableList<Any> = mutableListOf(), debug: Boolean = false): MutableList<Any> {
    m.add(a)
    if (debug) println("get upper class of ${a.javaClass.name}")
    val upperC = getDeclaringUpperLevelClassObject(a) ?: throw NullPointerException(
        "upperC is null, this should not happen"
    )
    return if (a == upperC) m
    else chain(upperC, m)
}
