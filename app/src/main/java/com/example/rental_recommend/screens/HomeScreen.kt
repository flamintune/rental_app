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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.rental_recommend.model.RentalHouse
import com.example.rental_recommend.viewmodel.RentalListState
import com.example.rental_recommend.viewmodel.RentalViewModel
import com.example.rental_recommend.viewmodel.FavoriteState
import com.example.rental_recommend.viewmodel.RentalEvent
import com.example.rental_recommend.data.UserManager
import com.example.rental_recommend.viewmodel.RentalViewModelFactory

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

    LaunchedEffect(events) {
        when (events) {
            is RentalEvent.AuthError -> {
                onNavigateToAuth()
                viewModel.clearEvent()
            }
            else -> {}
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadRentalList()
    }

    LaunchedEffect(rentalListState) {
        when (val state = rentalListState) {
            is RentalListState.Success -> {
                Log.d("HomeScreen", "获取到房源数据：${state.rentals.size} 条")
                state.rentals.forEachIndexed { index, rental ->
                    Log.d("HomeScreen", """
                        房源 ${index + 1}:
                        - ID: ${rental.id}
                        - 标题: ${rental.title}
                        - 价格: ${rental.priceText}
                        - 位置: ${rental.location}
                        - 面积: ${rental.areaText}
                        - 类型: ${rental.type}
                        - 结构: ${rental.structure}
                        - 标签: ${rental.tags}
                        ----------------------------------------
                    """.trimIndent())
                }
            }
            is RentalListState.Error -> {
                Log.e("HomeScreen", "加载房源数据失败: ${state.message}")
            }
            is RentalListState.Loading -> {
                Log.d("HomeScreen", "正在加载房源数据...")
            }
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(HomeTab.PERSONALIZED) }

    val filteredHouses = remember(searchQuery, rentalListState) {
        when (val state = rentalListState) {
            is RentalListState.Success -> {
                if (searchQuery.isBlank()) {
                    state.rentals
                } else {
                    state.rentals.filter { 
                        it.title.contains(searchQuery, ignoreCase = true) ||
                        it.location.contains(searchQuery, ignoreCase = true)
                    }
                }
            }
            else -> emptyList()
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
        Column(
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
                placeholder = { Text("搜索房源") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            // 标签页
            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                modifier = Modifier.fillMaxWidth()
            ) {
                HomeTab.values().forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = { Text(if (tab == HomeTab.PERSONALIZED) "个性化推荐" else "热门房源") }
                    )
                }
            }

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
                        Text(text = state.message)
                    }
                }
                is RentalListState.Success -> {
                    if (filteredHouses.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "暂无房源",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Log.d("HomeScreen", "filteredHouses: ${filteredHouses.size}")
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(filteredHouses) { rental ->
                                RentalItem(
                                    rental = rental,
                                    onClick = { onNavigateToDetail(rental.id) },
                                    onFavoriteClick = { /* 首页不需要收藏功能 */ },
                                    isFavorite = false
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RentalItem(
    rental: RentalHouse,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    isFavorite: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box {
                AsyncImage(
                    model = rental.cover ?: "",
                    contentDescription = rental.title ?: "房源图片",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
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
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = rental.title ?: "暂无标题",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = rental.priceText ?: "价格待定",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = rental.location ?: "位置待定",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${rental.areaText ?: "面积待定"} | ${rental.structure ?: "户型待定"}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = rental.type ?: "类型待定",
                    style = MaterialTheme.typography.bodySmall
                )
            }
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