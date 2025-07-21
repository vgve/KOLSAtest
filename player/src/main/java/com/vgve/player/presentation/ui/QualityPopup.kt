package com.vgve.player.presentation.ui

import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.vgve.player.R
import com.vgve.player.domain.VideoQuality

fun Fragment.showQualityPopup(
    view: View,
    resolutions: List<VideoQuality>?,
    onClick: (VideoQuality) -> Unit,
): PopupMenu {
    val popup = PopupMenu(context, view).apply {
        menuInflater.inflate(R.menu.menu_quality, menu)
        setOnMenuItemClickListener { item ->
            when (item?.itemId) {
                R.id.quality_auto -> onClick(VideoQuality.Auto)
            }
            true
        }
    }

    popup.show()
    return popup
}
