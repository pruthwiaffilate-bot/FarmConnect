package com.farmconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.farmconnect.ui.theme.FarmConnectTheme

class MainActivity : ComponentActivity() {
    private val viewModel: FarmConnectViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FarmConnectTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FarmConnectApp(viewModel)
                }
            }
        }
    }
}
