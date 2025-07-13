package com.vgve.workouts.presentation.utils.extensions

internal fun String.isInteger() = this.toIntOrNull()?.let { true } ?: false
