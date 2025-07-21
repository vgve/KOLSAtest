package com.vgve.player.presentation.ui

import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.vgve.player.R
import com.vgve.player.domain.Speed

fun Fragment.showSpeedPopup(
    view: View,
    onClick: (speed: Speed) -> Unit,
): PopupMenu {
    val popup = PopupMenu(context, view).apply {
        menuInflater.inflate(R.menu.menu_speed, menu)
        setOnMenuItemClickListener { item ->
            when (item?.itemId) {
                R.id.speed_slow -> onClick(Speed.SLOW)
                R.id.speed_normal -> onClick(Speed.NORMAL)
                R.id.speed_fast -> onClick(Speed.FAST)
                R.id.speed_faster -> onClick(Speed.FASTER)
                R.id.speed_fastest -> onClick(Speed.FASTEST)
            }
            true
        }
    }

    popup.show()
    return popup
}
