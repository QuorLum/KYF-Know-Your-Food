package com.kyf.knowyourfood

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kyf.knowyourfood.ui.components.KYFAppBackground
import com.kyf.knowyourfood.ui.navigation.KYFNavHost
import com.kyf.knowyourfood.ui.theme.KYFTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as KYFApplication

        setContent {
            KYFTheme {
                KYFAppBackground {
                    KYFNavHost(app = app)
                }
            }
        }
    }
}
