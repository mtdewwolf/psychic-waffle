package com.example.myapplication

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

fun Modifier.tabIndicatorOffset(
    currentTabPosition: TabPosition
): Modifier = this.then(Modifier.wrapContentSize(Alignment.BottomStart).offset(
    x = currentTabPosition.left,
    y = 0.dp
).width(currentTabPosition.width))

private fun getEffectName(effectType: SkillEffectType): String {
    return when (effectType) {
        SkillEffectType.HACKING_SPEED -> "Hacking Speed"
        SkillEffectType.HACKING_SUCCESS -> "Hacking Success"
        SkillEffectType.STEALTH -> "Stealth"
        SkillEffectType.DEFENSE -> "Defense"
        SkillEffectType.VIRUS_STRENGTH -> "Virus Strength"
        SkillEffectType.SCAN_SPEED -> "Scan Speed"
        SkillEffectType.SCAN_DEPTH -> "Scan Depth"
        SkillEffectType.CREDIT_BONUS -> "Credit Bonus"
        SkillEffectType.XP_BONUS -> "XP Bonus"
        SkillEffectType.UNLOCK_ABILITY -> "New Ability"
        SkillEffectType.CRACK_SPEED -> "Crack Speed"
        SkillEffectType.SCAN_DETAIL -> "Scan Detail"
        SkillEffectType.DETECTION_CHANCE -> "Detection Chance"
        SkillEffectType.FIREWALL_BYPASS -> "Firewall Bypass"
        SkillEffectType.SERVICE_EXPLOIT -> "Service Exploit"
        SkillEffectType.TRACE_REDUCTION -> "Trace Reduction"
        SkillEffectType.DATA_EXFIL_SPEED -> "Data Exfil Speed"
        SkillEffectType.ENCRYPTION_BOOST -> "Encryption Boost"
        SkillEffectType.VULN_DISCOVERY -> "Vulnerability Discovery"
        SkillEffectType.PERSISTENCE -> "Persistence"
        SkillEffectType.SOCIAL_SUCCESS -> "Social Success"
        SkillEffectType.SCRIPT_EFFICIENCY -> "Script Efficiency"
        SkillEffectType.IDS_EVASION -> "IDS Evasion"
    }
}

@Composable
fun SkillTreeScreen(
    viewModel: TerminalViewModel,
    onBack: () -> Unit
) {
    val questState by viewModel.questState.collectAsState()
    val skillSystem = remember { SkillSystem() }
    val skillState by skillSystem.state.collectAsState()
    
    var selectedCategory by remember { mutableStateOf(SkillCategory.NETWORK) }
    var selectedSkill by remember { mutableStateOf<PlayerSkill?>(null) }
    var showUnlockDialog by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with back button and title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Green
                    )
                }
                
                Text(
                    text = "SKILL TREE",
                    color = Color.Green,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                // Skill points display
                Card(
                    modifier = Modifier
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF001100)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "SKILL POINTS",
                            color = Color.Green,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                        
                        Text(
                            text = "${skillState.availableSkillPoints}",
                            color = Color.Green,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // Category tabs
            TabRow(
                selectedTabIndex = selectedCategory.ordinal,
                containerColor = Color(0xFF001100),
                contentColor = Color.Green,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedCategory.ordinal]),
                        height = 2.dp,
                        color = Color.Green
                    )
                }
            ) {
                // Only show unlocked categories
                skillState.unlockedCategories.forEach { category ->
                    Tab(
                        selected = category == selectedCategory,
                        onClick = { selectedCategory = category },
                        text = {
                            Text(
                                text = category.name,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                        },
                        selectedContentColor = Color.Green,
                        unselectedContentColor = Color.Green.copy(alpha = 0.5f)
                    )
                }
                
                // Show locked categories with a lock icon
                SkillCategory.values().filter { it !in skillState.unlockedCategories }.forEach { category ->
                    Tab(
                        selected = false,
                        onClick = { /* Do nothing, it's locked */ },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = Color.Gray
                            )
                        },
                        text = {
                            Text(
                                text = category.name,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        },
                        enabled = false
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Skills in selected category
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                val categorySkills = skillSystem.getSkillsByCategory(selectedCategory)
                
                items(categorySkills) { playerSkill ->
                    SkillCard(
                        playerSkill = playerSkill,
                        isLocked = !playerSkill.isUnlocked,
                        canUpgrade = skillState.availableSkillPoints > 0 &&
                                playerSkill.isUnlocked &&
                                playerSkill.currentLevel < playerSkill.skill.maxLevel &&
                                questState.playerStats.level >= playerSkill.skill.unlockLevel,
                        onClick = {
                            selectedSkill = playerSkill
                            if (playerSkill.isUnlocked && skillState.availableSkillPoints > 0) {
                                showUnlockDialog = true
                            }
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
        
        // Skill unlock/upgrade dialog
        if (showUnlockDialog && selectedSkill != null) {
            SkillUnlockDialog(
                playerSkill = selectedSkill!!,
                playerLevel = questState.playerStats.level,
                skillPoints = skillState.availableSkillPoints,
                onConfirm = {
                    skillSystem.learnSkill(selectedSkill!!.skill.id, questState.playerStats.level)
                    showUnlockDialog = false
                },
                onDismiss = {
                    showUnlockDialog = false
                }
            )
        }
    }
}

@Composable
fun SkillCard(
    playerSkill: PlayerSkill,
    isLocked: Boolean,
    canUpgrade: Boolean,
    onClick: () -> Unit
) {
    val alpha = animateFloatAsState(targetValue = if (isLocked) 0.5f else 1f, label = "SkillCardAlpha")
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isLocked || canUpgrade, onClick = onClick)
            .alpha(alpha.value),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF001100)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Skill level indicator
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (playerSkill.currentLevel > 0) Color.Green
                        else Color.DarkGray
                    )
                    .border(
                        width = 2.dp,
                        color = when {
                            canUpgrade -> Color.Yellow
                            playerSkill.currentLevel > 0 -> Color.Green
                            else -> Color.Gray
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isLocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color.Gray
                    )
                } else if (playerSkill.currentLevel > 0) {
                    Text(
                        text = "${playerSkill.currentLevel}",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Available",
                        tint = if (canUpgrade) Color.Yellow else Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Skill details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = playerSkill.skill.name,
                    color = when {
                        canUpgrade -> Color.Yellow
                        playerSkill.currentLevel > 0 -> Color.Green
                        isLocked -> Color.Gray
                        else -> Color.White
                    },
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = playerSkill.skill.description,
                    color = Color.Green.copy(alpha = 0.7f),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Skill level progress
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Level progress indicator
                    Row(
                        modifier = Modifier.weight(1f)
                    ) {
                        repeat(playerSkill.skill.maxLevel) { level ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(4.dp)
                                    .padding(end = if (level < playerSkill.skill.maxLevel - 1) 4.dp else 0.dp)
                                    .background(
                                        if (level < playerSkill.currentLevel) Color.Green
                                        else Color.DarkGray
                                    )
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Level display
                    Text(
                        text = "${playerSkill.currentLevel}/${playerSkill.skill.maxLevel}",
                        color = Color.Green,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                }
                
                // Effects
                if (playerSkill.skill.effects.isNotEmpty() && !isLocked) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    playerSkill.skill.effects.forEach { effect ->
                        Text(
                            text = "${getEffectName(effect.type)}: +${(effect.value * 100).toInt()}%",
                            color = Color.Cyan,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }
                }
                
                // Prerequisites
                if (isLocked && playerSkill.skill.requiredSkills.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Prerequisites: ${playerSkill.skill.requiredSkills.joinToString(", ")}",
                        color = Color.Red,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                }
                
                // Level requirement
                if (playerSkill.skill.unlockLevel > 1) {
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Required Level: ${playerSkill.skill.unlockLevel}",
                        color = if (isLocked) Color.Red else Color.Green,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                }
            }
            
            if (canUpgrade) {
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Green,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = if (playerSkill.currentLevel == 0) "Unlock" else "Upgrade",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun SkillUnlockDialog(
    playerSkill: PlayerSkill,
    playerLevel: Int,
    skillPoints: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val canUnlock = skillPoints > 0 && playerLevel >= playerSkill.skill.unlockLevel
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF001100)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (playerSkill.currentLevel == 0) "UNLOCK SKILL" else "UPGRADE SKILL",
                    color = Color.Green,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = playerSkill.skill.name,
                    color = Color.Green,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = playerSkill.skill.description,
                    color = Color.Green.copy(alpha = 0.7f),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Current level and next level comparison
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Current level
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "CURRENT",
                            color = Color.Gray,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                        
                        Text(
                            text = "Level ${playerSkill.currentLevel}",
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        // Current effects
                        if (playerSkill.currentLevel > 0) {
                            playerSkill.skill.effects.forEach { effect ->
                                val currentValue = effect.value * playerSkill.currentLevel / playerSkill.skill.maxLevel
                                Text(
                                    text = "+${(currentValue * 100).toInt()}%",
                                    color = Color.Cyan,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp
                                )
                            }
                        } else {
                            Text(
                                text = "None",
                                color = Color.Gray,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                        }
                    }
                    
                    // Arrow
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "To",
                        tint = Color.Green,
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(270f)
                            .align(Alignment.CenterVertically)
                    )
                    
                    // Next level
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "NEXT",
                            color = Color.Green,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                        
                        Text(
                            text = "Level ${playerSkill.currentLevel + 1}",
                            color = Color.Green,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        // Next level effects
                        playerSkill.skill.effects.forEach { effect ->
                            val nextValue = effect.value * (playerSkill.currentLevel + 1) / playerSkill.skill.maxLevel
                            Text(
                                text = "+${(nextValue * 100).toInt()}%",
                                color = Color.Yellow,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Cost
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "COST: ",
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    )
                    
                    Text(
                        text = "1 Skill Point",
                        color = if (skillPoints >= 1) Color.Green else Color.Red,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = " (${skillPoints} available)",
                        color = Color.Gray,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                }
                
                if (playerLevel < playerSkill.skill.unlockLevel) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Required Level: ${playerSkill.skill.unlockLevel}",
                        color = Color.Red,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.DarkGray,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "CANCEL",
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canUnlock) Color.Green else Color.DarkGray,
                            contentColor = if (canUnlock) Color.Black else Color.Gray
                        ),
                        enabled = canUnlock,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (playerSkill.currentLevel == 0) "UNLOCK" else "UPGRADE",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
} 