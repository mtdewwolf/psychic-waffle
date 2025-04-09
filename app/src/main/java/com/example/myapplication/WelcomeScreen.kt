package com.example.myapplication

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit
) {
    var currentPage by remember { mutableStateOf(0) }
    val totalPages = 4
    val scrollState = rememberScrollState()
    
    LaunchedEffect(currentPage) {
        scrollState.animateScrollTo(0)
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ASCII Art Logo
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color.Green, fontWeight = FontWeight.Bold)) {
                            append("""
                            
                              _   _            _     ___  ____  
                             | | | | __ _  ___| | __/ _ \/ ___| 
                             | |_| |/ _` |/ __| |/ / | | \___ \ 
                             |  _  | (_| | (__|   <| |_| |___) |
                             |_| |_|\__,_|\___|_|\_\\___/|____/ 
                             
                            """.trimIndent())
                        }
                    },
                    fontFamily = FontFamily.Monospace,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 20.dp, bottom = 30.dp)
                )
                
                when (currentPage) {
                    0 -> WelcomePage()
                    1 -> TutorialPage1()
                    2 -> TutorialPage2()
                    3 -> TutorialPage3()
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Navigation buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = { 
                            if (currentPage > 0) currentPage-- 
                        },
                        enabled = currentPage > 0,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.Green
                        )
                    ) {
                        Text("Previous")
                    }
                    
                    Text(
                        text = "${currentPage + 1} / $totalPages",
                        color = Color.Green,
                        fontFamily = FontFamily.Monospace
                    )
                    
                    TextButton(
                        onClick = { 
                            if (currentPage < totalPages - 1) {
                                currentPage++
                            } else {
                                onGetStarted()
                            }
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.Green
                        )
                    ) {
                        Text(if (currentPage < totalPages - 1) "Next" else "Start Hacking")
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomePage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to HackOS",
            color = Color.Green,
            fontFamily = FontFamily.Monospace,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0A2A12)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "You are a hacker in a world where information is the ultimate currency. Your goal is to breach security systems, steal valuable data, and become the most notorious hacker in the network.",
                    color = Color.Green,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = "HackOS is a terminal-based hacking simulator that puts you in the shoes of a hacker. Complete missions, improve your skills, and build your reputation in the digital underground.",
                    color = Color.Green,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )
            }
        }
    }
}

@Composable
fun TutorialPage1() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Terminal Basics",
            color = Color.Green,
            fontFamily = FontFamily.Monospace,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0A2A12)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "HackOS uses a command-line interface. Here are the basic commands:",
                    color = Color.Green,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                CommandTutorial(command = "help", description = "Shows all available commands")
                CommandTutorial(command = "ls", description = "Lists files in current directory")
                CommandTutorial(command = "cd <dir>", description = "Change directory")
                CommandTutorial(command = "cat <file>", description = "Read file contents")
                CommandTutorial(command = "clear", description = "Clear terminal screen")
                CommandTutorial(command = "pwd", description = "Show current directory")
                CommandTutorial(command = "whoami", description = "Show current user")
            }
        }
    }
}

@Composable
fun TutorialPage2() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Hacking Commands",
            color = Color.Green,
            fontFamily = FontFamily.Monospace,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0A2A12)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "These commands will help you hack into target systems:",
                    color = Color.Green,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                CommandTutorial(command = "scan", description = "Scan network for target systems")
                CommandTutorial(command = "ssh <ip>", description = "Connect to a remote server")
                CommandTutorial(command = "crack <file>", description = "Attempt to crack passwords")
                CommandTutorial(command = "sudo", description = "Escalate to root privileges")
                CommandTutorial(command = "exit", description = "Disconnect from remote system")
            }
        }
    }
}

@Composable
fun TutorialPage3() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Quest System",
            color = Color.Green,
            fontFamily = FontFamily.Monospace,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0A2A12)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Complete missions to earn XP and increase your hacking skills:",
                    color = Color.Green,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                CommandTutorial(command = "quests", description = "View available quests")
                CommandTutorial(command = "quest", description = "View active quest details")
                CommandTutorial(command = "accept <id>", description = "Accept a quest")
                CommandTutorial(command = "stats", description = "View your hacker stats")
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Your first quest will start automatically. Click on the quest banner at the top of the terminal to expand quest details and see your current objectives.",
                    color = Color.Yellow,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )
            }
        }
    }
}

@Composable
fun CommandTutorial(command: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = command,
            color = Color.Cyan,
            fontFamily = FontFamily.Monospace,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(120.dp)
        )
        
        Text(
            text = description,
            color = Color.Green,
            fontFamily = FontFamily.Monospace,
            fontSize = 16.sp
        )
    }
} 