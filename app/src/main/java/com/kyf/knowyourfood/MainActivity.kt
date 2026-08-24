package com.kyf.knowyourfood

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.kyf.knowyourfood.ui.navigation.KYFNavHost
import com.kyf.knowyourfood.ui.theme.KYFTheme
import com.kyf.knowyourfood.ui.theme.Slate950

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as KYFApplication

        setContent {
            KYFTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Slate950
                ) {
                    KYFNavHost(app = app)
                }
            }
        }
    }
}
