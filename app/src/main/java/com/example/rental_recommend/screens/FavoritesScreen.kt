package com.example.rental_recommend.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.rental_recommend.components.HouseCard
import com.example.rental_recommend.model.House

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onHouseClick: (House) -> Unit = {}
) {
    // 模拟数据加载状态
    var isLoading by remember { mutableStateOf(false) }
    // 模拟收藏房源数据
    val favoriteHouses = remember {
        createMockHouses()
    }
    // 列表滚动状态
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("我的收藏") },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
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
            if (isLoading) {
                // 加载指示器
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(50.dp)
                    )
                }
            } else if (favoriteHouses.isEmpty()) {
                // 空状态
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "暂无收藏",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "您可以浏览房源并添加到收藏",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                }
            } else {
                // 房源列表
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(favoriteHouses) { house ->
                        HouseCard(
                            house = house,
                            onHouseClick = { onHouseClick(house) },
                            onFavoriteClick = { /* 处理收藏状态变更 */ }
                        )
                    }
                }
            }
        }
    }
}

// 创建模拟数据
private fun createMockHouses(): List<House> {
    return listOf(
        House(
            id = 1,
            cover = "https://ke-image.ljcdn.com/110000-inspection/pc1_kLCs0NEj6_1.jpg!m_fill,w_250,h_182,l_fbk,o_auto",
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
            imgs = "https://s1.ljcdn.com/matrix_pc/dist/pc/src/resource/default/250-182_1.png?_v=20220908152455b3e",
            detail = "<div>房源详情HTML内容</div>"
        ),
        House(
            id = 2,
            cover = "https://s1.ljcdn.com/matrix_pc/dist/pc/src/resource/default/250-182_1.png?_v=20220908152455b3e",
            type = "合租",
            title = "拱墅区大关西六苑主卧出租",
            oid = "67890",
            url = "https://example.com/detail/67890",
            location = "拱墅区-大关",
            areaText = "20㎡",
            area = 20.0,
            orientation = "朝东",
            structure = "3室1厅1卫",
            priceText = "1800元/月",
            price = 1800.0,
            tags = "主卧,独卫,近地铁",
            level = "中楼层",
            floor = 11,
            province = "浙江省",
            city = "杭州",
            imgs = "https://s1.ljcdn.com/matrix_pc/dist/pc/src/resource/default/250-182_1.png?_v=20220908152455b3e",
            detail = "<div>房源详情HTML内容</div>"
        ),
        House(
            id = 3,
            cover = "https://s1.ljcdn.com/matrix_pc/dist/pc/src/resource/default/250-182_1.png?_v=20220908152455b3e",
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
            imgs = "https://s1.ljcdn.com/matrix_pc/dist/pc/src/resource/default/250-182_1.png?_v=20220908152455b3e",
            detail = "<div>房源详情HTML内容</div>"
        ),
        House(
            id = 4,
            cover = "https://s1.ljcdn.com/matrix_pc/dist/pc/src/resource/default/250-182_1.png?_v=20220908152455b3e",
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
            imgs = "https://s1.ljcdn.com/matrix_pc/dist/pc/src/resource/default/250-182_1.png?_v=20220908152455b3e",
            detail = "<div>房源详情HTML内容</div>"
        ),
        House(
            id = 5,
            cover = "https://s1.ljcdn.com/matrix_pc/dist/pc/src/resource/default/250-182_1.png?_v=20220908152455b3e",
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
            imgs = "https://s1.ljcdn.com/matrix_pc/dist/pc/src/resource/default/250-182_1.png?_v=20220908152455b3e",
            detail = "<div>房源详情HTML内容</div>"
        )
    )
} 