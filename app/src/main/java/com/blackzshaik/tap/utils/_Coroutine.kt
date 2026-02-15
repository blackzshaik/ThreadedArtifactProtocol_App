package com.blackzshaik.tap.utils

import kotlinx.coroutines.CoroutineExceptionHandler

fun coroutineExceptionHandler(callback:() -> Unit) = CoroutineExceptionHandler { _, throwable ->
    throwable.printStackTrace()
    callback()
}