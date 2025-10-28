package com.app.mytasks.ui.screens

import android.annotation.SuppressLint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import java.util.Calendar
import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.app.mytasks.data.entities.Priority
import com.app.mytasks.data.entities.Task

/**
 * TaskCreationScreen
 *
 *
 * Task creation screen with fields and options.
 *
 * @author Stephin
 * @date 2025-03-12
 */

@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCreationScreen(
    onSave: (Task) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.LOW) }
    var dueDate by remember { mutableStateOf<Long?>(null) }
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Task") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .padding(horizontal = 16.dp)) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            // Priority Dropdown
            Spacer(modifier = Modifier.height(8.dp))
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it } // ✅ Toggle dropdown state
            ) {
                TextField(
                    value = priority.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Priority") },
                    modifier = Modifier
                        .menuAnchor()
                        .clickable { expanded = true } // ✅ Open dropdown when clicked
                )
                ExposedDropdownMenu(
                    expanded = expanded, // ✅ Use state variable
                    onDismissRequest = { expanded = false } // ✅ Close on outside click
                ) {
                    Priority.entries.forEach { p ->
                        DropdownMenuItem(
                            text = { Text(p.name) },
                            onClick = {
                                priority = p
                                expanded = false // ✅ Close dropdown on selection
                            }
                        )
                    }
                }
            }
            // Date Picker
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                context,
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    dueDate = calendar.timeInMillis
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { datePicker.show() }) {
                Text("Pick Due Date")
            }
            dueDate?.let { Text("Due: ${java.text.SimpleDateFormat("MM/dd/yyyy").format(it)}") }

            Row(modifier = Modifier.padding(top = 16.dp)) {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onSave(
                                Task(
                                    title = title,
                                    description = description,
                                    priority = priority,
                                    dueDate = dueDate
                                )
                            )
                        }
                    }
                ) { Text("Save") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onCancel) { Text("Cancel") }
            }
        }
    }
}