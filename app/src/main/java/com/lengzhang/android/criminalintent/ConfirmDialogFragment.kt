package com.lengzhang.android.criminalintent

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

private const val TAG = "ConfirmDialogFragment"

private const val ARG_MESSAGE = "message"
private const val ARG_POSITIVE_LABEL = "positive_label"
private const val ARG_NEGATIVE_LABEL = "negative_label"

class ConfirmDialogFragment : DialogFragment() {

    interface Callbacks {
        fun onConfirmDialogConfirmClicked()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val message = arguments?.getInt(ARG_MESSAGE) ?: R.string.confirm_dialog_default_message
        val positiveLabel =
            arguments?.getInt(ARG_POSITIVE_LABEL) ?: R.string.confirm_dialog_default_positive_label
        val negativeLabel =
            arguments?.getInt(ARG_NEGATIVE_LABEL) ?: R.string.confirm_dialog_default_negative_label

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(message)
                .setPositiveButton(positiveLabel) { _, _ ->
                    targetFragment?.let { fragment ->
                        (fragment as Callbacks).onConfirmDialogConfirmClicked()
                    }
                }
                .setNegativeButton(negativeLabel) { dialog, _ ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object {
        fun newInstance(
            message: Int = R.string.confirm_dialog_default_message,
            positiveLabel: Int = R.string.confirm_dialog_default_positive_label,
            negativeLabel: Int = R.string.confirm_dialog_default_negative_label
        ): ConfirmDialogFragment {
            val args = Bundle().apply {
                putInt(ARG_MESSAGE, message)
                putInt(ARG_POSITIVE_LABEL, positiveLabel)
                putInt(ARG_NEGATIVE_LABEL, negativeLabel)
            }

            return ConfirmDialogFragment().apply { arguments = args }
        }
    }
}