package com.santalist.app.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.santalist.app.R
import com.santalist.app.data.model.Gift
import com.santalist.app.data.model.GiftStatus
import com.santalist.app.data.model.Occasion
import com.santalist.app.data.model.Person
import com.santalist.app.domain.Formatters
import com.santalist.app.ui.theme.SantaListTheme
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditGiftSheet(
    giftToEdit: Gift?,
    preselectedPersonId: String?,
    availablePeople: List<Person>,
    currentYear: Int = 2026,
    currency: String = "VND",
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        price: Int,
        status: GiftStatus,
        dueDate: Long?,
        occasion: Occasion,
        note: String,
        url: String,
        personId: String,
        year: Int
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val colors = SantaListTheme.colors
    val context = LocalContext.current

    var selectedPersonId by remember {
        mutableStateOf(giftToEdit?.personId ?: preselectedPersonId ?: availablePeople.firstOrNull()?.id ?: "")
    }
    var title by remember { mutableStateOf(giftToEdit?.title ?: "") }
    var priceText by remember { mutableStateOf(if (giftToEdit != null && giftToEdit.price > 0) giftToEdit.price.toString() else "") }
    var status by remember { mutableStateOf(giftToEdit?.status ?: GiftStatus.IDEA) }
    var occasion by remember { mutableStateOf(giftToEdit?.occasion ?: Occasion.CHRISTMAS) }
    var hasDueDate by remember { mutableStateOf(giftToEdit?.dueDate != null) }
    var dueDateMillis by remember { mutableLongStateOf(giftToEdit?.dueDate ?: System.currentTimeMillis()) }
    var note by remember { mutableStateOf(giftToEdit?.note ?: "") }
    var url by remember { mutableStateOf(giftToEdit?.url ?: "") }

    var isSubmitted by remember { mutableStateOf(false) }

    val trimmedTitle = title.trim()
    val parsedPrice: Int? = priceText
        .replace(".", "")
        .replace(",", "")
        .replace(" ", "")
        .replace("₫", "")
        .replace("$", "")
        .replace("€", "")
        .replace("£", "")
        .replace("¥", "")
        .let { if (it.isEmpty()) 0 else it.toIntOrNull() }

    val titleError = if (isSubmitted && trimmedTitle.isEmpty()) stringResource(R.string.val_title_required) else null
    val priceError = if (isSubmitted && (parsedPrice == null || parsedPrice < 0)) stringResource(R.string.val_price_invalid) else null
    val personError = if (isSubmitted && selectedPersonId.isEmpty()) stringResource(R.string.val_recipient_required) else null

    var personMenuExpanded by remember { mutableStateOf(false) }
    var occasionMenuExpanded by remember { mutableStateOf(false) }
    var statusMenuExpanded by remember { mutableStateOf(false) }

    val selectedPerson = availablePeople.firstOrNull { it.id == selectedPersonId }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.appCard,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(R.string.action_cancel), color = colors.textSecondary)
                }

                Text(
                    text = if (giftToEdit != null) stringResource(R.string.action_edit_gift) else stringResource(R.string.action_add_gift),
                    color = colors.textPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                TextButton(
                    onClick = {
                        isSubmitted = true
                        if (trimmedTitle.isNotEmpty() && parsedPrice != null && parsedPrice >= 0 && selectedPersonId.isNotEmpty()) {
                            val yearToSave = giftToEdit?.year ?: selectedPerson?.year ?: currentYear
                            onSave(
                                trimmedTitle,
                                parsedPrice,
                                status,
                                if (hasDueDate) dueDateMillis else null,
                                occasion,
                                note,
                                url,
                                selectedPersonId,
                                yearToSave
                            )
                            onDismiss()
                        }
                    }
                ) {
                    Text(
                        text = if (giftToEdit != null) stringResource(R.string.action_save) else stringResource(R.string.action_save),
                        color = colors.pineGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Recipient selector
            Text(
                text = "Recipient",
                color = colors.textSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedPerson?.name ?: "Select recipient",
                    onValueChange = {},
                    readOnly = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { personMenuExpanded = true },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.clickable { personMenuExpanded = true }
                        )
                    },
                    isError = personError != null,
                    supportingText = personError?.let { { Text(it, color = colors.cranberryRed) } },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.pineGreen,
                        unfocusedBorderColor = colors.textSecondary.copy(alpha = 0.3f)
                    )
                )

                DropdownMenu(
                    expanded = personMenuExpanded,
                    onDismissRequest = { personMenuExpanded = false }
                ) {
                    availablePeople.forEach { person ->
                        DropdownMenuItem(
                            text = { Text(person.name) },
                            onClick = {
                                selectedPersonId = person.id
                                personMenuExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title field
            Text(
                text = stringResource(R.string.gift_title_label),
                color = colors.textSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. Scarf, Books, Toys") },
                shape = RoundedCornerShape(14.dp),
                isError = titleError != null,
                supportingText = titleError?.let { { Text(it, color = colors.cranberryRed) } },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.pineGreen,
                    unfocusedBorderColor = colors.textSecondary.copy(alpha = 0.3f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Price field
            Text(
                text = "${stringResource(R.string.gift_price_label)} ($currency)",
                color = colors.textSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = priceText,
                onValueChange = { priceText = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("0") },
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = priceError != null,
                supportingText = {
                    if (priceError != null) {
                        Text(priceError, color = colors.cranberryRed)
                    } else if (parsedPrice != null && parsedPrice > 0) {
                        Text(
                            stringResource(R.string.equivalent_prefix, Formatters.formatCurrency(parsedPrice, currency)),
                            color = colors.pineGreen
                        )
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.pineGreen,
                    unfocusedBorderColor = colors.textSecondary.copy(alpha = 0.3f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Occasion & Status dropdowns
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Occasion
                Box(modifier = Modifier.weight(1f)) {
                    Column {
                        Text(
                            text = stringResource(R.string.gift_occasion_label),
                            color = colors.textSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = occasion.title,
                            onValueChange = {},
                            readOnly = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { occasionMenuExpanded = true },
                            trailingIcon = {
                                Icon(Icons.Default.ArrowDropDown, null, Modifier.clickable { occasionMenuExpanded = true })
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.pineGreen,
                                unfocusedBorderColor = colors.textSecondary.copy(alpha = 0.3f)
                            )
                        )
                    }
                    DropdownMenu(
                        expanded = occasionMenuExpanded,
                        onDismissRequest = { occasionMenuExpanded = false }
                    ) {
                        Occasion.entries.forEach { occ ->
                            DropdownMenuItem(
                                text = { Text(occ.title) },
                                onClick = {
                                    occasion = occ
                                    occasionMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // Status
                Box(modifier = Modifier.weight(1f)) {
                    Column {
                        Text(
                            text = stringResource(R.string.gift_status_label),
                            color = colors.textSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = status.title,
                            onValueChange = {},
                            readOnly = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { statusMenuExpanded = true },
                            trailingIcon = {
                                Icon(Icons.Default.ArrowDropDown, null, Modifier.clickable { statusMenuExpanded = true })
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.pineGreen,
                                unfocusedBorderColor = colors.textSecondary.copy(alpha = 0.3f)
                            )
                        )
                    }
                    DropdownMenu(
                        expanded = statusMenuExpanded,
                        onDismissRequest = { statusMenuExpanded = false }
                    ) {
                        GiftStatus.entries.forEach { st ->
                            DropdownMenuItem(
                                text = { Text(st.title) },
                                onClick = {
                                    status = st
                                    statusMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Due Date Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.gift_has_due_date),
                    color = colors.textPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                Switch(
                    checked = hasDueDate,
                    onCheckedChange = { hasDueDate = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colors.appCard,
                        checkedTrackColor = colors.pineGreen
                    )
                )
            }

            if (hasDueDate) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        value = Formatters.formatDate(dueDateMillis),
                        onValueChange = {},
                        readOnly = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                val cal = Calendar.getInstance().apply { timeInMillis = dueDateMillis }
                                DatePickerDialog(
                                    context,
                                    { _, year, month, day ->
                                        val newCal = Calendar.getInstance().apply {
                                            set(year, month, day, 12, 0, 0)
                                        }
                                        dueDateMillis = newCal.timeInMillis
                                    },
                                    cal.get(Calendar.YEAR),
                                    cal.get(Calendar.MONTH),
                                    cal.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.pineGreen,
                            unfocusedBorderColor = colors.textSecondary.copy(alpha = 0.3f)
                        )
                    )

                    TextButton(
                        onClick = { hasDueDate = false }
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = colors.cranberryRed)
                        Text(stringResource(R.string.gift_remove_due_date), color = colors.cranberryRed, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // URL field
            Text(
                text = stringResource(R.string.gift_url_label),
                color = colors.textSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("https://example.com/gift") },
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.pineGreen,
                    unfocusedBorderColor = colors.textSecondary.copy(alpha = 0.3f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Notes field
            Text(
                text = stringResource(R.string.gift_notes_label),
                color = colors.textSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Additional information") },
                shape = RoundedCornerShape(14.dp),
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
