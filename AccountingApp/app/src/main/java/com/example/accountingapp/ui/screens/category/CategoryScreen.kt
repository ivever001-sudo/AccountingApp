package com.example.accountingapp.ui.screens.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.accountingapp.data.model.Category
import com.example.accountingapp.ui.theme.BrownDark
import com.example.accountingapp.ui.theme.BrownLight
import com.example.accountingapp.ui.theme.BrownMedium
import com.example.accountingapp.ui.theme.CardGreen
import com.example.accountingapp.ui.theme.CardPink
import com.example.accountingapp.ui.theme.Cream
import com.example.accountingapp.ui.theme.Mint
import com.example.accountingapp.ui.theme.MintDark
import com.example.accountingapp.ui.theme.Pink
import com.example.accountingapp.ui.theme.PinkDark
import com.example.accountingapp.ui.theme.White

@Composable
fun CategoryScreen(
    viewModel: CategoryViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "分类管理",
                style = MaterialTheme.typography.headlineMedium,
                color = BrownDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "长按分类可删除（预设分类不可删）",
                style = MaterialTheme.typography.labelSmall,
                color = BrownLight
            )
        }

        // Expense categories
        item {
            SectionHeader(
                emoji = "💸",
                title = "支出分类",
                count = state.expenseCategories.size,
                bgColor = CardPink,
                textColor = PinkDark
            )
        }

        items(state.expenseCategories) { category ->
            CategoryRow(category = category)
        }

        // Income categories
        item {
            Spacer(modifier = Modifier.height(6.dp))
            SectionHeader(
                emoji = "💰",
                title = "收入分类",
                count = state.incomeCategories.size,
                bgColor = CardGreen,
                textColor = MintDark
            )
        }

        items(state.incomeCategories) { category ->
            CategoryRow(category = category)
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@Composable
private fun SectionHeader(
    emoji: String,
    title: String,
    count: Int,
    bgColor: androidx.compose.ui.graphics.Color,
    textColor: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = emoji, fontSize = 18.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$count 个",
            style = MaterialTheme.typography.labelSmall,
            color = BrownLight
        )
    }
}

@Composable
private fun CategoryRow(category: Category) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = category.emoji, fontSize = 26.sp)
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
