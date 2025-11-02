package com.glamstudio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.glamstudio.ui.theme.GlamStudioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GlamStudioTheme {
                // Aquí irá tu navegación
            }
        }
    }
}
