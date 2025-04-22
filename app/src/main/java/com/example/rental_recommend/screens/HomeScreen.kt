package com.example.rental_recommend.screens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.rental_recommend.R
import com.example.rental_recommend.model.RentalHouse
import com.example.rental_recommend.viewmodel.RentalListState
import com.example.rental_recommend.viewmodel.RentalViewModel
import com.example.rental_recommend.viewmodel.FavoriteState
import com.example.rental_recommend.viewmodel.RentalEvent
import com.example.rental_recommend.data.UserManager
import com.example.rental_recommend.viewmodel.RentalViewModelFactory
import com.example.rental_recommend.components.FilterBar
import com.example.rental_recommend.components.RentalItem
import com.example.rental_recommend.model.*

enum class HomeTab {
    PERSONALIZED, HOT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToFavorites: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToAuth: () -> Unit,
    viewModel: RentalViewModel = viewModel(
        factory = RentalViewModelFactory(LocalContext.current)
    )
) {
    val rentalListState by viewModel.rentalListState.collectAsState()
    val events by viewModel.events.collectAsState()
    val context = LocalContext.current

    // 搜索和筛选状态
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    
    // 筛选选项状态
    var filters by remember {
        mutableStateOf<List<FilterOption>>(
            listOf(
                FilterOption(FilterType.LOCATION, "位置"),
                FilterOption(FilterType.PRICE, "租金"),
                FilterOption(FilterType.HOUSE_TYPE, "户型"),
                FilterOption(FilterType.AREA, "面积"),
                FilterOption(FilterType.ORIENTATION, "朝向")
            )
        )
    }

    LaunchedEffect(events) {
        when (events) {
            is RentalEvent.AuthError -> {
                onNavigateToAuth()
                viewModel.clearEvent()
            }
            else -> {}
        }
    }

    // 初始加载列表
    LaunchedEffect(Unit) {
        viewModel.loadRentalList()
    }

    // 执行搜索的函数
    fun performSearch() {
        viewModel.searchRental(
            query = searchQuery.trim(),
            minPrice = (filters.find { it.type == FilterType.PRICE }?.value as? FilterValue.Range)?.min,
            maxPrice = (filters.find { it.type == FilterType.PRICE }?.value as? FilterValue.Range)?.max,
            minArea = (filters.find { it.type == FilterType.AREA }?.value as? FilterValue.Range)?.min,
            maxArea = (filters.find { it.type == FilterType.AREA }?.value as? FilterValue.Range)?.max,
            type = (filters.find { it.type == FilterType.HOUSE_TYPE }?.value as? FilterValue.SingleChoice)?.value.let { 
                if (it == "不限") null else it 
            },
            orientation = (filters.find { it.type == FilterType.ORIENTATION }?.value as? FilterValue.SingleChoice)?.value.let { 
                if (it == "不限") null else it 
            },
            province = (filters.find { it.type == FilterType.LOCATION }?.value as? FilterValue.Location)?.province,
            city = (filters.find { it.type == FilterType.LOCATION }?.value as? FilterValue.Location)?.city
        )
    }

    // 处理筛选选项变化
    fun handleFilterSelected(type: FilterType, value: FilterValue) {
        filters = filters.map { filter ->
            if (filter.type == type) {
                filter.copy(
                    value = value,
                    isSelected = when (value) {
                        is FilterValue.Range -> value.min != null || value.max != null
                        is FilterValue.SingleChoice -> value.value != "不限"
                        is FilterValue.Location -> value.province != null || value.city != null
                        FilterValue.None -> false
                    }
                )
            } else filter
        }
        isSearchActive = true
        performSearch()
    }

    // 搜索框文本变化处理
    LaunchedEffect(searchQuery) {
        if (searchQuery.isBlank() && filters.none { it.isSelected }) {
            isSearchActive = false
            viewModel.loadRentalList()
        } else {
            isSearchActive = true
            performSearch()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("租房推荐") },
                actions = {
                    IconButton(onClick = onNavigateToFavorites) {
                        Icon(Icons.Default.Favorite, contentDescription = "收藏")
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "个人中心")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 搜索栏
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { 
                    Text("搜索房源（地址、标题、标签等）") 
                },
                leadingIcon = { 
                    Icon(
                        Icons.Default.Search, 
                        contentDescription = "搜索",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    ) 
                },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            // 筛选栏
            FilterBar(
                filters = filters,
                onFilterSelected = ::handleFilterSelected,
                modifier = Modifier.fillMaxWidth()
            )

            // 内容区域
            when (val state = rentalListState) {
                is RentalListState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is RentalListState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                is RentalListState.Success -> {
                    if (state.rentals.isEmpty()) {
                        EmptyStateMessage(isSearchActive)
                    } else {
                        RentalList(
                            rentals = state.rentals,
                            isSearchActive = isSearchActive,
                            onItemClick = { onNavigateToDetail(it.id) },
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyStateMessage(isSearchActive: Boolean) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (isSearchActive) "未找到相关房源" else "暂无房源",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (isSearchActive) {
                Text(
                    text = "试试调整筛选条件",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun RentalList(
    rentals: List<RentalHouse>,
    isSearchActive: Boolean,
    onItemClick: (RentalHouse) -> Unit,
    viewModel: RentalViewModel
) {
    val favoriteState by viewModel.favoriteState.collectAsState()
    val favoriteStates = remember(rentals) {
        mutableStateMapOf<Int, Boolean>()
    }
    
    // 初始化收藏状态
    LaunchedEffect(rentals) {
        rentals.forEach { rental ->
            viewModel.checkFavorite(rental.id)
        }
    }
    
    // 监听收藏状态变化
    LaunchedEffect(favoriteState) {
        when (favoriteState) {
            is FavoriteState.Success -> {
                val state = favoriteState as FavoriteState.Success
                // 找到当前操作的房源ID
                val currentRentalId = rentals.find { rental ->
                    viewModel.lastCheckedRentalId == rental.id
                }?.id
                
                currentRentalId?.let { id ->
                    favoriteStates[id] = state.isFavorite
                    // 如果是切换收藏操作，立即更新UI
                    if (viewModel.lastOperationWasToggle) {
                        favoriteStates[id] = state.isFavorite
                    }
                }
            }
            is FavoriteState.Error -> {
                // 处理错误状态
                val currentRentalId = rentals.find { rental ->
                    viewModel.lastCheckedRentalId == rental.id
                }?.id
                
                currentRentalId?.let { id ->
                    // 保持原有状态
                    favoriteStates[id] = favoriteStates[id] ?: false
                }
            }
            else -> {}
        }
    }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (!isSearchActive) {
            item {
                Text(
                    text = "全部房源",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
        
        items(rentals) { rental ->
            RentalItem(
                rental = rental,
                onItemClick = { onItemClick(rental) },
                onFavoriteClick = { 
                    viewModel.toggleFavorite(rental.id)
                },
                isFavorite = favoriteStates[rental.id] ?: false
            )
        }
    }
}

@Composable
fun RentalItem(
    rental: RentalHouse,
    onItemClick: (RentalHouse) -> Unit,
    onFavoriteClick: () -> Unit,
    isFavorite: Boolean
) {
    val context = LocalContext.current
    var retryCount by remember { mutableStateOf(0) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onItemClick(rental) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(rental.cover)
                        .crossfade(true)
                        .size(width = 500, height = 300)
                        .build(),
                    contentDescription = rental.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop,
                    onLoading = {
                        Log.d("RentalItem", "开始加载图片: ${rental.cover}, 重试次数: $retryCount")
                    },
                    onSuccess = { state ->
                        Log.d("RentalItem", "图片加载成功: ${rental.cover}")
                    },
                    onError = { result ->
                        Log.e("RentalItem", "图片加载失败: ${rental.cover}, 错误: ${result.result.throwable?.message ?: "未知错误"}")
                        if (retryCount < 3) {
                            retryCount++
                            Log.d("RentalItem", "尝试重新加载图片: ${rental.cover}, 重试次数: $retryCount")
                        }
                    }
                )
                
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "取消收藏" else "收藏",
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = rental.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = rental.priceText,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "${rental.location} · ${rental.areaText} · ${rental.structure}",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (!rental.tags.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = rental.tags,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}