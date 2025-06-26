package com.adityacodes.financebuddy.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.adityacodes.financebuddy.BottomNavBar
import com.adityacodes.financebuddy.data.TransactionEntity
import com.adityacodes.financebuddy.viewmodels.AuthViewModel
import com.adityacodes.financebuddy.viewmodels.TransactionViewModel
import java.util.Date

@Composable
fun TransactionScreen(
    viewModel: TransactionViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val userId = authViewModel.currentUserId ?: return
    LaunchedEffect(userId) { viewModel.loadTransactions(userId) }
    val transactions by viewModel.transactions.collectAsState()

    val totalBalance = transactions.sumOf { it.amount }

    var newAmount by remember { mutableStateOf("") }
    var newTag by remember { mutableStateOf("") }

    Scaffold(bottomBar = { BottomNavBar(navController =navController )}) {paddingvalues->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE7F4F7)) // light blue background
            .padding(paddingvalues),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Total: ₹$totalBalance",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = if (totalBalance >= 0) Color(0xFF41B8C9) else Color(0xFFF44336),
            modifier = Modifier.padding(top=12.dp,bottom = 24.dp)
        )


        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    newAmount.toDoubleOrNull()?.let {
                        viewModel.addTransaction(it, newTag, userId)
                        newAmount = ""; newTag = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(Color(0xFF41B8C9)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Got Money", color = Color.White)
            }

            Button(
                onClick = {
                    newAmount.toDoubleOrNull()?.let {
                        viewModel.addTransaction(-it, newTag, userId)
                        newAmount = ""; newTag = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(Color(0xFFF44336)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Expense", color = Color.White)
            }
        }

        Spacer(Modifier.height(16.dp))


        OutlinedTextField(
            value = newAmount,
            onValueChange = { newAmount = it },
            label = { Text("Amount") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            shape = RoundedCornerShape(24.dp)
        )
        OutlinedTextField(
            value = newTag,
            onValueChange = { newTag = it },
            label = { Text("Description") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            shape = RoundedCornerShape(24.dp)
        )

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(transactions) { tx ->
                TransactionRow(
                    transaction = tx,
                    onDelete = { viewModel.deleteTransaction(tx) }
                )
            }
        }
    }
}}

@Composable
fun TransactionRow(
    transaction: TransactionEntity,
    onDelete: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                transaction.tag,
                color = Color(0xFF3C3C3C),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )

            Text(
                transaction.timestamp.formatAsDate(),
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                "₹${transaction.amount}",
                color = if (transaction.amount < 0) Color(0xFFF44336) else Color(0xFF41B8C9),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(Modifier.width(8.dp))
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color.Gray
            )
        }
    }
}

fun Long.formatAsDate(): String {
    val sdf = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault())
    return sdf.format(Date(this))
}
