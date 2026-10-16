package com.santalist.app.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.santalist.app.R
import com.santalist.app.domain.SettingsState
import com.santalist.app.ui.theme.SantaListTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    settings: SettingsState,
    onCurrencyChange: (String) -> Unit,
    onLanguageChange: (String) -> Unit,
    onNotificationsToggle: (Boolean) -> Unit,
    onThemeChange: (String) -> Unit,
    onExportJson: () -> String,
    onLoadSampleData: () -> Unit,
    onClearAllData: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val colors = SantaListTheme.colors
    val context = LocalContext.current
    var showConfirmDeleteDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.appCard,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.settings_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Currency
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Paid, contentDescription = null, tint = colors.textSecondary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.settings_currency),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = colors.textPrimary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("VND", "USD", "EUR", "GBP", "JPY").forEach { curr ->
                    FilterChip(
                        selected = settings.currency.equals(curr, ignoreCase = true),
                        onClick = { onCurrencyChange(curr) },
                        label = { Text(curr) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Language
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Language, contentDescription = null, tint = colors.textSecondary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.settings_language),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = colors.textPrimary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = settings.language == "en" || settings.language == "system",
                    onClick = { onLanguageChange("en") },
                    label = { Text("English") }
                )
                FilterChip(
                    selected = settings.language == "vi",
                    onClick = { onLanguageChange("vi") },
                    label = { Text("Tiếng Việt") }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Theme
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Brightness4, contentDescription = null, tint = colors.textSecondary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.settings_theme),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = colors.textPrimary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "system" to stringResource(R.string.theme_system),
                    "light" to stringResource(R.string.theme_light),
                    "dark" to stringResource(R.string.theme_dark)
                ).forEach { (mode, label) ->
                    FilterChip(
                        selected = settings.themeMode == mode,
                        onClick = { onThemeChange(mode) },
                        label = { Text(label) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Notifications Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = colors.textSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.settings_reminders),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "1 day before deadline at 9:00 AM",
                            fontSize = 12.sp,
                            color = colors.textSecondary
                        )
                    }
                }
                Switch(
                    checked = settings.notificationsEnabled,
                    onCheckedChange = { onNotificationsToggle(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colors.appCard,
                        checkedTrackColor = colors.pineGreen
                    )
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 5. Data Actions
            OutlinedButton(
                onClick = {
                    val json = onExportJson()
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("SantaList Data", json))
                    Toast.makeText(context, context.getString(R.string.json_copied_toast), Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Download, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.settings_export_json))
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = {
                    onLoadSampleData()
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Science, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.settings_load_demo))
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = { showConfirmDeleteDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.cranberryRed),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.settings_clear_data))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "SantaList v1.0.0 • No Internet required • Private & Offline",
                fontSize = 12.sp,
                color = colors.textSecondary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }

    if (showConfirmDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDeleteDialog = false },
            containerColor = colors.appCard,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = stringResource(R.string.confirm_clear_title),
                    fontWeight = FontWeight.Bold,
                    color = colors.cranberryRed
                )
            },
            text = { Text(stringResource(R.string.confirm_clear_msg)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDeleteDialog = false
                        onClearAllData()
                        onDismiss()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.action_delete),
                        color = colors.cranberryRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDeleteDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}
