package com.adityacodes.financebuddy.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adityacodes.financebuddy.data.FinanceRepository
import com.adityacodes.financebuddy.data.TransactionEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val repository: FinanceRepository
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<TransactionEntity>>(emptyList())
    val transactions: StateFlow<List<TransactionEntity>> = _transactions

    fun loadTransactions(userId: String) {
        viewModelScope.launch {
            repository.getAllTransactionsForUser(userId).collect {
                _transactions.value = it
            }
        }
    }

    fun addTransaction(amount: Double, tag: String,userId: String) {
        viewModelScope.launch {
            val tx = TransactionEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                amount = amount,
                tag = tag,
                timestamp = System.currentTimeMillis(),
                note = null
            )
            repository.insertTransaction(tx)
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }
}
