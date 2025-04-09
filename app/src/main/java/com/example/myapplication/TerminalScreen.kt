package com.example.myapplication

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.animateSize
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.myapplication.QuestStatus
import com.example.myapplication.NotificationType

@Composable
fun TerminalScreen(
    viewModel: TerminalViewModel,
    onNavigateToAchievements: () -> Unit,
    onNavigateToSkills: () -> Unit,
    onNavigateToEquipment: () -> Unit,
    onNavigateToMissions: () -> Unit,
    onNavigateToShop: () -> Unit
) {
    val terminalState by viewModel.terminalState.collectAsState()
    val questState by viewModel.questState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val questBannerVisible = remember { mutableStateOf(true) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Terminal Header with Navigation Icons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Player stats
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "LVL ${questState.playerStats.level}",
                        color = Color.Green,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    androidx.compose.material3.LinearProgressIndicator(
                        progress = questState.playerStats.xp.toFloat() / 100f,
                        modifier = Modifier
                            .height(8.dp)
                            .width(80.dp),
                        color = Color.Green,
                        trackColor = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${questState.playerStats.xp}/100 XP",
                        color = Color.Green,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Icon(
                        imageVector = Icons.Default.Paid,
                        contentDescription = "Credits",
                        tint = Color.Green,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${terminalState.credits}",
                        color = Color.Green,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    )
                }
                
                // Navigation icons
                Row {
                    // Skills button
                    IconButton(onClick = onNavigateToSkills) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Skills",
                            tint = Color.Cyan
                        )
                    }
                    
                    // Equipment button
                    IconButton(onClick = onNavigateToEquipment) {
                        Icon(
                            imageVector = Icons.Default.Cable,
                            contentDescription = "Equipment",
                            tint = Color.Yellow
                        )
                    }
                    
                    // Missions button
                    IconButton(onClick = onNavigateToMissions) {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = "Missions",
                            tint = Color.Green
                        )
                    }
                    
                    // Shop button
                    IconButton(onClick = onNavigateToShop) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Shop",
                            tint = Color.LightGray
                        )
                    }
                    
                    // Achievements button
                    IconButton(onClick = onNavigateToAchievements) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Face,
                                contentDescription = "Achievements",
                                tint = Color(0xFFFFD700)
                            )
                            Text(
                                text = "${questState.achievements.count { it.isUnlocked }}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
            
            // Active quest banner (collapsible)
            val activeQuest = viewModel.questSystem.getActiveQuest()
            if (activeQuest != null && questBannerVisible.value) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF1A237E))
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ACTIVE QUEST: ${activeQuest.title}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        
                        IconButton(
                            onClick = { questBannerVisible.value = false },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Hide",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    
                    Text(
                        text = activeQuest.description,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    
                    activeQuest.objectives.forEach { objective ->
                        Row(
                            modifier = Modifier.padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val statusColor = if (objective.status == QuestStatus.COMPLETED) Color.Green else Color.Gray
                            val statusIcon = if (objective.status == QuestStatus.COMPLETED) 
                                Icons.Default.CheckCircle 
                            else 
                                Icons.Outlined.RadioButtonUnchecked
                            
                            Icon(
                                imageVector = statusIcon,
                                contentDescription = null,
                                tint = statusColor,
                                modifier = Modifier.size(16.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(
                                text = objective.description,
                                color = statusColor,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Terminal output area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .border(1.dp, Color.Green, RoundedCornerShape(4.dp))
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF0A0A0A))
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    state = listState
                ) {
                    items(terminalState.output) { line ->
                        Text(
                            text = line,
                            color = Color.Green,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
                
                // Auto-scroll to bottom when new messages appear
                LaunchedEffect(terminalState.output.size) {
                    if (terminalState.output.isNotEmpty()) {
                        listState.animateScrollToItem(terminalState.output.size - 1)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Command input field
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ">",
                    color = Color.Green,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp)
                )
                
                var inputText by remember { mutableStateOf("") }
                
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Green,
                        unfocusedTextColor = Color.Green,
                        focusedContainerColor = Color(0xFF0A0A0A),
                        unfocusedContainerColor = Color(0xFF0A0A0A),
                        focusedIndicatorColor = Color.Green,
                        unfocusedIndicatorColor = Color.Green,
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    placeholder = {
                        Text(
                            "Enter command...",
                            color = Color.Gray,
                            fontFamily = FontFamily.Monospace
                        )
                    },
                    singleLine = true,
                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                        onDone = {
                            if (inputText.isNotEmpty()) {
                                viewModel.addOutput("> $inputText")
                                viewModel.processCommand(inputText)
                                inputText = ""
                            }
                        }
                    )
                )
                
                // Command submit button
                IconButton(
                    onClick = {
                        if (inputText.isNotEmpty()) {
                            viewModel.addOutput("> $inputText")
                            viewModel.processCommand(inputText)
                            inputText = ""
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Submit",
                        tint = Color.Green
                    )
                }
            }
        }
        
        // Show quest notifications
        AnimatedVisibility(
            visible = questState.notifications.isNotEmpty(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        ) {
            questState.notifications.firstOrNull()?.let { notification ->
                val backgroundColor = when (notification.type) {
                    NotificationType.QUEST_COMPLETED -> Color(0xFF388E3C)
                    NotificationType.ACHIEVEMENT_UNLOCKED -> Color(0xFFF57F17)
                    NotificationType.LEVEL_UP -> Color(0xFF1565C0)
                    else -> Color(0xFF303F9F)
                }
                
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable {
                            viewModel.dismissQuestNotification()
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = backgroundColor
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = notification.message,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = notification.iconEmoji,
                            color = Color.White,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        
                        LaunchedEffect(notification) {
                            delay(3000)
                            viewModel.dismissQuestNotification()
                        }
                    }
                }
            }
        }
    }
} 