package com.vgve.core.utils.extensions

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator

fun NavController.navigateSafe(@IdRes actionId: Int) {
    currentDestination?.getAction(actionId)?.let { navigate(actionId) }
}

fun NavController.navigateSafe(direction: NavDirections) {
    currentDestination?.getAction(direction.actionId)?.let { navigate(direction) }
}

fun <T : Fragment> T.withArgs(vararg pairs: Pair<String, Any?>): T {
    arguments = bundleOf(*pairs)
    return this
}

fun NavController.navigateSafe(
    @IdRes resId: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null,
    navExtras: Navigator.Extras? = null
) {
    val action = currentDestination?.getAction(resId) ?: graph.getAction(resId)

    if (action != null && currentDestination?.id != action.destinationId) {
        navigate(resId, args, navOptions, navExtras)
    }
}
