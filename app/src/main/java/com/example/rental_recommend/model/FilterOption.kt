package com.example.rental_recommend.model

// 筛选选项类型
enum class FilterType {
    LOCATION,   // 位置
    PRICE,      // 租金
    HOUSE_TYPE, // 户型
    AREA,       // 面积
    ORIENTATION // 朝向
}

// 筛选选项的值
sealed class FilterValue {
    data class Range(
        val min: Double? = null,
        val max: Double? = null,
        val displayText: String = "不限"
    ) : FilterValue()

    data class SingleChoice(
        val value: String,
        val displayText: String = value
    ) : FilterValue()

    data class Location(
        val province: String? = null,
        val city: String? = null,
        val displayText: String = "不限"
    ) : FilterValue()

    object None : FilterValue()
}

// 筛选选项数据类
data class FilterOption(
    val type: FilterType,
    val title: String,
    val value: FilterValue = FilterValue.None,
    val isSelected: Boolean = false
)

// 预定义的选项值
object FilterOptions {
    val priceRanges = listOf(
        FilterValue.Range(displayText = "不限"),
        FilterValue.Range(min = 0.0, max = 1000.0, displayText = "1000元以下"),
        FilterValue.Range(min = 1000.0, max = 2000.0, displayText = "1000-2000元"),
        FilterValue.Range(min = 2000.0, max = 3000.0, displayText = "2000-3000元"),
        FilterValue.Range(min = 3000.0, max = 4000.0, displayText = "3000-4000元"),
        FilterValue.Range(min = 4000.0, max = 5000.0, displayText = "4000-5000元"),
        FilterValue.Range(min = 5000.0, max = null, displayText = "5000元以上")
    )

    val areaRanges = listOf(
        FilterValue.Range(displayText = "不限"),
        FilterValue.Range(min = 0.0, max = 30.0, displayText = "30㎡以下"),
        FilterValue.Range(min = 30.0, max = 50.0, displayText = "30-50㎡"),
        FilterValue.Range(min = 50.0, max = 70.0, displayText = "50-70㎡"),
        FilterValue.Range(min = 70.0, max = 90.0, displayText = "70-90㎡"),
        FilterValue.Range(min = 90.0, max = null, displayText = "90㎡以上")
    )

    val houseTypes = listOf(
        FilterValue.SingleChoice("不限"),
        FilterValue.SingleChoice("整租"),
        FilterValue.SingleChoice("合租"),
        FilterValue.SingleChoice("公寓")
    )

    val orientations = listOf(
        FilterValue.SingleChoice("不限"),
        FilterValue.SingleChoice("东"),
        FilterValue.SingleChoice("南"),
        FilterValue.SingleChoice("西"),
        FilterValue.SingleChoice("北"),
        FilterValue.SingleChoice("东南"),
        FilterValue.SingleChoice("东北"),
        FilterValue.SingleChoice("西南"),
        FilterValue.SingleChoice("西北")
    )
} 