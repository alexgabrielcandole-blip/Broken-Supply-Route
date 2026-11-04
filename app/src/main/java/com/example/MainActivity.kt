package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AppScreen
import com.example.ui.DispatchScreen
import com.example.ui.DispatchViewModel
import com.example.ui.RosterScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val dispatchViewModel: DispatchViewModel by viewModels {
        DispatchViewModel.Factory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val currentScreen by dispatchViewModel.currentScreen.collectAsStateWithLifecycle()
                    when (currentScreen) {
                        AppScreen.DISPATCH -> DispatchScreen(viewModel = dispatchViewModel)
                        AppScreen.ROSTER -> RosterScreen(viewModel = dispatchViewModel)
                        AppScreen.TRAINING_HALL -> com.example.ui.TrainingHallScreen(viewModel = dispatchViewModel)
                        AppScreen.FORMATION -> com.example.ui.FormationScreen(viewModel = dispatchViewModel)
                    }
                }
            }
        }
    }
}
