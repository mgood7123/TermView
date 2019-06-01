package utils

import android.os.SystemClock
import kotlin.system.measureTimeMillis

fun sample(
    code: () -> Unit,
    printer: (time: Long) -> Unit
) {
    val time = measureTimeMillis {
        code()
    }
    printer(time)
}

fun sampleLoop(
    iterations: Int,
    code: (iterations: Int) -> Unit,
    printer: (loopTime: Long) -> Unit
) {
    val loopTime = measureTimeMillis {
        for (i in 1..iterations) {
            code(i)
        }
    }
    printer(loopTime)
}

fun sampleLoop(
    iterations: Int,
    delay: Int,
    code: (iterations: Int) -> Unit,
    printer: (loopTime: Long) -> Unit
) {
    val loopTime = measureTimeMillis {
        for (i in 1..iterations) {
            code(i)
            SystemClock.sleep(delay.toLong())
        }
    }
    printer(loopTime)
}
