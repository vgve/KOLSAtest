package com.vgve.workouts.presentation.utils.extensions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

/**
 * Launching a coroutine from a VM with mandatory exception handling.
 */
@Suppress("TooGenericExceptionCaught", "InstanceOfCheckForException")
inline fun ViewModel.launchSafe(
    crossinline errorHandler: (exception: Exception) -> Unit,
    crossinline block: suspend CoroutineScope.() -> Unit,
): Job {
    return viewModelScope.launch {
        try {
            block()
        } catch (exception: Exception) {
            if (exception !is CancellationException) {
                exception.printStackTrace()
                errorHandler(exception)
            }
        }
    }
}

fun Job.track(
    state: (isWorking: Boolean) -> Unit
): Job {
    state(isActive)
    this.invokeOnCompletion {
        state(false)
    }
    return this
}
