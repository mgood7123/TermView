package com.utils.`class`.extensions

import android.content.Context

fun Float.spToPx(context: Context) = this * context.resources.displayMetrics.scaledDensity
fun Float.pxToSp(context: Context) = this / context.resources.displayMetrics.scaledDensity
fun Float.pxToDp(context: Context) = this / context.resources.displayMetrics.density
fun Float.dpToPx(context: Context) = this * context.resources.displayMetrics.density
