package com.example.myapplication

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import com.example.myapplication.EquipmentSystemState

// Categories of equipment
enum class EquipmentCategory {
    HARDWARE,      // Physical devices
    SOFTWARE,      // Installed software
    NETWORK,       // Network devices
    UTILITY,       // Utility items
    SPECIAL        // Rare or special items
}

// Rarity levels for equipment
enum class EquipmentRarity {
    COMMON,
    UNCOMMON,
    RARE,
    EPIC,
    LEGENDARY
}

// Types of equipment effects
enum class EquipmentEffectType {
    CRACK_SPEED,        // Increase password cracking speed
    SCAN_SPEED,         // Increase network scan speed
    SCAN_DETAIL,        // More detailed scan results
    CONNECTION_SPEED,   // Faster data transfer
    STEALTH,            // Reduce detection chance
    DATA_CAPACITY,      // Increase data storage
    ENCRYPTION,         // Better encryption/decryption
    EXPLOIT_BOOST,      // Better exploit effectiveness
    DEFENSE,            // Better defense against counter-attacks
    SCRIPT_BOOST,       // Improve script performance
    HARDWARE_BOOST,     // Boost to physical hardware capabilities
    SPECIAL_ABILITY     // Special unique abilities
}

// Effect provided by equipment
data class EquipmentEffect(
    val type: EquipmentEffectType,
    val value: Float,   // Percentage or flat value boost
    val isPercentage: Boolean = true,
    val scope: String = "" // Optional scope/context for the effect
)

// Equipment that can be used by the player
data class Equipment(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val category: EquipmentCategory,
    val rarity: EquipmentRarity,
    val level: Int = 1,
    val maxLevel: Int = 3,
    val basePrice: Int = 100, // Base price for purchasing
    val upgradePrice: Int = 200, // Price to upgrade per level
    val isEquipped: Boolean = false,
    val effects: List<EquipmentEffect> = listOf(),
    val requiredPlayerLevel: Int = 1,
    val commandProvided: String = "", // Special command this equipment provides
    val isConsumable: Boolean = false, // Whether it's used up when used
    val usesRemaining: Int = -1, // -1 means infinite uses
    val cooldownTime: Int = 0 // Cooldown time in seconds before it can be used again
)

// Equipment System to manage player's gear
class EquipmentSystem {
    private val _state = MutableStateFlow(EquipmentSystemState())
    val state: StateFlow<EquipmentSystemState> = _state.asStateFlow()
    
    // Get shop items that are available for purchase
    fun getShopItems(): Map<String, Equipment> {
        // Show only equipment not already in inventory
        return defaultEquipment().filter { !_state.value.inventory.containsKey(it.key) }
    }
    
    // Get items organized by category
    fun getItemsByCategory(category: EquipmentCategory): List<Equipment> {
        return _state.value.inventory.values.toList().filter { it.category == category }
    }
    
    // Get all currently equipped items
    fun getEquippedItems(): List<Equipment> {
        return _state.value.inventory.values.toList().filter { it.isEquipped }
    }
    
    // Add currency to player's account
    fun addCurrency(amount: Int) {
        _state.update { 
            it.copy(virtualCurrency = it.virtualCurrency + amount) 
        }
    }
    
    // Calculate total effects from all equipped items
    fun calculateEquippedEffects(): Map<EquipmentEffectType, Float> {
        val effects = mutableMapOf<EquipmentEffectType, Float>()
        
        // Get all equipped items
        val equippedItems = getEquippedItems()
        
        // Calculate total effect values by type
        for (item in equippedItems) {
            for (effect in item.effects) {
                val currentValue = effects.getOrDefault(effect.type, 0f)
                effects[effect.type] = currentValue + effect.value
            }
        }
        
        return effects
    }
    
    companion object {
        // Make defaultEquipment accessible as a static method
        fun defaultEquipment(): Map<String, Equipment> {
            val equipment: MutableMap<String, Equipment> = mutableMapOf()
            
            // HARDWARE category equipment
            equipment["hw_basic_cpu"] = Equipment(
                id = "hw_basic_cpu",
                name = "Basic CPU",
                description = "Standard processor for basic hacking operations",
                category = EquipmentCategory.HARDWARE,
                rarity = EquipmentRarity.COMMON,
                basePrice = 100,
                effects = listOf(
                    EquipmentEffect(EquipmentEffectType.CRACK_SPEED, 5f),
                    EquipmentEffect(EquipmentEffectType.SCAN_SPEED, 5f)
                )
            )
            
            equipment["hw_advanced_cpu"] = Equipment(
                id = "hw_advanced_cpu",
                name = "Advanced CPU",
                description = "High-performance processor for intensive operations",
                category = EquipmentCategory.HARDWARE,
                rarity = EquipmentRarity.UNCOMMON,
                basePrice = 350,
                requiredPlayerLevel = 2,
                effects = listOf(
                    EquipmentEffect(EquipmentEffectType.CRACK_SPEED, 15f),
                    EquipmentEffect(EquipmentEffectType.SCAN_SPEED, 15f),
                    EquipmentEffect(EquipmentEffectType.CONNECTION_SPEED, 10f)
                )
            )
            
            equipment["hw_quantum_processor"] = Equipment(
                id = "hw_quantum_processor",
                name = "Quantum Processor",
                description = "Experimental quantum CPU for maximum performance",
                category = EquipmentCategory.HARDWARE,
                rarity = EquipmentRarity.LEGENDARY,
                basePrice = 2000,
                requiredPlayerLevel = 8,
                effects = listOf(
                    EquipmentEffect(EquipmentEffectType.CRACK_SPEED, 50f),
                    EquipmentEffect(EquipmentEffectType.SCAN_SPEED, 40f),
                    EquipmentEffect(EquipmentEffectType.CONNECTION_SPEED, 35f),
                    EquipmentEffect(EquipmentEffectType.SPECIAL_ABILITY, 25f)
                )
            )
            
            // SOFTWARE category equipment
            equipment["sw_basic_antivirus"] = Equipment(
                id = "sw_basic_antivirus",
                name = "Basic Antivirus",
                description = "Simple protection against basic threats",
                category = EquipmentCategory.SOFTWARE,
                rarity = EquipmentRarity.COMMON,
                basePrice = 80,
                effects = listOf(
                    EquipmentEffect(EquipmentEffectType.DEFENSE, 10f)
                )
            )
            
            equipment["sw_exploit_kit"] = Equipment(
                id = "sw_exploit_kit",
                name = "Exploit Kit",
                description = "Collection of vulnerability exploits",
                category = EquipmentCategory.SOFTWARE,
                rarity = EquipmentRarity.UNCOMMON,
                basePrice = 250,
                requiredPlayerLevel = 2,
                effects = listOf(
                    EquipmentEffect(EquipmentEffectType.EXPLOIT_BOOST, 20f)
                )
            )
            
            equipment["sw_neural_decoder"] = Equipment(
                id = "sw_neural_decoder",
                name = "Neural Decoder",
                description = "AI-powered password cracking software",
                category = EquipmentCategory.SOFTWARE,
                rarity = EquipmentRarity.EPIC,
                basePrice = 800,
                requiredPlayerLevel = 5,
                effects = listOf(
                    EquipmentEffect(EquipmentEffectType.CRACK_SPEED, 35f),
                    EquipmentEffect(EquipmentEffectType.ENCRYPTION, 25f)
                )
            )
            
            // NETWORK equipment
            equipment["net_basic_adapter"] = Equipment(
                id = "net_basic_adapter",
                name = "Basic Network Adapter",
                description = "Standard network interface for connectivity",
                category = EquipmentCategory.NETWORK,
                rarity = EquipmentRarity.COMMON,
                basePrice = 120,
                effects = listOf(
                    EquipmentEffect(EquipmentEffectType.CONNECTION_SPEED, 10f)
                )
            )
            
            equipment["net_advanced_router"] = Equipment(
                id = "net_advanced_router",
                name = "Advanced Router",
                description = "High-performance network router",
                category = EquipmentCategory.NETWORK,
                rarity = EquipmentRarity.RARE,
                basePrice = 500,
                requiredPlayerLevel = 3,
                effects = listOf(
                    EquipmentEffect(EquipmentEffectType.CONNECTION_SPEED, 25f),
                    EquipmentEffect(EquipmentEffectType.STEALTH, 15f)
                )
            )
            
            // UTILITY equipment
            equipment["util_encryption_chip"] = Equipment(
                id = "util_encryption_chip",
                name = "Encryption Chip",
                description = "Hardware encryption module",
                category = EquipmentCategory.UTILITY,
                rarity = EquipmentRarity.UNCOMMON,
                basePrice = 200,
                effects = listOf(
                    EquipmentEffect(EquipmentEffectType.ENCRYPTION, 20f),
                    EquipmentEffect(EquipmentEffectType.STEALTH, 10f)
                )
            )
            
            equipment["util_cooling_system"] = Equipment(
                id = "util_cooling_system",
                name = "Liquid Cooling System",
                description = "Allows hardware to run at peak performance",
                category = EquipmentCategory.UTILITY,
                rarity = EquipmentRarity.UNCOMMON,
                basePrice = 180,
                requiredPlayerLevel = 2,
                effects = listOf(
                    EquipmentEffect(EquipmentEffectType.HARDWARE_BOOST, 15f)
                )
            )
            
            // SPECIAL equipment
            equipment["special_data_ghost"] = Equipment(
                id = "special_data_ghost",
                name = "Data Ghost",
                description = "Special module that hides your identity",
                category = EquipmentCategory.SPECIAL,
                rarity = EquipmentRarity.EPIC,
                basePrice = 1000,
                requiredPlayerLevel = 6,
                effects = listOf(
                    EquipmentEffect(EquipmentEffectType.STEALTH, 40f),
                    EquipmentEffect(EquipmentEffectType.SPECIAL_ABILITY, 15f)
                )
            )
            
            // Consumables
            equipment["util_single_use_vpn"] = Equipment(
                id = "util_single_use_vpn",
                name = "Single-Use VPN",
                description = "One-time VPN for extreme stealth during a hack",
                category = EquipmentCategory.UTILITY,
                rarity = EquipmentRarity.UNCOMMON,
                basePrice = 150,
                isConsumable = true,
                usesRemaining = 1,
                effects = listOf(
                    EquipmentEffect(EquipmentEffectType.STEALTH, 50f)
                )
            )
            
            equipment["util_system_booster"] = Equipment(
                id = "util_system_booster",
                name = "System Booster",
                description = "Temporary boost to all system performance",
                category = EquipmentCategory.UTILITY,
                rarity = EquipmentRarity.RARE,
                basePrice = 300,
                requiredPlayerLevel = 3,
                isConsumable = true,
                usesRemaining = 3,
                effects = listOf(
                    EquipmentEffect(EquipmentEffectType.HARDWARE_BOOST, 25f),
                    EquipmentEffect(EquipmentEffectType.CONNECTION_SPEED, 25f),
                    EquipmentEffect(EquipmentEffectType.CRACK_SPEED, 25f)
                )
            )
            
            return equipment
        }
    }
    
    // Function to buy equipment
    fun buyEquipment(equipmentId: String, playerLevel: Int, playerCredits: Int): Boolean {
        val equipment = defaultEquipment()[equipmentId] ?: return false
        
        // Check if player has required level
        if (playerLevel < equipment.requiredPlayerLevel) {
            return false
        }
        
        // Check if player can afford it
        if (playerCredits < equipment.basePrice) {
            return false
        }
        
        // Add to inventory with isEquipped = false
        val updatedInventory: MutableMap<String, Equipment> = _state.value.inventory.toMutableMap()
        updatedInventory[equipmentId] = equipment.copy(isEquipped = false)
        
        _state.update { 
            it.copy(inventory = updatedInventory) 
        }
        
        return true
    }
    
    // Function to equip an item
    fun equipItem(equipmentId: String): Boolean {
        val currentState = _state.value
        
        // Check if the equipment exists in inventory
        val equipment = currentState.inventory[equipmentId] ?: return false
        
        // Check if we've reached the max equipped items for this category
        val equippedInCategory = currentState.inventory.values.count { 
            it.category == equipment.category && it.isEquipped 
        }
        val maxInCategory = currentState.maxEquippedPerCategory[equipment.category] ?: 1
        
        if (equippedInCategory >= maxInCategory && !equipment.isEquipped) {
            return false
        }
        
        // Update the equipment's isEquipped status
        val updatedInventory: MutableMap<String, Equipment> = currentState.inventory.toMutableMap()
        updatedInventory[equipmentId] = equipment.copy(isEquipped = !equipment.isEquipped)
        
        _state.update { 
            it.copy(inventory = updatedInventory)
        }
        
        return true
    }
    
    // Function to unequip an item
    fun unequipItem(equipmentId: String): Boolean {
        val currentState = _state.value
        
        // Check if the equipment exists and is equipped
        val equipment = currentState.inventory[equipmentId] ?: return false
        
        if (!equipment.isEquipped) {
            return false
        }
        
        // Update the equipment's isEquipped status
        val updatedInventory: MutableMap<String, Equipment> = currentState.inventory.toMutableMap()
        updatedInventory[equipmentId] = equipment.copy(isEquipped = false)
        
        _state.update { 
            it.copy(inventory = updatedInventory)
        }
        
        return true
    }
    
    // Function to upgrade equipment
    fun upgradeEquipment(equipmentId: String, playerCredits: Int): Boolean {
        val currentState = _state.value
        
        // Check if player owns this equipment
        val equipment = currentState.inventory[equipmentId] ?: return false
        
        // Check if it can be upgraded further
        if (equipment.level >= equipment.maxLevel) {
            return false
        }
        
        // Check if player can afford the upgrade
        if (playerCredits < equipment.upgradePrice) {
            return false
        }
        
        // Upgrade the equipment - increase level and effects
        val updatedEffects = equipment.effects.map { effect ->
            // Increase effect value by 20% per level
            val newValue = effect.value * 1.2f
            effect.copy(value = newValue)
        }
        
        val updatedEquipment = equipment.copy(
            level = equipment.level + 1,
            effects = updatedEffects
        )
        
        // Update inventory
        val updatedInventory: MutableMap<String, Equipment> = currentState.inventory.toMutableMap()
        updatedInventory[equipmentId] = updatedEquipment
        
        _state.update { 
            it.copy(inventory = updatedInventory) 
        }
        
        return true
    }
    
    // Use a consumable item
    fun useConsumable(equipmentId: String): Boolean {
        val currentState = _state.value
        
        // Check if player owns this equipment
        val equipment = currentState.inventory[equipmentId] ?: return false
        
        // Check if it's a consumable
        if (!equipment.isConsumable) {
            return false
        }
        
        // Check if there are uses remaining
        if (equipment.usesRemaining == 0) {
            return false
        }
        
        // Calculate remaining uses
        val usesRemaining = if (equipment.usesRemaining > 0) {
            equipment.usesRemaining - 1
        } else {
            -1 // Infinite uses
        }
        
        // Update equipment with reduced uses
        val updatedInventory: MutableMap<String, Equipment> = currentState.inventory.toMutableMap()
        
        if (usesRemaining == 0) {
            // Remove the item if no uses left
            updatedInventory.remove(equipmentId)
        } else {
            // Update uses remaining
            updatedInventory[equipmentId] = equipment.copy(usesRemaining = usesRemaining)
        }
        
        _state.update { 
            it.copy(inventory = updatedInventory) 
        }
        
        return true
    }
    
    // Add reward equipment to inventory (from mission rewards, etc.)
    fun addRewardEquipment(itemId: String): Boolean {
        // Get the equipment from default list
        val equipment = defaultEquipment()[itemId] ?: return false
        
        // Check if player already has this equipment
        if (_state.value.inventory.containsKey(itemId)) {
            return false
        }
        
        // Add to inventory
        val updatedInventory: MutableMap<String, Equipment> = _state.value.inventory.toMutableMap()
        updatedInventory[itemId] = equipment
        
        _state.update { 
            it.copy(
                inventory = updatedInventory
            ) 
        }
        
        return true
    }
} 