package com.example.myapplication

// Player's equipment state
data class EquipmentSystemState(
    val inventory: Map<String, Equipment> = mapOf(),
    val equippedItems: Map<EquipmentCategory, String> = mapOf(), // Category -> Equipment ID
    val virtualCurrency: Int = 500, // Starting currency
    val maxEquippedPerCategory: Map<EquipmentCategory, Int> = mapOf(
        EquipmentCategory.HARDWARE to 2,
        EquipmentCategory.SOFTWARE to 3,
        EquipmentCategory.NETWORK to 1,
        EquipmentCategory.UTILITY to 2,
        EquipmentCategory.SPECIAL to 1
    )
) 