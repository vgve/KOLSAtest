package com.vgve.player.presentation.ui

import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.vgve.player.R
import com.vgve.player.domain.SettingsType

fun Fragment.showSettingsPopup(
    view: View,
    onClick: (value: SettingsType) -> Unit,
): PopupMenu {
    val popup = PopupMenu(context, view).apply {
        menuInflater.inflate(R.menu.menu_settings, menu)
        setOnMenuItemClickListener { item ->
            when (item?.itemId) {
                R.id.quality_settings -> onClick(SettingsType.QUALITY)
                R.id.speed_settings -> onClick(SettingsType.SPEED)
            }
            true
        }
    }

    popup.show()
    return popup
}
