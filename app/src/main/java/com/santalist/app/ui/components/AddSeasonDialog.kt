package com.santalist.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.santalist.app.R
import com.santalist.app.ui.theme.SantaListTheme

@Composable
fun AddSeasonDialog(
    onDismiss: () -> Unit,
    onConfirm: (year: Int, occasion: String) -> Unit
) {
    var yearText by remember { mutableStateOf("2027") }
    var occasionText by remember { mutableStateOf("Christmas") }
    var yearError by remember { mutableStateOf(false) }

    val colors = SantaListTheme.colors

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = colors.appCard,
        title = {
            Text(
                text = stringResource(R.string.dialog_add_season_title),
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = yearText,
                    onValueChange = {
                        yearText = it
                        yearError = it.toIntOrNull() == null
                    },
                    label = { Text(stringResource(R.string.dialog_year_label)) },
                    isError = yearError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = occasionText,
                    onValueChange = { occasionText = it },
                    label = { Text(stringResource(R.string.dialog_occasion_label)) },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val year = yearText.toIntOrNull()
                    if (year != null && year in 2000..2100) {
                        onConfirm(year, occasionText.ifBlank { "Christmas" })
                    } else {
                        yearError = true
                    }
                }
            ) {
                Text(
                    text = stringResource(R.string.action_save),
                    color = colors.pineGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.action_cancel),
                    color = colors.textSecondary
                )
            }
        }
    )
}
