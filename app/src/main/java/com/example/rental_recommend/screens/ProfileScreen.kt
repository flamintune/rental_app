package com.example.rental_recommend.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rental_recommend.model.User
import com.example.rental_recommend.viewmodel.ProfileState
import com.example.rental_recommend.viewmodel.ProfileViewModel
import androidx.compose.runtime.collectAsState
import kotlin.math.max
import kotlin.math.min

@Composable
fun ProfileScreen(
    onNavigateToFavorite: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val profileState by viewModel.profileState.collectAsState()
    
    // 加载用户数据
    LaunchedEffect(Unit) {
        viewModel.loadUserProfile(context)
    }
    
    when (val state = profileState) {
        is ProfileState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is ProfileState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        is ProfileState.Success -> {
            // 对话框状态
            var showEditProfileDialog by remember { mutableStateOf(false) }
            var showRentalPreferenceDialog by remember { mutableStateOf(false) }
            var showLogoutDialog by remember { mutableStateOf(false) }
            
            // 编辑个人资料对话框
            if (showEditProfileDialog) {
                EditProfileDialog(
                    user = state.user,
                    onDismiss = { showEditProfileDialog = false },
                    onSave = { updatedUser -> 
                        viewModel.updateUserProfile(
                            context = context,
                            nickname = updatedUser.nickname,
                            email = updatedUser.email,
                            phone = updatedUser.phone,
                            gender = updatedUser.gender
                        )
                        showEditProfileDialog = false
                    }
                )
            }
            
            // 编辑租房偏好对话框
            if (showRentalPreferenceDialog) {
                EditRentalPreferenceDialog(
                    user = state.user,
                    onDismiss = { showRentalPreferenceDialog = false },
                    onSave = { updatedUser -> 
                        viewModel.updateUserProfile(
                            context = context,
                            budgetMin = updatedUser.budgetMin,
                            budgetMax = updatedUser.budgetMax,
                            preferredAreas = updatedUser.preferredAreas,
                            houseType = updatedUser.houseType
                        )
                        showRentalPreferenceDialog = false
                    }
                )
            }

            // 退出登录确认对话框
            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    title = { Text("确认退出") },
                    text = { Text("确定要退出登录吗？") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showLogoutDialog = false
                                onLogout()
                            }
                        ) {
                            Text("确定")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLogoutDialog = false }) {
                            Text("取消")
                        }
                    }
                )
            }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        // 用户信息头部
                        UserProfileHeader(state.user)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // 个人信息
                        SettingsCategory(title = "个人信息") {
                            SettingsItem(
                                icon = Icons.Default.AccountCircle, 
                                title = "基本资料", 
                                subtitle = "${state.user.nickname ?: state.user.username} · ${state.user.gender} · ${state.user.email}",
                                onClick = { showEditProfileDialog = true }
                            )
                            SettingsItem(
                                icon = Icons.Default.Call, 
                                title = "联系电话", 
                                subtitle = state.user.phone ?: "未设置",
                                onClick = { showEditProfileDialog = true }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // 租房偏好
                        SettingsCategory(title = "租房偏好") {
                            SettingsItem(
                                icon = Icons.Default.Home, 
                                title = "房源类型", 
                                subtitle = state.user.houseType ?: "未设置",
                                onClick = { showRentalPreferenceDialog = true }
                            )
                            SettingsItem(
                                icon = Icons.Outlined.Payments, 
                                title = "预算范围", 
                                subtitle = if (state.user.budgetMin != null && state.user.budgetMax != null) 
                                    "¥${state.user.budgetMin?.toInt() ?: 0}-${state.user.budgetMax?.toInt() ?: 0}/月"
                                else 
                                    "未设置",
                                onClick = { showRentalPreferenceDialog = true }
                            )
                            SettingsItem(
                                icon = Icons.Default.Favorite, 
                                title = "意向区域", 
                                subtitle = if (state.user.preferredAreas.isNotEmpty()) 
                                    state.user.preferredAreas.joinToString(", ")
                                else 
                                    "未设置",
                                onClick = { showRentalPreferenceDialog = true }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))

                        // 退出登录按钮
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Button(
                                onClick = { showLogoutDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "退出登录",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("退出登录", fontSize = 16.sp)
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
fun UserProfileHeader(user: User) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 用户头像 - 使用首字母+随机颜色背景
            val avatarBackground = remember {
                val colors = listOf(
                    Color(0xFF6200EE), // 紫色
                    Color(0xFF03DAC5), // 青色
                    Color(0xFFFF5722), // 橙色
                    Color(0xFF4CAF50), // 绿色
                    Color(0xFFE91E63)  // 粉色
                )
                colors[user.username.hashCode().mod(colors.size)]
            }
            
            val avatarText = (user.nickname ?: user.username).take(1).uppercase()
            
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(avatarBackground)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = avatarText,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 用户名
            Text(
                text = user.nickname ?: user.username,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            
            // 显示用户ID
            Text(
                text = "ID: ${user.id}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // 用户身份标签
            Row(
                modifier = Modifier.padding(top = 4.dp)
            ) {
                if (user.isLandlord) {
                    Badge(text = "房东", backgroundColor = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                
                if (user.isTenant) {
                    Badge(text = "租客", backgroundColor = MaterialTheme.colorScheme.tertiary)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileDialog(
    user: User,
    onDismiss: () -> Unit,
    onSave: (User) -> Unit
) {
    var nickname by remember { mutableStateOf(user.nickname ?: "") }
    var email by remember { mutableStateOf(user.email) }
    var phone by remember { mutableStateOf(user.phone ?: "") }
    var gender by remember { mutableStateOf(user.gender) }
    var expanded by remember { mutableStateOf(false) }
    val genderOptions = listOf("男", "女", "未设置")
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // 标题和关闭按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "编辑个人资料",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "关闭"
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 显示用户名（不可修改）
                OutlinedTextField(
                    value = user.username,
                    onValueChange = { },
                    label = { Text("用户名") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 昵称输入框
                OutlinedTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = { Text("昵称") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 邮箱输入框
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("电子邮箱") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 电话输入框
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("电话号码") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 性别选择下拉框
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = gender,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("性别") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        genderOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    gender = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 保存和取消按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("取消")
                    }
                    
                    Button(
                        onClick = {
                            // 创建更新后的用户对象
                            val updatedUser = user.copy(
                                nickname = nickname.takeIf { it.isNotBlank() },
                                email = email,
                                phone = phone.takeIf { it.isNotBlank() },
                                gender = gender
                            )
                            onSave(updatedUser)
                        }
                    ) {
                        Text("保存")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRentalPreferenceDialog(
    user: User,
    onDismiss: () -> Unit,
    onSave: (User) -> Unit
) {
    // 状态管理
    var houseType by remember { mutableStateOf(user.houseType ?: "") }
    var houseTypeExpanded by remember { mutableStateOf(false) }
    var areaExpanded by remember { mutableStateOf(false) }
    var selectedArea by remember { mutableStateOf("") }
    
    // 使用字符串状态来管理输入
    var budgetMinStr by remember { mutableStateOf((user.budgetMin?.toInt() ?: 1000).toString()) }
    var budgetMaxStr by remember { mutableStateOf((user.budgetMax?.toInt() ?: 5000).toString()) }
    
    // 数值转换函数
    val budgetMin = budgetMinStr.toIntOrNull() ?: 1000
    val budgetMax = budgetMaxStr.toIntOrNull() ?: 5000
    
    val houseTypeOptions = listOf("一室一厅", "两室一厅", "三室一厅", "三室两厅", "四室及以上", "单间")
    val areaOptions = listOf("锦江区", "青羊区", "金牛区", "武侯区", "成华区", "龙泉驿区", "青白江区", "新都区", "温江区", "双流区", "郫都区", "新津区")
    
    // 选中的区域列表 - 使用独立的可变状态列表
    var preferredAreas by remember { mutableStateOf(user.preferredAreas.toList()) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // 标题和关闭按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "租房偏好设置",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "关闭"
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 房型选择
                ExposedDropdownMenuBox(
                    expanded = houseTypeExpanded,
                    onExpandedChange = { houseTypeExpanded = !houseTypeExpanded }
                ) {
                    OutlinedTextField(
                        value = houseType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("房型偏好") },
                        placeholder = { Text("请选择您偏好的房型") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = houseTypeExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = houseTypeExpanded,
                        onDismissRequest = { houseTypeExpanded = false }
                    ) {
                        houseTypeOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    houseType = option
                                    houseTypeExpanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 预算范围文本输入字段
                Text(
                    text = "月租预算",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 预算输入字段布局
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 最小预算输入
                    OutlinedTextField(
                        value = budgetMinStr,
                        onValueChange = { input ->
                            // 只接受数字输入
                            if (input.isEmpty() || input.all { it.isDigit() }) {
                                budgetMinStr = input
                            }
                        },
                        label = { Text("最低预算") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        leadingIcon = { Text("¥") },
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    // 最大预算输入
                    OutlinedTextField(
                        value = budgetMaxStr,
                        onValueChange = { input ->
                            // 只接受数字输入
                            if (input.isEmpty() || input.all { it.isDigit() }) {
                                budgetMaxStr = input
                            }
                        },
                        label = { Text("最高预算") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        leadingIcon = { Text("¥") },
                        singleLine = true
                    )
                }
                
                // 提示信息
                if (budgetMin > budgetMax && budgetMinStr.isNotEmpty() && budgetMaxStr.isNotEmpty()) {
                    Text(
                        text = "最低预算不能大于最高预算",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 意向区域选择
                Text(
                    text = "意向区域",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 已选择的区域显示
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    preferredAreas.forEach { area ->
                        Badge(
                            text = area,
                            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                            onClose = {
                                // 创建新列表而不是修改原列表
                                val updatedAreas = preferredAreas.toMutableList()
                                updatedAreas.remove(area)
                                preferredAreas = updatedAreas
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 添加区域下拉框
                ExposedDropdownMenuBox(
                    expanded = areaExpanded,
                    onExpandedChange = { areaExpanded = !areaExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedArea,
                        onValueChange = { selectedArea = it },
                        label = { Text("添加意向区域") },
                        placeholder = { Text("请选择您意向的区域") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = areaExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = areaExpanded,
                        onDismissRequest = { areaExpanded = false }
                    ) {
                        areaOptions.forEach { option ->
                            if (!preferredAreas.contains(option)) {
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        selectedArea = ""
                                        if (!preferredAreas.contains(option)) {
                                            // 创建新列表而不是修改原列表
                                            val updatedAreas = preferredAreas.toMutableList()
                                            updatedAreas.add(option)
                                            preferredAreas = updatedAreas
                                        }
                                        areaExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 保存和取消按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("取消")
                    }
                    
                    Button(
                        onClick = {
                            // 验证输入
                            val validBudgetMin = budgetMinStr.toIntOrNull() ?: 1000
                            val validBudgetMax = budgetMaxStr.toIntOrNull() ?: 5000
                            
                            // 确保最小值不大于最大值
                            val finalBudgetMin = min(validBudgetMin, validBudgetMax).toDouble()
                            val finalBudgetMax = max(validBudgetMin, validBudgetMax).toDouble()
                            
                            // 创建更新后的用户对象
                            val updatedUser = user.copy(
                                houseType = houseType.takeIf { it.isNotBlank() },
                                budgetMin = finalBudgetMin,
                                budgetMax = finalBudgetMax,
                                preferredAreas = preferredAreas
                            )
                            onSave(updatedUser)
                        },
                        // 如果输入无效则禁用保存按钮
                        enabled = !(budgetMin > budgetMax && budgetMinStr.isNotEmpty() && budgetMaxStr.isNotEmpty())
                    ) {
                        Text("保存")
                    }
                }
            }
        }
    }
}

@Composable
fun Badge(
    text: String, 
    backgroundColor: Color,
    onClose: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .padding(
                start = 8.dp,
                top = 4.dp,
                end = if (onClose != null) 4.dp else 8.dp,
                bottom = 4.dp
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            if (onClose != null) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "删除",
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onClose() },
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun SettingsCategory(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            content()
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    badge: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        if (badge != null) {
            Badge(
                text = badge,
                backgroundColor = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "前往",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
    }
    
    Divider(
        modifier = Modifier.padding(start = 56.dp),
        color = MaterialTheme.colorScheme.outlineVariant,
        thickness = 0.5.dp
    )
} 