package com.example.myapplication

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.CheckCircle
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
import androidx.compose.ui.window.Dialog
import com.example.myapplication.ui.theme.Orange

@Composable
fun MissionsScreen(
    viewModel: TerminalViewModel,
    onBack: () -> Unit
) {
    val missionState by viewModel.missionState.collectAsState()
    var showMissionDetails by remember { mutableStateOf<Mission?>(null) }
    
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
                    text = "MISSION BOARD",
                    color = Color.Green,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                // Refresh button
                IconButton(
                    onClick = { 
                        viewModel.generateNewMission() 
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Generate New Mission",
                        tint = Color.Green
                    )
                }
            }
            
            Divider(
                color = Color.Green,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Active mission section
            if (missionState.activeMission != null) {
                MissionCard(
                    mission = missionState.activeMission!!,
                    isActive = true,
                    onClick = {
                        showMissionDetails = missionState.activeMission
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Available missions section
            Text(
                text = "AVAILABLE MISSIONS",
                color = Color.Green,
                fontFamily = FontFamily.Monospace,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            if (missionState.availableMissions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No available missions. Check back later or generate a new one.",
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
                    items(missionState.availableMissions) { mission ->
                        MissionCard(
                            mission = mission,
                            isActive = false,
                            onClick = {
                                showMissionDetails = mission
                            }
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            
            Divider(
                color = Color.Green,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            
            // Completed missions counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Completed Missions: ${missionState.completedMissions.size}",
                    color = Color.Green,
                    fontFamily = FontFamily.Monospace
                )
                
                Text(
                    text = "Failed Missions: ${missionState.failedMissions.size}",
                    color = Color.Red,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
        
        // Mission details dialog
        if (showMissionDetails != null) {
            MissionDetailsDialog(
                mission = showMissionDetails!!,
                canStart = showMissionDetails!!.status == MissionStatus.AVAILABLE && missionState.activeMission == null,
                onDismiss = { showMissionDetails = null },
                onStartMission = {
                    viewModel.startMission(showMissionDetails!!.id)
                    showMissionDetails = null
                }
            )
        }
    }
}

@Composable
fun MissionCard(
    mission: Mission,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isActive -> Color(0xFF003300)
        mission.status == MissionStatus.AVAILABLE -> Color(0xFF1A1A2E)
        else -> Color(0xFF330000)
    }
    
    val borderColor = when {
        isActive -> Color.Green
        mission.status == MissionStatus.AVAILABLE -> Color.Cyan
        else -> Color.Red
    }
    
    val statusText = when {
        isActive -> "ACTIVE"
        mission.status == MissionStatus.AVAILABLE -> "AVAILABLE"
        mission.status == MissionStatus.LOCKED -> "LOCKED"
        mission.status == MissionStatus.IN_PROGRESS -> "IN PROGRESS"
        mission.status == MissionStatus.COMPLETED -> "COMPLETED"
        mission.status == MissionStatus.FAILED -> "FAILED"
        else -> "UNAVAILABLE"
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
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = mission.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace
                )
                
                Text(
                    text = statusText,
                    color = borderColor,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Difficulty and type
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Map DifficultyLevel to MissionDifficulty for display
                val missionDifficulty = when(mission.difficulty) {
                    DifficultyLevel.BEGINNER -> MissionDifficulty.BEGINNER
                    DifficultyLevel.EASY -> MissionDifficulty.EASY
                    DifficultyLevel.MEDIUM -> MissionDifficulty.MEDIUM
                    DifficultyLevel.HARD -> MissionDifficulty.HARD
                    DifficultyLevel.EXPERT -> MissionDifficulty.EXPERT
                    else -> MissionDifficulty.MEDIUM
                }
                
                val difficultyColor = when(missionDifficulty) {
                    MissionDifficulty.BEGINNER -> Color.Green
                    MissionDifficulty.EASY -> Color.Cyan
                    MissionDifficulty.MEDIUM -> Color.Yellow
                    MissionDifficulty.HARD -> Orange
                    MissionDifficulty.EXPERT -> Color.Red
                }
                
                Text(
                    text = "Difficulty: ",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
                
                Text(
                    text = missionDifficulty.name,
                    color = difficultyColor,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = "Type: ${mission.type.name}",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Description
            Text(
                text = mission.description,
                color = Color.White,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Requirements section
            val levelRequirement = mission.requirements.find { it.type == RequirementType.PLAYER_LEVEL }
            val equipmentRequirements = mission.requirements.filter { it.type == RequirementType.EQUIPMENT }
            val skillRequirements = mission.requirements.filter { it.type == RequirementType.SKILL }
            
            if (levelRequirement != null || equipmentRequirements.isNotEmpty() || skillRequirements.isNotEmpty()) {
                Text(
                    text = "Requirements:",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                if (levelRequirement != null) {
                    Text(
                        text = "• Level ${levelRequirement.amount}+",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                
                if (equipmentRequirements.isNotEmpty()) {
                    Text(
                        text = "• Equipment: ${equipmentRequirements.joinToString(", ") { it.value }}",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                
                if (skillRequirements.isNotEmpty()) {
                    Text(
                        text = "• Skills: ${skillRequirements.joinToString(", ") { it.value }}",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
            
            // Show reward info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                // Find XP and credit rewards
                val xpReward = mission.rewards.find { it.type == RewardType.XP }?.amount ?: 0
                val creditReward = mission.rewards.find { it.type == RewardType.MONEY }?.amount ?: 0
                
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color.Yellow,
                    modifier = Modifier.size(16.dp)
                )
                
                Text(
                    text = "$xpReward XP",
                    color = Color.Yellow,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(start = 4.dp, end = 8.dp)
                )
                
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color.Green,
                    modifier = Modifier.size(16.dp)
                )
                
                Text(
                    text = "$creditReward CR",
                    color = Color.Green,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            
            // Progress bar for active missions
            if (isActive) {
                val completedObjectives = mission.objectives.count { it.status == MissionStatus.COMPLETED }
                val progress = if (mission.objectives.isNotEmpty()) {
                    completedObjectives.toFloat() / mission.objectives.size
                } else {
                    0f
                }
                val animatedProgress by animateFloatAsState(targetValue = progress)
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Progress:",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        
                        Text(
                            text = "$completedObjectives/${mission.objectives.size} objectives",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    LinearProgressIndicator(
                        progress = animatedProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = Color.Green,
                        trackColor = Color.DarkGray
                    )
                }
            }
        }
    }
}

@Composable
fun MissionDetailsDialog(
    mission: Mission,
    canStart: Boolean,
    onDismiss: () -> Unit,
    onStartMission: () -> Unit
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
                Text(
                    text = mission.title,
                    color = Color.Green,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Description
                Text(
                    text = mission.description,
                    color = Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Details
                // Map DifficultyLevel to MissionDifficulty for display
                val missionDifficulty = when(mission.difficulty) {
                    DifficultyLevel.BEGINNER -> MissionDifficulty.BEGINNER
                    DifficultyLevel.EASY -> MissionDifficulty.EASY
                    DifficultyLevel.MEDIUM -> MissionDifficulty.MEDIUM
                    DifficultyLevel.HARD -> MissionDifficulty.HARD
                    DifficultyLevel.EXPERT -> MissionDifficulty.EXPERT
                    else -> MissionDifficulty.MEDIUM
                }
                
                val difficultyColor = when(missionDifficulty) {
                    MissionDifficulty.BEGINNER -> Color.Green
                    MissionDifficulty.EASY -> Color.Cyan
                    MissionDifficulty.MEDIUM -> Color.Yellow
                    MissionDifficulty.HARD -> Orange
                    MissionDifficulty.EXPERT -> Color.Red
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Difficulty: ",
                        color = Color.Gray,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    )
                    
                    Text(
                        text = missionDifficulty.name,
                        color = difficultyColor,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Type: ${mission.type.name}",
                    color = Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                if (mission.location != null) {
                    Text(
                        text = "Location: ${mission.location.name}",
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    )
                }
                
                // Requirements
                if (mission.requirements.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "REQUIREMENTS:",
                        color = Color.Yellow,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    mission.requirements.forEach { requirement ->
                        val requirementText = when (requirement.type) {
                            RequirementType.PLAYER_LEVEL -> "• Level ${requirement.amount}+"
                            RequirementType.SKILL -> "• Skill: ${requirement.value}"
                            RequirementType.EQUIPMENT -> "• Equipment: ${requirement.value}"
                            RequirementType.PREVIOUS_MISSION -> "• Previous Mission: ${requirement.value}"
                            RequirementType.REPUTATION -> "• Reputation: ${requirement.value} (${requirement.amount}+)"
                            RequirementType.SKILL_CATEGORY -> "• Skill Category: ${requirement.value}"
                            RequirementType.STORY_PROGRESS -> "• Story Progress: ${requirement.amount}%"
                        }
                        
                        Text(
                            text = requirementText,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp
                        )
                    }
                }
                
                // Rewards
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "REWARDS:",
                    color = Color.Green,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                
                mission.rewards.forEach { reward ->
                    val rewardText = when (reward.type) {
                        RewardType.XP -> "• ${reward.amount} XP"
                        RewardType.SKILL_POINTS -> "• ${reward.amount} Skill Points"
                        RewardType.MONEY -> "• ${reward.amount} Credits"
                        RewardType.EQUIPMENT -> "• Equipment: ${reward.value}"
                        RewardType.SKILL_UNLOCK -> "• Unlock Skill: ${reward.value}"
                        RewardType.REPUTATION -> "• ${reward.amount} Reputation with ${reward.value}"
                        RewardType.STORY_PROGRESS -> "• Story Progress"
                        RewardType.INTEL -> "• Intel: ${reward.value}"
                        RewardType.NEW_COMMANDS -> "• New Command: ${reward.value}"
                        RewardType.CUSTOM_SCRIPT -> "• Custom Script: ${reward.value}"
                    }
                    
                    Text(
                        text = rewardText,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    )
                }
                
                // Objectives
                if (mission.status == MissionStatus.IN_PROGRESS) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "OBJECTIVES:",
                        color = Color.Cyan,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    mission.objectives.forEachIndexed { index, objective ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            val statusColor = if (objective.status == MissionStatus.COMPLETED) Color.Green else Color.Gray
                            val statusIcon = if (objective.status == MissionStatus.COMPLETED) 
                                Icons.Default.CheckCircle 
                            else 
                                Icons.Filled.RadioButtonUnchecked
                            
                            Icon(
                                imageVector = statusIcon,
                                contentDescription = null,
                                tint = statusColor,
                                modifier = Modifier.size(16.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(
                                text = "${index + 1}. ${objective.description}",
                                color = statusColor,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                
                // Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
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
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = onStartMission,
                        enabled = canStart,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Green,
                            contentColor = Color.Black,
                            disabledContainerColor = Color.DarkGray,
                            disabledContentColor = Color.Gray
                        )
                    ) {
                        Text(
                            text = if (mission.status == MissionStatus.IN_PROGRESS) "RESUME MISSION" else "START MISSION",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
} 