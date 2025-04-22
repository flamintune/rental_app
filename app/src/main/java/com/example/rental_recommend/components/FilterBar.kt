package com.example.rental_recommend.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.rental_recommend.model.*
import com.example.rental_recommend.model.FilterOption

@Composable
fun FilterBar(
    filters: List<FilterOption>,
    onFilterSelected: (FilterType, FilterValue) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedFilter by remember { mutableStateOf<FilterType?>(null) }

    Column(modifier = modifier) {
        // 筛选选项栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(MaterialTheme.colorScheme.surface),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            filters.forEach { filter ->
                FilterTab(
                    filter = filter,
                    isExpanded = expandedFilter == filter.type,
                    onClick = {
                        expandedFilter = if (expandedFilter == filter.type) null else filter.type
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 展开的筛选面板
        AnimatedVisibility(
            visible = expandedFilter != null,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                when (expandedFilter) {
                    FilterType.LOCATION -> LocationFilter(
                        currentValue = (filters.find { it.type == FilterType.LOCATION }?.value as? FilterValue.Location) ?: FilterValue.Location(),
                        onValueSelected = { onFilterSelected(FilterType.LOCATION, it) }
                    )
                    FilterType.PRICE -> RangeFilter(
                        options = FilterOptions.priceRanges,
                        currentValue = (filters.find { it.type == FilterType.PRICE }?.value as? FilterValue.Range) ?: FilterValue.Range(),
                        onValueSelected = { onFilterSelected(FilterType.PRICE, it) }
                    )
                    FilterType.HOUSE_TYPE -> SingleChoiceFilter(
                        options = FilterOptions.houseTypes,
                        currentValue = (filters.find { it.type == FilterType.HOUSE_TYPE }?.value as? FilterValue.SingleChoice) ?: FilterValue.SingleChoice("不限"),
                        onValueSelected = { onFilterSelected(FilterType.HOUSE_TYPE, it) }
                    )
                    FilterType.AREA -> RangeFilter(
                        options = FilterOptions.areaRanges,
                        currentValue = (filters.find { it.type == FilterType.AREA }?.value as? FilterValue.Range) ?: FilterValue.Range(),
                        onValueSelected = { onFilterSelected(FilterType.AREA, it) }
                    )
                    FilterType.ORIENTATION -> SingleChoiceFilter(
                        options = FilterOptions.orientations,
                        currentValue = (filters.find { it.type == FilterType.ORIENTATION }?.value as? FilterValue.SingleChoice) ?: FilterValue.SingleChoice("不限"),
                        onValueSelected = { onFilterSelected(FilterType.ORIENTATION, it) }
                    )
                    null -> {}
                }
            }
        }
    }
}

@Composable
private fun FilterTab(
    filter: FilterOption,
    isExpanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = when (val value = filter.value) {
                is FilterValue.Range -> value.displayText
                is FilterValue.SingleChoice -> value.displayText
                is FilterValue.Location -> value.displayText
                FilterValue.None -> filter.title
            },
            style = MaterialTheme.typography.bodyMedium,
            color = if (filter.isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
        Icon(
            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = if (isExpanded) "收起" else "展开",
            modifier = Modifier.size(20.dp),
            tint = if (filter.isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun RangeFilter(
    options: List<FilterValue.Range>,
    currentValue: FilterValue.Range,
    onValueSelected: (FilterValue.Range) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 300.dp)
            .padding(vertical = 8.dp)
    ) {
        items(options) { option ->
            FilterOption(
                text = option.displayText,
                isSelected = option == currentValue,
                onClick = { onValueSelected(option) }
            )
        }
    }
}

@Composable
private fun SingleChoiceFilter(
    options: List<FilterValue.SingleChoice>,
    currentValue: FilterValue.SingleChoice,
    onValueSelected: (FilterValue.SingleChoice) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 300.dp)
            .padding(vertical = 8.dp)
    ) {
        items(options) { option ->
            FilterOption(
                text = option.displayText,
                isSelected = option == currentValue,
                onClick = { onValueSelected(option) }
            )
        }
    }
}

@Composable
private fun LocationFilter(
    currentValue: FilterValue.Location,
    onValueSelected: (FilterValue.Location) -> Unit
) {
    // 这里可以实现省市选择器
    // 暂时使用简单的文本输入
    Text(
        text = "位置筛选功能开发中...",
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun FilterOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "已选择",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
} 