package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.navigation.AppRoute
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.MessCalculatorScreen
import com.example.ui.theme.HisabProTheme
import com.example.ui.viewmodel.MessViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MessViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            // Dynamic theme with Light theme as default
            HisabProTheme(darkTheme = uiState.isDarkTheme) {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    // HashRouter-like state navigation switching between screens without new pages
                    when (uiState.currentRoute) {
                        AppRoute.CALCULATOR -> {
                            MessCalculatorScreen(
                                viewModel = viewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        AppRoute.HISTORY -> {
                            HistoryScreen(
                                viewModel = viewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}
