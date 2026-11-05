package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.CubeWorkspaceScreen
import com.example.util.configureAppCheck
import com.example.viewmodel.CubeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Configure App Check with launch intent before any UI or Firebase operations
        configureAppCheck(this, intent)

        enableEdgeToEdge()
        setContent {
            val cubeViewModel: CubeViewModel = viewModel()
            CubeWorkspaceScreen(viewModel = cubeViewModel)
        }
    }
}
