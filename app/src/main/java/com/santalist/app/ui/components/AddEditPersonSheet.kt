package com.santalist.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.santalist.app.data.model.Person
import com.santalist.app.domain.Formatters
import com.santalist.app.ui.theme.SantaListTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPersonSheet(
    personToEdit: Person?,
    onDismiss: () -> Unit,
    onSave: (name: String, budget: Int, note: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val colors = SantaListTheme.colors

    var name by remember { mutableStateOf(personToEdit?.name ?: "") }
    var budgetText by remember { mutableStateOf(personToEdit?.budget?.toString() ?: "") }
    var note by remember { mutableStateOf(personToEdit?.note ?: "") }
    var isSubmitted by remember { mutableStateOf(false) }

    val trimmedName = name.trim()
    val parsedBudget: Int? = budgetText
        .replace(".", "")
        .replace(",", "")
        .replace(" ", "")
        .replace("₫", "")
        .let { if (it.isEmpty()) 0 else it.toIntOrNull() }

    val nameError: String? = if (isSubmitted && trimmedName.isEmpty()) "Please enter recipient name" else null
    val budgetError: String? = if (isSubmitted && (parsedBudget == null || parsedBudget < 0)) "Budget must be a positive number" else null

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.appCard,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDismiss) {
                    Text(text = "Cancel", color = colors.textSecondary)
                }

                Text(
                    text = if (personToEdit != null) "Edit Recipient" else "Add Recipient",
                    color = colors.textPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                TextButton(
                    onClick = {
                        isSubmitted = true
                        if (trimmedName.isNotEmpty() && parsedBudget != null && parsedBudget >= 0) {
                            onSave(trimmedName, parsedBudget, note)
                            onDismiss()
                        }
                    }
                ) {
                    Text(
                        text = if (personToEdit != null) "Save" else "Add",
                        color = colors.pineGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Name field
            Text(
                text = "Full name or nickname",
                color = colors.textSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. Mom, Best Friend") },
                isError = nameError != null,
                supportingText = nameError?.let { { Text(it, color = colors.cranberryRed) } },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.pineGreen,
                    unfocusedBorderColor = colors.textSecondary.copy(alpha = 0.3f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Budget field
            Text(
                text = "Budget (VNĐ)",
                color = colors.textSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = budgetText,
                onValueChange = { budgetText = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("0") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = budgetError != null,
                supportingText = {
                    if (budgetError != null) {
                        Text(budgetError, color = colors.cranberryRed)
                    } else if (parsedBudget != null && parsedBudget > 0) {
                        Text("Equivalent: ${Formatters.formatVND(parsedBudget)}", color = colors.pineGreen)
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.pineGreen,
                    unfocusedBorderColor = colors.textSecondary.copy(alpha = 0.3f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Notes field
            Text(
                text = "Preferences, notes (optional)",
                color = colors.textSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Gift ideas, favorite colors, sizes...") },
                minLines = 3,
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.pineGreen,
                    unfocusedBorderColor = colors.textSecondary.copy(alpha = 0.3f)
                )
            )
        }
    }
}
