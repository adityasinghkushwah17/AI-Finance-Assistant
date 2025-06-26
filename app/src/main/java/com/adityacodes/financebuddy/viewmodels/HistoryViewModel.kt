package com.adityacodes.financebuddy.viewmodels

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import com.adityacodes.financebuddy.data.FinanceAdvice
import kotlinx.coroutines.launch
import com.adityacodes.financebuddy.data.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: FinanceRepository
) : ViewModel() {

    private val _adviceHistory = mutableStateOf<List<FinanceAdvice>>(emptyList())
    val adviceHistory: State<List<FinanceAdvice>> = _adviceHistory

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    init {
        loadAdviceHistory()
    }

    private fun loadAdviceHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _adviceHistory.value = repository.getAllAdvice()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshHistory() {
        loadAdviceHistory()
    }
}