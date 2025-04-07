package com.example.rental_recommend.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rental_recommend.components.HouseTag
import com.example.rental_recommend.model.House
import com.example.rental_recommend.utils.SimpleImageLoader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseDetailScreen(
    houseId: Int = 0,
    onNavigateBack: () -> Unit = {}
) {
    // 在实际应用中，应该根据houseId从数据源获取房源详情
    // 这里使用模拟数据
    val house = remember {
        createMockHouses().find { it.id == houseId } ?: createMockHouses().first()
    }
    
    var isFavorite by remember { mutableStateOf(true) } // 默认为已收藏状态
    val images = house.imgs.split("\n")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("房源详情") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { isFavorite = !isFavorite }
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) "取消收藏" else "收藏",
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                    IconButton(onClick = { /* 分享功能 */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "分享"
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // 图片轮播
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    ) {
                        // 仅显示第一张图片，实际应用中应实现轮播效果
                        SimpleImageLoader(
                            model = house.cover,
                            contentDescription = house.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        
                        // 图片计数指示器
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Black.copy(alpha = 0.6f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .align(Alignment.BottomEnd)
                        ) {
                            Text(
                                text = "1/${images.size}",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                        
                        // 房源类型标签
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .align(Alignment.TopStart)
                        ) {
                            Text(
                                text = house.type,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                
                // 房源标题和价格
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = house.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            
                            Text(
                                text = house.priceText,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // 位置信息
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${house.city} ${house.location}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                // 标签
                item {
                    if (house.tags.isNotEmpty()) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(house.tags.split(",")) { tag ->
                                HouseTag(tag.trim())
                            }
                        }
                    }
                }
                
                // 房源基本信息
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "房源信息",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                InfoItem(label = "户型", value = house.structure)
                                InfoItem(label = "面积", value = house.areaText)
                                InfoItem(label = "朝向", value = house.orientation)
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                InfoItem(label = "楼层", value = "${house.floor}层(${house.level})")
                                InfoItem(label = "区域", value = house.location.split("-").firstOrNull() ?: "")
                                InfoItem(label = "出租方式", value = house.type)
                            }
                        }
                    }
                }
                
                // 房源详情（HTML内容）
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "房源详情",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Divider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // 在实际应用中，应该使用WebView或HTML解析库展示详情
                            Text(
                                text = "这里是房源的详细描述内容。包括周边配套、交通状况、房屋特点等信息。",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                // 更多图片
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "房源图片",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            
                            // 图片网格（这里简化为单列展示）
                            images.forEachIndexed { index, imageUrl ->
                                if (index > 0) { // 跳过第一张图（已在顶部展示）
                                    SimpleImageLoader(
                                        model = imageUrl,
                                        contentDescription = "房源图片 ${index + 1}",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    )
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
                
                // 空白间隔
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
            
            // 底部联系按钮
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Button(
                    onClick = { /* 联系房东 */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "联系房东",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// 使用从FavoriteScreen.kt中相同的模拟数据函数
private fun createMockHouses(): List<House> {
    return listOf(
        House(
            id = 1,
            cover = "https://img.zcool.cn/community/0156cb5cd95889a8012109ec19f56a.jpg",
            type = "整租",
            title = "海淀区知春路精装两室一厅",
            oid = "12345",
            url = "https://example.com/detail/12345",
            location = "海淀区-知春路",
            areaText = "89㎡",
            area = 89.0,
            orientation = "朝南",
            structure = "2室1厅1卫",
            priceText = "3500元/月",
            price = 3500.0,
            tags = "精装,近地铁,拎包入住",
            level = "高楼层",
            floor = 18,
            province = "北京市",
            city = "北京",
            imgs = "https://img.zcool.cn/community/0156cb5cd95889a8012109ec19f56a.jpg\nhttps://img.zcool.cn/community/0156cb5cd95889a8012109ec19f56a.jpg",
            detail = "<div>房源详情HTML内容</div>"
        ),
        House(
            id = 2,
            cover = "https://img.zcool.cn/community/01c2265cd1b668a8012187f4261189.jpg",
            type = "合租",
            title = "海淀区大钟寺主卧出租",
            oid = "67890",
            url = "https://example.com/detail/67890",
            location = "海淀区-大钟寺",
            areaText = "20㎡",
            area = 20.0,
            orientation = "朝东",
            structure = "3室1厅1卫",
            priceText = "1800元/月",
            price = 1800.0,
            tags = "主卧,独卫,近地铁",
            level = "中楼层",
            floor = 11,
            province = "北京市",
            city = "北京",
            imgs = "https://img.zcool.cn/community/01c2265cd1b668a8012187f4261189.jpg\nhttps://img.zcool.cn/community/01c2265cd1b668a8012187f4261189.jpg",
            detail = "<div>房源详情HTML内容</div>"
        ),
        House(
            id = 3,
            cover = "https://img.zcool.cn/community/0173a35cd95889a801213f9a5e6c10.jpg",
            type = "整租",
            title = "滨江区滨和路豪华三室两厅",
            oid = "54321",
            url = "https://example.com/detail/54321",
            location = "滨江区-滨和路",
            areaText = "120㎡",
            area = 120.0,
            orientation = "朝南",
            structure = "3室2厅2卫",
            priceText = "6800元/月",
            price = 6800.0,
            tags = "豪华装修,近公园,地铁口",
            level = "高楼层",
            floor = 25,
            province = "浙江省",
            city = "杭州",
            imgs = "https://img.zcool.cn/community/0173a35cd95889a801213f9a5e6c10.jpg\nhttps://img.zcool.cn/community/0173a35cd95889a801213f9a5e6c10.jpg",
            detail = "<div>房源详情HTML内容</div>"
        ),
        House(
            id = 4,
            cover = "https://img.zcool.cn/community/01a3845cd95889a8012187f42fe014.jpg",
            type = "整租",
            title = "江干区新塘路精品单身公寓",
            oid = "98765",
            url = "https://example.com/detail/98765",
            location = "江干区-新塘路",
            areaText = "45㎡",
            area = 45.0,
            orientation = "朝西",
            structure = "1室1厅1卫",
            priceText = "2600元/月",
            price = 2600.0,
            tags = "精装,近商圈,有电梯",
            level = "中楼层",
            floor = 12,
            province = "浙江省",
            city = "杭州",
            imgs = "https://img.zcool.cn/community/01a3845cd95889a8012187f42fe014.jpg\nhttps://img.zcool.cn/community/01a3845cd95889a8012187f42fe014.jpg",
            detail = "<div>房源详情HTML内容</div>"
        ),
        House(
            id = 5,
            cover = "https://img.zcool.cn/community/01c01a5cd95889a8012187f4d19afa.jpg",
            type = "合租",
            title = "上城区清波门次卧出租",
            oid = "24680",
            url = "https://example.com/detail/24680",
            location = "上城区-清波门",
            areaText = "15㎡",
            area = 15.0,
            orientation = "朝南",
            structure = "4室1厅2卫",
            priceText = "1500元/月",
            price = 1500.0,
            tags = "次卧,押一付一,有阳台",
            level = "低楼层",
            floor = 3,
            province = "浙江省",
            city = "杭州",
            imgs = "https://img.zcool.cn/community/01c01a5cd95889a8012187f4d19afa.jpg\nhttps://img.zcool.cn/community/01c01a5cd95889a8012187f4d19afa.jpg",
            detail = "<div>房源详情HTML内容</div>"
        )
    )
} 