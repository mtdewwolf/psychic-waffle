package com.example.myapplication

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.myapplication.ui.theme.Orange

@Composable
fun EquipmentScreen(
    viewModel: TerminalViewModel,
    equipmentSystem: EquipmentSystem,
    onBack: () -> Unit,
    onNavigateToShop: () -> Unit
) {
    val equipmentState by viewModel.equipmentState.collectAsState()
    val terminalState by viewModel.terminalState.collectAsState()
    var selectedEquipment by remember { mutableStateOf<Equipment?>(null) }
    var showEquipmentDetails by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Green
                    )
                }
                
                Text(
                    text = "EQUIPMENT",
                    color = Color.Green,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                Text(
                    text = "CREDITS: ${terminalState.credits}",
                    color = Color.Green,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Shop button
                Button(
                    onClick = onNavigateToShop,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Green,
                        contentColor = Color.Black
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Shop",
                            tint = Color.Black
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        Text(
                            text = "SHOP",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Divider(
                color = Color.Green,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Equipped items section
            Text(
                text = "EQUIPPED",
                color = Color.Green,
                fontFamily = FontFamily.Monospace,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                val equippedItems = equipmentState.inventory.values.filter { it.isEquipped }
                
                items(equippedItems) { equipment ->
                    EquipmentCard(
                        equipment = equipment,
                        isEquipped = true,
                        onClick = {
                            selectedEquipment = equipment
                            showEquipmentDetails = true
                        }
                    )
                }
                
                // Add empty slots for categories with no equipped items
                val equippedCategories = equippedItems.map { it.category }.toSet()
                val missingCategories = EquipmentCategory.values().filter { it !in equippedCategories }
                
                items(missingCategories) { category ->
                    EmptyEquipmentSlot(
                        category = category,
                        onClick = { onNavigateToShop() }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Inventory section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "INVENTORY",
                    color = Color.Green,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            // Category filter
            var currentCategoryFilter by remember { mutableStateOf<EquipmentCategory?>(null) }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // All categories button
                FilterChip(
                    selected = currentCategoryFilter == null,
                    onClick = { currentCategoryFilter = null },
                    label = { Text("ALL") },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.DarkGray,
                        labelColor = Color.White,
                        selectedContainerColor = Color.Green,
                        selectedLabelColor = Color.Black
                    )
                )
                
                // Specific category filters
                EquipmentCategory.values().forEach { category ->
                    FilterChip(
                        selected = currentCategoryFilter == category,
                        onClick = { currentCategoryFilter = category },
                        label = { Text(category.name) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color.DarkGray,
                            labelColor = Color.White,
                            selectedContainerColor = Color.Green,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }
            
            if (equipmentState.inventory.values.none { !it.isEquipped }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Your inventory is empty. Visit the shop to purchase equipment.",
                        color = Color.Gray,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Filter inventory items by category if a filter is applied
                val inventoryItems = equipmentState.inventory.values.filter { !it.isEquipped }.let { items ->
                    if (currentCategoryFilter != null) {
                        items.filter { it.category == currentCategoryFilter }
                    } else {
                        items
                    }
                }
                
                if (inventoryItems.isEmpty() && currentCategoryFilter != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No ${currentCategoryFilter!!.name} items in your inventory.",
                            color = Color.Gray,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        items(inventoryItems) { equipment ->
                            EquipmentCard(
                                equipment = equipment,
                                isEquipped = false,
                                onClick = {
                                    selectedEquipment = equipment
                                    showEquipmentDetails = true
                                }
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
        
        // Equipment details dialog
        if (showEquipmentDetails && selectedEquipment != null) {
            EquipmentDetailsDialog(
                equipment = selectedEquipment!!,
                onDismiss = { showEquipmentDetails = false },
                onEquip = {
                    viewModel.equipItem(selectedEquipment!!.id)
                    showEquipmentDetails = false
                },
                onUnequip = {
                    viewModel.unequipItem(selectedEquipment!!.id)
                    showEquipmentDetails = false
                },
                onUse = {
                    viewModel.useEquipment(listOf(selectedEquipment!!.id))
                    showEquipmentDetails = false
                },
                onUpgrade = {
                    viewModel.upgradeEquipment(selectedEquipment!!.id)
                    showEquipmentDetails = false
                }
            )
        }
    }
}

@Composable
fun EquipmentCard(
    equipment: Equipment,
    isEquipped: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when (equipment.rarity) {
        EquipmentRarity.COMMON -> Color(0xFF1A1A2E)
        EquipmentRarity.UNCOMMON -> Color(0xFF003300)
        EquipmentRarity.RARE -> Color(0xFF000066)
        EquipmentRarity.EPIC -> Color(0xFF330066)
        EquipmentRarity.LEGENDARY -> Color(0xFF663300)
    }
    
    val borderColor = when (equipment.rarity) {
        EquipmentRarity.COMMON -> Color.Gray
        EquipmentRarity.UNCOMMON -> Color.Green
        EquipmentRarity.RARE -> Color.Blue
        EquipmentRarity.EPIC -> Color.Magenta
        EquipmentRarity.LEGENDARY -> Orange
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon and name
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🔧", // Default emoji for equipment
                        fontSize = 16.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    
                    Text(
                        text = equipment.name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Level
                Text(
                    text = "Lvl ${equipment.level}",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Category and rarity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = equipment.category.name,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
                
                Text(
                    text = equipment.rarity.name,
                    color = borderColor,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            
            if (isEquipped) {
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF003300),
                        contentColor = Color.Green
                    ),
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.End),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "EQUIPPED",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            } else if (equipment.isConsumable) {
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF003366),
                        contentColor = Color.Cyan
                    ),
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.End),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "CONSUMABLE",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyEquipmentSlot(
    category: EquipmentCategory,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1A1A1A))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Empty Slot",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = category.name,
                color = Color.Gray,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun EquipmentDetailsDialog(
    equipment: Equipment,
    onDismiss: () -> Unit,
    onEquip: () -> Unit,
    onUnequip: () -> Unit,
    onUse: () -> Unit,
    onUpgrade: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF0A0A0A)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔧", // Default emoji for equipment
                        fontSize = 24.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    
                    Column {
                        Text(
                            text = equipment.name,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Row {
                            Text(
                                text = equipment.category.name,
                                color = Color.Gray,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            val rarityColor = when (equipment.rarity) {
                                EquipmentRarity.COMMON -> Color.Gray
                                EquipmentRarity.UNCOMMON -> Color.Green
                                EquipmentRarity.RARE -> Color.Blue
                                EquipmentRarity.EPIC -> Color.Magenta
                                EquipmentRarity.LEGENDARY -> Orange
                            }
                            
                            Text(
                                text = equipment.rarity.name,
                                color = rarityColor,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Text(
                        text = "Level ${equipment.level}",
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    )
                }
                
                Divider(
                    color = Color.DarkGray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                // Description
                Text(
                    text = equipment.description,
                    color = Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Effects
                Text(
                    text = "EFFECTS:",
                    color = Color.Cyan,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                
                equipment.effects.forEach { effect ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        val effectColor = when (effect.type) {
                            EquipmentEffectType.CRACK_SPEED -> Color.Red
                            EquipmentEffectType.SCAN_SPEED -> Color.Green
                            EquipmentEffectType.SCAN_DETAIL -> Color.Blue
                            EquipmentEffectType.CONNECTION_SPEED -> Color.Cyan
                            EquipmentEffectType.STEALTH -> Color.Yellow
                            EquipmentEffectType.DATA_CAPACITY -> Color.Green
                            EquipmentEffectType.ENCRYPTION -> Color.Yellow
                            EquipmentEffectType.EXPLOIT_BOOST -> Color.Red
                            EquipmentEffectType.DEFENSE -> Color.Blue
                            EquipmentEffectType.SCRIPT_BOOST -> Color.Green
                            EquipmentEffectType.HARDWARE_BOOST -> Color.Cyan
                            EquipmentEffectType.SPECIAL_ABILITY -> Color.Magenta
                        }
                        
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = effectColor,
                            modifier = Modifier.size(16.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "${effect.type.name}: +${effect.value}",
                            color = effectColor,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // If upgradable, show next level stats
                if (!equipment.isConsumable) {
                    Text(
                        text = "NEXT LEVEL:",
                        color = Color.Yellow,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    equipment.effects.forEach { effect ->
                        val nextLevelValue = (effect.value * 1.25f).toInt()
                        Text(
                            text = "${effect.type.name}: +$nextLevelValue (+${nextLevelValue - effect.value.toInt()})",
                            color = Color.Yellow,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp
                        )
                    }
                    
                    val upgradeCost = (equipment.upgradePrice * equipment.level)
                    Text(
                        text = "Upgrade Cost: $upgradeCost Credits",
                        color = Color.Green,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                
                // Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Close button
                    OutlinedButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Gray
                        ),
                        border = BorderStroke(1.dp, Color.Gray)
                    ) {
                        Text(
                            text = "CLOSE",
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    
                    // Action buttons based on equipment state
                    if (equipment.isEquipped) {
                        // Unequip button
                        Button(
                            onClick = onUnequip,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "UNEQUIP",
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    } else if (equipment.isConsumable) {
                        // Use button for consumables
                        Button(
                            onClick = onUse,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Cyan,
                                contentColor = Color.Black
                            )
                        ) {
                            Text(
                                text = "USE",
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    } else {
                        // Equip button for non-consumables
                        Button(
                            onClick = onEquip,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Green,
                                contentColor = Color.Black
                            )
                        ) {
                            Text(
                                text = "EQUIP",
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                    
                    // Upgrade button (only for non-consumables)
                    if (!equipment.isConsumable) {
                        Button(
                            onClick = onUpgrade,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Yellow,
                                contentColor = Color.Black
                            )
                        ) {
                            Text(
                                text = "UPGRADE",
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
} 