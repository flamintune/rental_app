package com.example.rental_recommend.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.rental_recommend.R

/**
 * 简单的图片加载组件，用于替代 Coil 的 AsyncImage
 * 由于我们没有添加 Coil 依赖，这里使用占位符图片或资源 ID
 */
@Composable
fun SimpleImageLoader(
    model: Any,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    placeholderResId: Int = R.drawable.placeholder_image
) {
    // 如果 model 是 Int 类型，假设它是资源 ID
    if (model is Int) {
        Image(
            painter = painterResource(id = model),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    } else {
        // 否则显示占位符图片
        Box(modifier = modifier) {
            Image(
                painter = painterResource(id = placeholderResId),
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale
            )
        }
    }
} 