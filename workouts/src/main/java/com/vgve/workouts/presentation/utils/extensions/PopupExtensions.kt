package com.vgve.workouts.presentation.utils.extensions

import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vgve.workouts.databinding.LayoutPopupBinding

fun Fragment.showBottomSheet(
    title: String,
    subTitle: String? = null,
    btnTitle: String? = null,
    cancelable: Boolean = true,
    btnClick: () -> Unit,
    isVisibleProgress: Boolean = true
): BottomSheetDialog {
    val binding = LayoutPopupBinding.inflate(layoutInflater)
    val bottomSheet = BottomSheetDialog(requireContext()).apply {
        setContentView(binding.root)
        setCancelable(cancelable)
    }

    with(binding) {
        tvTitle.text = title
        progressIndicator.isVisible = isVisibleProgress
        subTitle?.let {
            tvSubTitle.apply {
                isVisible = true
                text = it

            }
        }

        btnTitle?.let {
            btn.apply {
                text = it
                setOnClickListener {
                    btnClick()
                    bottomSheet.dismiss()
                }
            }
        }
    }

    bottomSheet.show()

    return bottomSheet
}
