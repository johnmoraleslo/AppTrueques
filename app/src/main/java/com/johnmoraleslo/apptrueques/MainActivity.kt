package com.johnmoraleslo.apptrueques

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.johnmoraleslo.apptrueques.ui.navigation.NavGraph
import com.johnmoraleslo.apptrueques.ui.theme.AppTruequesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTruequesTheme {
                NavGraph()
            }
        }
    }
}
