package com.santalist.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.santalist.app.ui.MainScreen
import com.santalist.app.ui.MainViewModel
import com.santalist.app.ui.theme.SantaListTheme

class MainActivity : ComponentActivity() {
    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
            // Permission result handled
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            val mainViewModel: MainViewModel = viewModel()
            val settings by mainViewModel.settingsState.collectAsState()
            val systemDark = isSystemInDarkTheme()
            val isDark = when (settings.themeMode) {
                "light" -> false
                "dark" -> true
                else -> systemDark
            }

            SantaListTheme(darkTheme = isDark) {
                MainScreen(viewModel = mainViewModel)
            }
        }
    }
}
