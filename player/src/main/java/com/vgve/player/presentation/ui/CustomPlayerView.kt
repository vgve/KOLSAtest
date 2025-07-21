package com.vgve.player.presentation.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.media3.common.Player
import com.vgve.player.R
import com.vgve.player.databinding.CustomPlayerViewBinding

class CustomPlayerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val binding: CustomPlayerViewBinding by lazy {
        CustomPlayerViewBinding.inflate(LayoutInflater.from(context), this)
    }

    private val ivPlay: ImageView by lazy { binding.playerView.findViewById(R.id.iv_play) }
    val play: ImageView
        get() = ivPlay

    private val ivReplay: ImageView by lazy { binding.playerView.findViewById(R.id.iv_replay) }
    val replay: ImageView
        get() = ivReplay

    private val ivRewind: ImageView by lazy { binding.playerView.findViewById(R.id.iv_rewind) }
    val rewind: ImageView
        get() = ivRewind

    private val ivForward: ImageView by lazy { binding.playerView.findViewById(R.id.iv_forward) }
    val forward: ImageView
        get() = ivForward

    private val ivSettings: ImageView by lazy { binding.playerView.findViewById(R.id.iv_settings) }
    val settings: ImageView
        get() = ivSettings

    private val ivMute: ImageView by lazy { binding.playerView.findViewById(R.id.iv_mute) }
    val mute: ImageView
        get() = ivMute

    fun setPlayer(player: Player) {
        binding.playerView.player = player
    }

    fun setMuteResource(isMute: Boolean) {
        val image =
            if (isMute) R.drawable.ic_volume_off
            else R.drawable.ic_volume_on
        ivMute.setImageResource(image)
    }

    fun setPlayResource(isPlaying: Boolean) {
        val image =
            if (isPlaying) R.drawable.ic_pause
            else R.drawable.ic_play
        ivPlay.setImageResource(image)
    }
}
