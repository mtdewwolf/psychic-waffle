package com.example.myapplication

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AchievementsScreen(
    viewModel: TerminalViewModel = viewModel(),
    onBack: () -> Unit
) {
    val questState by viewModel.questState.collectAsState()
    val achievements = remember(questState) { viewModel.questSystem.getAllAchievements() }
    
    var selectedAchievement by remember { mutableStateOf<Achievement?>(null) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Column {
            // Header with back button
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
                    text = "ACHIEVEMENTS",
                    color = Color.Green,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            
            // Progress overview
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF113311)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val unlockedCount = achievements.count { it.isUnlocked }
                    val totalCount = achievements.size
                    
                    Text(
                        text = "Progress: $unlockedCount/$totalCount",
                        color = Color.Green,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LinearProgressIndicator(
                        progress = unlockedCount.toFloat() / totalCount,
                        color = Color.Green,
                        trackColor = Color(0xFF002200),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                    )
                }
            }
            
            // Achievements grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(achievements) { achievement ->
                    AchievementCard(
                        achievement = achievement,
                        onClick = { selectedAchievement = achievement }
                    )
                }
            }
        }
        
        // Achievement detail popup
        AnimatedVisibility(
            visible = selectedAchievement != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            selectedAchievement?.let { achievement ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x99000000))
                        .clickable { selectedAchievement = null },
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .padding(32.dp)
                            .fillMaxWidth()
                            .clickable { /* Prevent clicks from propagating */ },
                        colors = CardDefaults.cardColors(
                            containerColor = if (achievement.isUnlocked) Color(0xFF224422) else Color(0xFF222222)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = achievement.iconEmoji,
                                fontSize = 48.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            
                            Text(
                                text = achievement.title,
                                color = if (achievement.isUnlocked) Color.Green else Color.Gray,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            Text(
                                text = achievement.description,
                                color = if (achievement.isUnlocked) Color.White else Color.Gray,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            
                            if (achievement.isUnlocked) {
                                Text(
                                    text = "UNLOCKED",
                                    color = Color.Green,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Text(
                                    text = "LOCKED",
                                    color = Color.Gray,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Button(
                                onClick = { selectedAchievement = null },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF113311)
                                ),
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                Text(
                                    text = "Close",
                                    color = Color.Green,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementCard(
    achievement: Achievement,
    onClick: () -> Unit
) {
    val backgroundColor = if (achievement.isUnlocked) Color(0xFF113311) else Color(0xFF111111)
    val borderColor = if (achievement.isUnlocked) Color.Green else Color(0xFF333333)
    val textColor = if (achievement.isUnlocked) Color.Green else Color.Gray
    
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .border(2.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = achievement.iconEmoji,
                fontSize = 32.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = achievement.title,
                color = textColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
} 