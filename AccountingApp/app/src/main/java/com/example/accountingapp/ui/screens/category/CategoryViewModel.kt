package com.example.accountingapp.ui.screens.category

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.accountingapp.AccountingApp
import com.example.accountingapp.data.model.Category
import com.example.accountingapp.data.model.TransactionType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CategoryUiState(
    val incomeCategories: List<Category> = emptyList(),
    val expenseCategories: List<Category> = emptyList(),
    val showAddDialog: Boolean = false,
    val addDialogType: TransactionType = TransactionType.EXPENSE
)

class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as AccountingApp).repository

    val uiState: StateFlow<CategoryUiState> = kotlinx.coroutines.flow.combine(
        repository.getCategoriesByType(TransactionType.INCOME),
        repository.getCategoriesByType(TransactionType.EXPENSE)
    ) { income, expense ->
        CategoryUiState(
            incomeCategories = income,
            expenseCategories = expense
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CategoryUiState())

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }
}
