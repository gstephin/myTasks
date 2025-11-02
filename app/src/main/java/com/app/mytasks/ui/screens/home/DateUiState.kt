package com.app.mytasks.ui.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import com.app.mytasks.data.entities.Task
import java.time.LocalDate

data class DateUiState @RequiresApi(Build.VERSION_CODES.O) constructor(
    var selectedDate: LocalDate = LocalDate.now(),
    var listTasks: List<Task> = emptyList(),
    var hasTasks: Boolean = true
)