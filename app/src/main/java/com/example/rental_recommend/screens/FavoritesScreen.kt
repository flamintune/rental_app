package com.example.rental_recommend.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.rental_recommend.model.RentalHouse
import com.example.rental_recommend.viewmodel.RentalViewModel
import com.example.rental_recommend.viewmodel.RentalListState
import com.example.rental_recommend.components.RentalItem
import com.example.rental_recommend.viewmodel.RentalViewModelFactory
import com.example.rental_recommend.viewmodel.RentalEvent
import com.example.rental_recommend.viewmodel.FavoriteState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToAuth: () -> Unit,
    viewModel: RentalViewModel = viewModel(
        factory = RentalViewModelFactory(LocalContext.current)
    )
) {
    val rentalListState by viewModel.rentalListState.collectAsState()
    val favoriteState by viewModel.favoriteState.collectAsState()
    val events by viewModel.events.collectAsState()
    val context = LocalContext.current
    
    // 当前操作的房源ID
    var currentRentalId by remember { mutableStateOf<Int?>(null) }
    // 是否显示确认对话框
    var showConfirmDialog by remember { mutableStateOf(false) }
    // 是否正在操作中
    var isOperating by remember { mutableStateOf(false) }

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
        viewModel.loadFavorites()
    }
    
    // 监听收藏状态变化
    LaunchedEffect(favoriteState) {
        when (favoriteState) {
            is FavoriteState.Success -> {
                isOperating = false
                // 如果取消收藏成功，重新加载收藏列表
                if (!(favoriteState as FavoriteState.Success).isFavorite) {
                    viewModel.loadFavorites()
                }
            }
            is FavoriteState.Error -> {
                isOperating = false
                // 显示错误提示
                // TODO: 可以添加一个Snackbar来显示错误信息
            }
            is FavoriteState.Loading -> {
                isOperating = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("我的收藏") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
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
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }
            is RentalListState.Success -> {
                val rentals = state.rentals
                Log.d("FavoritesScreen", "rentals: ${rentals}")
                if (rentals.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "暂无收藏",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(rentals) { rental ->
                            RentalItem(
                                rental = rental,
                                onItemClick = { onNavigateToDetail(rental.id) },
                                onFavoriteClick = {
                                    currentRentalId = rental.id
                                    showConfirmDialog = true
                                },
                                isFavorite = true
                            )
                        }
                    }
                }
            }
        }
        
        // 确认取消收藏对话框
        if (showConfirmDialog && currentRentalId != null) {
            AlertDialog(
                onDismissRequest = { 
                    if (!isOperating) {
                        showConfirmDialog = false
                        currentRentalId = null
                    }
                },
                title = { Text("取消收藏") },
                text = { Text("确定要取消收藏该房源吗？") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            currentRentalId?.let { id ->
                                isOperating = true
                                viewModel.toggleFavorite(id)
                                // 关闭弹窗
                                showConfirmDialog = false
                            }
                        }
                    ) {
                        Text("确定")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { 
                            showConfirmDialog = false
                            currentRentalId = null
                        }
                    ) {
                        Text("取消")
                    }
                }
            )
        }
    }
}