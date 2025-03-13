package com.app.mytasks.viemodel

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    var clickOffset: Offset? = null
        private set

    fun setClickOffset(offset: Offset) {
        clickOffset = offset
    }
}