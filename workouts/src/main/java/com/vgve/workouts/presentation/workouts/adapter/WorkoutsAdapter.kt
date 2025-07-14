package com.vgve.workouts.presentation.workouts.adapter

import androidx.core.view.isVisible
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.vgve.workouts.R
import com.vgve.workouts.databinding.ItemWorkoutBinding
import com.vgve.workouts.domain.models.WorkoutModel
import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.vgve.workouts.domain.models.toResString
import com.vgve.workouts.presentation.utils.extensions.isInteger

class WorkoutsDiffUtil: DiffUtil.ItemCallback<WorkoutModel>() {
    override fun areItemsTheSame(oldItem: WorkoutModel, newItem: WorkoutModel) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: WorkoutModel, newItem: WorkoutModel) = oldItem == newItem
}

class WorkoutsAdapter(
    onClick: (WorkoutModel) -> Unit,
) : AsyncListDifferDelegationAdapter<WorkoutModel>(WorkoutsDiffUtil()) {
    init {
        delegatesManager.addDelegate(workoutsAdapterDelegate(onClick))
    }
}

fun workoutsAdapterDelegate(
    onClick: (WorkoutModel) -> Unit,
) = adapterDelegateViewBinding<WorkoutModel, WorkoutModel, ItemWorkoutBinding>(
    { layoutInflater, root ->
        ItemWorkoutBinding.inflate(layoutInflater, root, false)
    }
) {
    with(binding) {
        mcv.setOnClickListener { onClick(item) }

        bind {
            tvTitle.text = item.title
            tvDescription.apply {
                isVisible = item.description.isNotEmpty()
                text = item.description
            }
            tvType.text = getString(item.type.toResString())
            tvDuration.apply {
                isVisible = item.duration.isInteger()
                text = getString(R.string.duration_minutes, item.duration)
            }
        }
    }
}
