package com.gmail.isoxonov341.tiptime2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.gmail.isoxonov341.tiptime2.databinding.FragmentCustomTipDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CustomTipDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentCustomTipDialogBinding
    private val viewModel: TipViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCustomTipDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
        * Receives total bill value from the user and
        * and sets the value to CustomTipPercentage in TipViewModel
        */
        binding.tipInput.addTextChangedListener {
            if (it?.isNotBlank()!!) {
                binding.apply {
                    btnCheck.isEnabled = true
                    val newTipPercentage = binding.tipInput.text.toString()
                    btnCheck.setOnClickListener {
                        addEntry("$newTipPercentage%")
                        dismiss()
                    }
                }
            } else {
                binding.btnCheck.isEnabled = false
            }
        }

    }

    private fun addEntry(value: String) = viewModel.setCustomTip(value)


}