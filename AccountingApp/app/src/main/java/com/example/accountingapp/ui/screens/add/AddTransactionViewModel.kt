package com.example.accountingapp.ui.screens.add

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.accountingapp.AccountingApp
import com.example.accountingapp.data.model.Category
import com.example.accountingapp.data.model.Transaction
import com.example.accountingapp.data.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AddTransactionUiState(
    val type: TransactionType = TransactionType.EXPENSE,
    val amount: String = "",
    val selectedCategoryId: Long? = null,
    val categories: List<Category> = emptyList(),
    val note: String = "",
    val date: Long = System.currentTimeMillis(),
    val saved: Boolean = false
)

class AddTransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as AccountingApp).repository

    private val _type = MutableStateFlow(TransactionType.EXPENSE)
    private val _amount = MutableStateFlow("")
    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    private val _note = MutableStateFlow("")
    private val _date = MutableStateFlow(System.currentTimeMillis())
    private val _saved = MutableStateFlow(false)

    val categories: StateFlow<List<Category>> = repository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val uiState: StateFlow<AddTransactionUiState> = combineStates()

    private fun combineStates(): StateFlow<AddTransactionUiState> {
        return kotlinx.coroutines.flow.combine(
            _type, _amount, _selectedCategoryId, _note, _date, _saved, categories
        ) { type, amount, catId, note, date, saved, cats ->
            AddTransactionUiState(
                type = type,
                amount = amount,
                selectedCategoryId = catId,
                categories = cats.filter { it.type == type },
                note = note,
                date = date,
                saved = saved
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AddTransactionUiState())
    }

    fun setType(type: TransactionType) {
        _type.value = type
        _selectedCategoryId.value = null
    }

    fun setAmount(value: String) {
        // Only allow valid decimal input
        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
            _amount.value = value
        }
    }

    fun selectCategory(categoryId: Long) {
        _selectedCategoryId.value = categoryId
    }

    fun setNote(value: String) {
        _note.value = value
    }

    fun setDate(timestamp: Long) {
        _date.value = timestamp
    }

    fun save() {
        val amount = _amount.value.toDoubleOrNull() ?: return
        val categoryId = _selectedCategoryId.value ?: return
        if (amount <= 0) return

        viewModelScope.launch {
            repository.insertTransaction(
                Transaction(
                    amount = amount,
                    type = _type.value,
                    categoryId = categoryId,
                    note = _note.value,
                    date = _date.value
                )
            )
            _saved.value = true
        }
    }
}
