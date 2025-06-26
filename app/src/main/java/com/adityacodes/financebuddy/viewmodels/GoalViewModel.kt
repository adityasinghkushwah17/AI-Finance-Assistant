package com.adityacodes.financebuddy.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adityacodes.financebuddy.NotificationHelper
import com.adityacodes.financebuddy.data.FinanceRepository
import com.adityacodes.financebuddy.data.GoalEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class GoalViewModel @Inject constructor(
    private val financeRepository: FinanceRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _goals = MutableStateFlow<List<GoalEntity>>(emptyList())
    val goals = _goals.asStateFlow()

    private val _selectedGoal = MutableStateFlow<GoalEntity?>(null)
    val selectedGoal = _selectedGoal.asStateFlow()

    private val _currentGoalProgress = MutableStateFlow(0f)
    val currentGoalProgress = _currentGoalProgress.asStateFlow()

    private val _currentSavings = MutableStateFlow(0.0)
    val currentSavings = _currentSavings.asStateFlow()

    private val _canCreateGoal = MutableStateFlow(true)
    val canCreateGoal = _canCreateGoal.asStateFlow()

    fun loadGoals(userId: String) {
        viewModelScope.launch {
            financeRepository.getUserGoals(userId).collect { list ->
                _goals.value = list
                _canCreateGoal.value = list.isEmpty() // allow creating goal only if none exist
                if (_selectedGoal.value == null && list.isNotEmpty()) {
                    selectGoal(list.first())
                }
            }
        }
        observeTransactions(userId)
    }

    fun selectGoal(goal: GoalEntity) {
        _selectedGoal.value = goal
        refreshProgress(goal.targetAmount, goal.userId)
    }

    fun createGoal(
        description: String,
        targetAmount: Double,
        targetDate: String,
        userId: String
    ) {
        viewModelScope.launch {
            if (_canCreateGoal.value) {
                financeRepository.insertGoal(
                    GoalEntity(
                        description = description,
                        targetAmount = targetAmount,
                        targetDateMillis = parseDate(targetDate),
                        userId = userId
                    )
                )
                loadGoals(userId) // reload goals
            }
        }
    }

    fun deleteGoal(goal: GoalEntity, userId: String) {
        viewModelScope.launch {
            financeRepository.deleteGoal(goal)
            loadGoals(userId)
        }
    }

    fun completeGoal(goal: GoalEntity, userId: String) {
        viewModelScope.launch {
            financeRepository.deleteGoal(goal)
            loadGoals(userId)
        }
    }

    fun refreshProgress(targetAmount: Double, userId: String) {
        viewModelScope.launch {
            financeRepository.getAllTransactionsForUser(userId).collect { transactions ->
                _selectedGoal.value?.let { goal ->
                    val savedAmount = transactions
                        .filter { it.tag == "Save" && it.timestamp >= goal.targetDateMillis }
                        .sumOf { it.amount }

                    _currentSavings.value = savedAmount
                    _currentGoalProgress.value =
                        (savedAmount / targetAmount).toFloat().coerceAtMost(1f)

                    if (savedAmount >= targetAmount) {
                        NotificationHelper.showGoalReachedNotification(
                            appContext,
                            goal.description,
                            targetAmount
                        )
                    }
                }
            }
        }
    }


    private fun observeTransactions(userId: String) {
        viewModelScope.launch {
            financeRepository.getAllTransactionsForUser(userId).collect { transactions ->
                _selectedGoal.value?.let { goal ->
                    // Only sum savings after the goal's creation time
                    val savedAmount = transactions
                        .filter { it.tag == "Save" && it.timestamp >= goal.targetDateMillis }
                        .sumOf { it.amount }

                    _currentSavings.value = savedAmount
                    _currentGoalProgress.value =
                        (savedAmount / goal.targetAmount).toFloat().coerceAtMost(1f)

                    if (savedAmount >= goal.targetAmount) {
                        NotificationHelper.showGoalReachedNotification(
                            appContext,
                            goal.description,
                            goal.targetAmount
                        )
                    }
                }
            }
        }
    }


    private fun parseDate(date: String): Long {
        return try {
            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            sdf.parse(date)?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
}
