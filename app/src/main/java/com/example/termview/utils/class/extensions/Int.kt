@file:Suppress("unused")

package com.example.termview.utils.`class`.extensions

val Int.second get() = this.seconds
val Int.seconds get() = this * 1000
val Int.minute get() = this.minutes
val Int.minutes get() = this * 60.seconds
val Int.hour get() = this.hours
val Int.hours get() = this * 60.minutes
