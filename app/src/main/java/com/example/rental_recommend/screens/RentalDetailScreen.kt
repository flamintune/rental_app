package com.example.rental_recommend.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
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
import coil.request.ImageRequest
import com.example.rental_recommend.model.RentalHouse
import com.example.rental_recommend.viewmodel.RentalViewModel
import com.example.rental_recommend.viewmodel.RentalDetailState
import com.example.rental_recommend.viewmodel.FavoriteState
import com.example.rental_recommend.viewmodel.RentalViewModelFactory
import com.example.rental_recommend.viewmodel.RentalEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentalDetailScreen(
    id: Int,
    onNavigateBack: () -> Unit,
    onNavigateToAuth: () -> Unit,
    viewModel: RentalViewModel = viewModel(
        factory = RentalViewModelFactory(LocalContext.current)
    )
) {
    val context = LocalContext.current
    var isFavorite by remember { mutableStateOf(false) }
    val favoriteState by viewModel.favoriteState.collectAsState()
    val events by viewModel.events.collectAsState()
    val detailState by viewModel.detailState.collectAsState()
    var retryCount by remember { mutableStateOf(0) }

    LaunchedEffect(events) {
        when (events) {
            is RentalEvent.AuthError -> {
                onNavigateToAuth()
                viewModel.clearEvent()
            }
            else -> {}
        }
    }

    LaunchedEffect(id) {
        viewModel.checkFavorite(id)
        viewModel.getRentalDetail(id)
    }

    LaunchedEffect(favoriteState) {
        if (favoriteState is FavoriteState.Success) {
            isFavorite = (favoriteState as FavoriteState.Success).isFavorite
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("房源详情") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.toggleFavorite(id)
                        }
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) "取消收藏" else "收藏",
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        when (val state = detailState) {
            is RentalDetailState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is RentalDetailState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            is RentalDetailState.Success -> {
                val rental = state.rental
                Log.d("RentalDetail", "房源详情: ${rental}")
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    // 封面图片
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(rental.cover)
                                .crossfade(true)
                                .size(width = 800, height = 500)
                                .build(),
                            contentDescription = rental.title,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(MaterialTheme.shapes.medium),
                            contentScale = ContentScale.Crop,
                            onLoading = {
                                Log.d("RentalDetail", "开始加载图片: ${rental.cover}, 重试次数: $retryCount")
                            },
                            onSuccess = { state ->
                                Log.d("RentalDetail", "图片加载成功: ${rental.cover}")
                            },
                            onError = { result ->
                                Log.e("RentalDetail", "图片加载失败: ${rental.cover}, 错误: ${result.result.throwable?.message ?: "未知错误"}")
                                if (retryCount < 3) {
                                    retryCount++
                                    Log.d("RentalDetail", "尝试重新加载图片: ${rental.cover}, 重试次数: $retryCount")
                                }
                            }
                        )
                    }
                    
                    // 基本信息
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // 标题和价格
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = rental.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            
                            Text(
                                text = rental.priceText,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // 位置信息
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "位置",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(
                                text = rental.location,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // 房源标签
                        if (!rental.tags.isNullOrBlank()) {
                            Text(
                                text = "房源标签",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // 使用简单的 Row 和 Wrap 替代 FlowRow
                            TagList(tags = rental.tags.split(","))
                            
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        
                        // 详细信息
                        Text(
                            text = "房源详情",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // 详细信息卡片
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                DetailRow("户型", rental.structure)
                                DetailRow("面积", rental.areaText)
                                DetailRow("朝向", rental.orientation)
                                DetailRow("楼层", "${rental.floor}层")
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun TagList(tags: List<String>) {
    var currentRow = mutableListOf<String>()
    val rows = mutableListOf<List<String>>()
    
    tags.forEach { tag ->
        if (currentRow.size >= 3) {
            rows.add(currentRow.toList())
            currentRow = mutableListOf()
        }
        currentRow.add(tag.trim())
    }
    
    if (currentRow.isNotEmpty()) {
        rows.add(currentRow.toList())
    }
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
} 