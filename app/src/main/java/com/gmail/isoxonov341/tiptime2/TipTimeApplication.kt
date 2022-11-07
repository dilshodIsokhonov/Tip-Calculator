package com.gmail.isoxonov341.tiptime2

import android.app.Application
import com.google.android.material.color.DynamicColors

class TipTimeApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}