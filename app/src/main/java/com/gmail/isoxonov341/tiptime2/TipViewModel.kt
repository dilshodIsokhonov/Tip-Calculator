package com.gmail.isoxonov341.tiptime2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TipViewModel: ViewModel() {

    private val _customTipPercentage = MutableLiveData<String>()
    val customTipPercentage: LiveData<String> = _customTipPercentage

    fun setCustomTip(value: String) {
        _customTipPercentage.value = value
    }

}