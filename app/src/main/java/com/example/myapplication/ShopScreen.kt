package com.example.myapplication

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.myapplication.ui.theme.Orange

@Composable
fun ShopScreen(
    viewModel: TerminalViewModel,
    equipmentSystem: EquipmentSystem,
    onBack: () -> Unit
) {
    val equipmentState by viewModel.equipmentState.collectAsState()
    val terminalState by viewModel.terminalState.collectAsState()
    var selectedEquipment by remember { mutableStateOf<Equipment?>(null) }
    var showEquipmentDetails by remember { mutableStateOf(false) }
    var currentCategoryFilter by remember { mutableStateOf<EquipmentCategory?>(null) }
    
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
                    text = "DIGITAL MARKETPLACE",
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
            }
            
            Divider(
                color = Color.Green,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Category filter
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
            
            // Shop items list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Filter items by category if a filter is applied
                val shopItems = EquipmentSystem.defaultEquipment().filter { 
                    // Filter out items already in inventory
                    !equipmentState.inventory.containsKey(it.key)
                }.values.let { items ->
                    if (currentCategoryFilter != null) {
                        items.filter { it.category == currentCategoryFilter }
                    } else {
                        items
                    }
                }
                
                if (shopItems.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No items available in this category.",
                                color = Color.Gray,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                } else {
                    items(shopItems.toList()) { equipment ->
                        ShopItemCard(
                            equipment = equipment,
                            playerCredits = terminalState.credits,
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
        
        // Equipment details dialog
        if (showEquipmentDetails && selectedEquipment != null) {
            ShopItemDetailsDialog(
                equipment = selectedEquipment!!,
                playerCredits = terminalState.credits,
                onDismiss = { showEquipmentDetails = false },
                onBuy = {
                    viewModel.buyEquipment(listOf(selectedEquipment!!.id))
                    showEquipmentDetails = false
                }
            )
        }
    }
}

@Composable
fun ShopItemCard(
    equipment: Equipment,
    playerCredits: Int,
    onClick: () -> Unit
) {
    val isAffordable = playerCredits >= equipment.basePrice
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
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Item icon
            Text(
                text = "🔧", // Default emoji for equipment
                fontSize = 24.sp,
                modifier = Modifier.padding(end = 16.dp)
            )
            
            // Item details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = equipment.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = equipment.category.name,
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = equipment.rarity.name,
                        color = borderColor,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    
                    if (equipment.isConsumable) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CONSUMABLE",
                            color = Color.Cyan,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
                
                Text(
                    text = equipment.description,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            // Cost
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Paid,
                        contentDescription = "Credits",
                        tint = Color.Green,
                        modifier = Modifier.size(16.dp)
                    )
                    
                    Text(
                        text = equipment.basePrice.toString(),
                        color = Color.Green,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                
                // Buy button
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAffordable) Color.Green else Color.Gray,
                        contentColor = if (isAffordable) Color.Black else Color.DarkGray
                    ),
                    enabled = isAffordable,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = "BUY",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ShopItemDetailsDialog(
    equipment: Equipment,
    playerCredits: Int,
    onDismiss: () -> Unit,
    onBuy: () -> Unit
) {
    val isAffordable = playerCredits >= equipment.basePrice
    
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
                            
                            if (equipment.isConsumable) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "CONSUMABLE",
                                    color = Color.Cyan,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
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
                            imageVector = Icons.Default.Info,
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
                
                // Cost
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PRICE:",
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Icon(
                        imageVector = Icons.Default.Paid,
                        contentDescription = "Credits",
                        tint = Color.Green,
                        modifier = Modifier.size(16.dp)
                    )
                    
                    Text(
                        text = equipment.basePrice.toString(),
                        color = if (isAffordable) Color.Green else Color.Red,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                if (!isAffordable) {
                    Text(
                        text = "You need ${equipment.basePrice - playerCredits} more credits",
                        color = Color.Red,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 4.dp)
                    )
                }
                
                // Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
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
                    
                    Button(
                        onClick = onBuy,
                        enabled = isAffordable,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Green,
                            contentColor = Color.Black,
                            disabledContainerColor = Color.DarkGray,
                            disabledContentColor = Color.Gray
                        )
                    ) {
                        Text(
                            text = "BUY",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
} 