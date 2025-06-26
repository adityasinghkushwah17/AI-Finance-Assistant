package com.adityacodes.financebuddy.Screens


import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.adityacodes.financebuddy.BottomNavBar
import com.adityacodes.financebuddy.data.GoalEntity
import com.adityacodes.financebuddy.viewmodels.AuthViewModel
import com.adityacodes.financebuddy.viewmodels.GoalViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Composable
fun GoalScreen(
    viewModel: GoalViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val userId = authViewModel.currentUserId ?: return
    LaunchedEffect(Unit) { viewModel.loadGoals(userId) }

    val goals by viewModel.goals.collectAsState()

    var description by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
    var targetDate by remember { mutableStateOf("") }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            targetDate = dateFormat.format(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    androidx.compose.material3.Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .background(Color(0xFFE7F4F7))
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Your Financial Goals",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF3C3C3C),
                modifier = Modifier.padding(top =12.dp, bottom =24.dp)
            )


            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Goal Description") },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = targetAmount,
                onValueChange = { targetAmount = it },
                label = { Text("Target Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = targetDate,
                onValueChange = { },
                readOnly = true,
                label = { Text("Target Date (dd-MM-yyyy)") },
                trailingIcon = {
                    IconButton(
                        onClick = { datePickerDialog.show() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Pick Date",
                            tint = Color(0xFF41B8C9)
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    viewModel.createGoal(
                        description,
                        targetAmount.toDoubleOrNull() ?: 0.0,
                        targetDate,
                        userId
                    )
                    description = ""
                    targetAmount = ""
                    targetDate = ""
                },
                colors = ButtonDefaults.buttonColors(Color(0xFF41B8C9)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Goal", color = Color.White)
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "Your Goals",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF3C3C3C),
                modifier = Modifier.padding(bottom = 8.dp)
            )


            goals.forEach { goal ->
                GoalCard(
                    goal = goal,
                    onChecked = { viewModel.completeGoal(goal, userId) }
                )
            }
        }
    }
}

@Composable
fun GoalCard(
    goal: GoalEntity,
    onChecked: () -> Unit
) {
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Checkbox(
                checked = false,
                onCheckedChange = { if (it) onChecked() },
                colors = androidx.compose.material3.CheckboxDefaults.colors(
                    checkedColor = Color(0xFF41B8C9)
                )
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    goal.description,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                    color = Color(0xFF3C3C3C)
                )
                Text(
                    "Target: ₹${goal.targetAmount}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = "By: ${SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(goal.targetDateMillis)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}
