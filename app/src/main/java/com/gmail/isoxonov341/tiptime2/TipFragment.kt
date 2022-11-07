package com.gmail.isoxonov341.tiptime2

import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.gmail.isoxonov341.tiptime2.databinding.FragmentTipBinding
import java.text.NumberFormat
import kotlin.math.ceil
import kotlin.math.round

class TipFragment : Fragment() {

    private var _binding: FragmentTipBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TipViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTipBinding.inflate(inflater, container, false)
        return binding.root
    }

    @Suppress("NAME_SHADOWING")
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // DataBinding
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            tipViewModel = viewModel
        }
        viewModel.setCustomTip(resources.getString(R.string.tip_10))

        /*
        * Regulates chips state and sets a value
        * from CustomTipDialogFragment to custom chip
        */
        binding.chipGroup.setOnCheckedStateChangeListener { _, _ ->
            viewModel.customTipPercentage.observe(this.viewLifecycleOwner) {
                when (viewModel.customTipPercentage.value) {
                    "10%" -> {
                        binding.optionCustomPercentage.text = resources.getString(R.string.custom_tip)
                        binding.optionCustomPercentage.isChecked = false
                        binding.optionTenPercent.isChecked = true
                    }
                    "15%" -> {
                        binding.optionCustomPercentage.text = resources.getString(R.string.custom_tip)
                        binding.optionCustomPercentage.isChecked = false
                        binding.optionFifteenPercent.isChecked = true
                    }
                    "20%" -> {
                        binding.optionCustomPercentage.text = resources.getString(R.string.custom_tip)
                        binding.optionCustomPercentage.isChecked = false
                        binding.optionTwentyPercent.isChecked = true
                    }
                    else -> {
                        binding.optionCustomPercentage.isChecked = true
                        binding.optionCustomPercentage.text =
                            getString(R.string.custom_tip_percentage, it)

                    }
                }
            }
        }

        // Directs user to the BottomSheetDialog to enter customTipPercentage
        binding.optionCustomPercentage.setOnClickListener {
            val action = TipFragmentDirections
                .actionTipFragmentToCustomTipDialogFragment()
            findNavController().navigate(action)
        }



        // Responds to the changes in tipPercentage, totalBill and Split values
        binding.optionCustomPercentage.addTextChangedListener { calculateValue() }
        binding.totalBillInput.addTextChangedListener { calculateValue() }
        binding.tvSplitValue.addTextChangedListener { calculateValue() }

        binding.totalBillInput.setOnKeyListener { view, keyCode, _ ->
            handleKeyEvent(
                view,
                keyCode
            )
        }

        // Reduces split size until 1
        binding.btnMinus.setOnClickListener {
            if (binding.tvSplitValue.text.toString().toInt() <= 1) {
                binding.btnMinus.isEnabled = false
            } else {
                binding.tvSplitValue.text =
                    (binding.tvSplitValue.text.toString().toInt() - 1).toString()
            }
        }
        // Increases split size(no set limit)
        binding.btnPlus.setOnClickListener {
            if (binding.tvSplitValue.text.toString().toInt() >= 1) {
                binding.btnMinus.isEnabled = true
                binding.tvSplitValue.text =
                    (binding.tvSplitValue.text.toString().toInt() + 1).toString()
            }
        }
    }

    /*
    * Calculates Bill, Tip and Total values and sets these values
    * according to a country currency and roundIcon(checkbox) state
    */
    private fun calculateValue() {
        binding.apply {
            // If user has no set total bill it sets initial values
            if (totalBillInput.text.isNullOrEmpty()) {
                initialInputState()
                return
            }

            // Shows toast if user tries to input more than 7 characters
            if (binding.totalBillInput.text?.length!! == 7) {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.split_toast_msg),
                    Toast.LENGTH_SHORT
                ).show()
            }

            roundIcon.visibility = View.VISIBLE

            val tipPercentage: Long =
                viewModel.customTipPercentage.value!!.substringBefore("%").toLong()
            val totalBill = totalBillInput.text?.toString()?.toDouble()
            val split = tvSplitValue.text.toString().toInt()

            val bill: Double = totalBill?.div(split) ?: 0.00
            val tip: Double = (bill.times(tipPercentage)) / 100
            val total: Double = tip.plus(bill)

            val roundedBill = ceil(bill)
            val roundedTip = round(tip)
            val roundedTotal = roundedTip.plus(roundedBill)

            if (!roundIcon.isChecked) {
                tvBillAmount.text = NumberFormat.getCurrencyInstance().format(bill)
                tvTotalPerPerson.text = NumberFormat.getCurrencyInstance().format(total)
                tvTipAmount.text = NumberFormat.getCurrencyInstance().format(tip)
            } else {
                tvBillAmount.text = NumberFormat.getCurrencyInstance().format(roundedBill)
                tvTotalPerPerson.text =
                    NumberFormat.getCurrencyInstance().format(roundedTotal)
                tvTipAmount.text = NumberFormat.getCurrencyInstance().format(roundedTip)
            }
            roundIcon.setOnCheckedChangeListener { _, boolean ->
                if (!boolean) {
                    tvBillAmount.text = NumberFormat.getCurrencyInstance().format(bill)
                    tvTotalPerPerson.text = NumberFormat.getCurrencyInstance().format(total)
                    tvTipAmount.text = NumberFormat.getCurrencyInstance().format(tip)
                } else {
                    tvBillAmount.text = NumberFormat.getCurrencyInstance().format(roundedBill)
                    tvTotalPerPerson.text =
                        NumberFormat.getCurrencyInstance().format(roundedTotal)
                    tvTipAmount.text = NumberFormat.getCurrencyInstance().format(roundedTip)
                }
            }
        }

    }

    // Helpful code for quick hiding the soft keyboard
    private fun handleKeyEvent(view: View, keyCode: Int): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            // Hide the keyboard
            val inputMethodManager =
                activity?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            return true
        }
        return false
    }

    private fun initialInputState() {
        binding.apply {
            tvTotalPerPerson.text = ""
            tvBillAmount.text = ""
            tvTipAmount.text = ""
            roundIcon.visibility = View.INVISIBLE
        }
    }

}