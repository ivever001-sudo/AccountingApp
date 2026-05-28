package com.example.accountingapp.ui.screens.stats

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.accountingapp.AccountingApp
import com.example.accountingapp.data.db.CategorySummary
import com.example.accountingapp.data.model.Category
import com.example.accountingapp.data.model.TransactionType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

data class CategoryStat(
    val category: Category,
    val total: Double,
    val percentage: Float
)

data class StatsUiState(
    val year: Int = 2024,
    val month: Int = 1,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val expenseCategories: List<CategoryStat> = emptyList(),
    val incomeCategories: List<CategoryStat> = emptyList()
)

class StatsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as AccountingApp).repository

    private val _year = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    private val _month = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH) + 1)

    private fun getMonthRange(year: Int, month: Int): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, 1, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        cal.set(year, month - 1, cal.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val end = cal.timeInMillis
        return start to end
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<StatsUiState> = combine(_year, _month) { year, month ->
        year to month
    }.flatMapLatest { (year, month) ->
        val (start, end) = getMonthRange(year, month)
        combine(
            repository.getTotalByType(TransactionType.INCOME, start, end),
            repository.getTotalByType(TransactionType.EXPENSE, start, end),
            repository.getCategorySummary(TransactionType.EXPENSE, start, end),
            repository.getCategorySummary(TransactionType.INCOME, start, end),
            repository.getAllCategories()
        ) { income, expense, expenseSummary, incomeSummary, categories ->
            StatsUiState(
                year = year,
                month = month,
                totalIncome = income,
                totalExpense = expense,
                expenseCategories = buildCategoryStats(expenseSummary, categories, expense),
                incomeCategories = buildCategoryStats(incomeSummary, categories, income)
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StatsUiState())

    fun previousMonth() {
        if (_month.value == 1) {
            _month.value = 12
            _year.value -= 1
        } else {
            _month.value -= 1
        }
    }

    fun nextMonth() {
        if (_month.value == 12) {
            _month.value = 1
            _year.value += 1
        } else {
            _month.value += 1
        }
    }

    private fun buildCategoryStats(
        summary: List<CategorySummary>,
        categories: List<Category>,
        totalAmount: Double
    ): List<CategoryStat> {
        if (totalAmount <= 0) return emptyList()
        return summary.mapNotNull { s ->
            val cat = categories.find { it.id == s.categoryId } ?: return@mapNotNull null
            CategoryStat(
                category = cat,
                total = s.total,
                percentage = (s.total / totalAmount).toFloat()
            )
        }.sortedByDescending { it.total }
    }
}
