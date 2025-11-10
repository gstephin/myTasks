package com.app.mytasks.ui.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.app.mytasks.util.ColorPreferences
import com.app.mytasks.viemodel.AuthViewModel

/**
 * HomeTab
 *
 * @author stephingeorge
 * @date 30/10/2025
 */

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController

) {

    Scaffold() { paddingValues ->
        Content(paddingValues, navController)
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Content(
    paddingValues: PaddingValues,
    navController: NavController
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .padding(top = 5.dp)
    ) {
        InlineTitleIconComponent()
        WeekCalenderSection(navController)
    }
}
