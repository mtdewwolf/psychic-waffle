package com.example.myapplication

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class TerminalState(
    val terminalOutput: String = getWelcomeMessage(),
    val currentCommand: String = "",
    val currentDirectory: String = "/home/user",
    val isConnectedToTarget: Boolean = false,
    val currentTargetIp: String = "",
    val discoveredIps: List<String> = listOf(),
    val hasRootAccess: Boolean = false,
    val localFileSystem: Map<String, FileSystemNode> = defaultLocalFileSystem(),
    val remoteFileSystems: Map<String, Map<String, FileSystemNode>> = defaultRemoteFileSystems(),
    val output: List<String> = listOf(),
    val currentCommandHistory: List<String> = listOf(),
    val historyIndex: Int = -1,
    val workingDirectory: String = "/home/user",
    val fileSystem: Map<String, Any> = defaultFileSystem(),
    val credits: Int = 500
)

sealed class FileSystemNode {
    data class Directory(val name: String, val children: MutableMap<String, FileSystemNode> = mutableMapOf()) : FileSystemNode()
    data class File(
        val name: String, 
        val content: String = "", 
        val isReadable: Boolean = true,
        val isWritable: Boolean = false,
        val isExecutable: Boolean = false
    ) : FileSystemNode()
}

fun getWelcomeMessage(): String {
    return """
    ██╗  ██╗ █████╗  ██████╗██╗  ██╗ ██████╗ ███████╗
    ██║  ██║██╔══██╗██╔════╝██║ ██╔╝██╔═══██╗██╔════╝
    ███████║███████║██║     █████╔╝ ██║   ██║███████╗
    ██╔══██║██╔══██║██║     ██╔═██╗ ██║   ██║╚════██║
    ██║  ██║██║  ██║╚██████╗██║  ██╗╚██████╔╝███████║
    ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝╚═╝  ╚═╝ ╚═════╝ ╚══════╝
                                           
    Terminal v1.0 - A Hacking Simulator
    Type 'help' for a list of commands.
    
    """.trimIndent()
}

fun defaultLocalFileSystem(): Map<String, FileSystemNode> {
    // Create a simple file system structure
    val root = FileSystemNode.Directory("/")
    val home = FileSystemNode.Directory("home")
    val user = FileSystemNode.Directory("user")
    
    val documents = FileSystemNode.Directory("documents")
    documents.children["readme.txt"] = FileSystemNode.File(
        "readme.txt", 
        "Welcome to HackOS!\nThis is your personal hacking system.\n\nUse the 'scan' command to find potential targets."
    )
    documents.children["hacking_guide.txt"] = FileSystemNode.File(
        "hacking_guide.txt",
        """
        BASIC HACKING GUIDE
        ------------------
        1. Use 'scan' or 'nmap' to discover potential targets
        2. Use 'ssh <ip>' to connect to a target
        3. Use 'crack /etc/shadow' to find login credentials
        4. After gaining access, try to escalate privileges to root
        5. Look for sensitive data in the system
        6. Use 'ps' to view running processes
        7. Use 'netstat' to check network connections
        8. 'ifconfig' shows network interfaces
        
        Remember: Different systems have different vulnerabilities!
        """.trimIndent()
    )
    
    val bin = FileSystemNode.Directory("bin")
    bin.children["scan"] = FileSystemNode.File(
        "scan", 
        "#!/bin/bash\n# Scan network for targets", 
        isExecutable = true
    )
    bin.children["nmap"] = FileSystemNode.File(
        "nmap",
        "#!/bin/bash\n# Advanced network scanning tool",
        isExecutable = true
    )
    bin.children["ssh"] = FileSystemNode.File(
        "ssh", 
        "#!/bin/bash\n# Connect to remote servers", 
        isExecutable = true
    )
    bin.children["crack"] = FileSystemNode.File(
        "crack", 
        "#!/bin/bash\n# Password cracking tool", 
        isExecutable = true
    )
    bin.children["netstat"] = FileSystemNode.File(
        "netstat",
        "#!/bin/bash\n# Network connection viewer",
        isExecutable = true
    )
    bin.children["ifconfig"] = FileSystemNode.File(
        "ifconfig",
        "#!/bin/bash\n# Network interface configuration",
        isExecutable = true
    )
    bin.children["ps"] = FileSystemNode.File(
        "ps",
        "#!/bin/bash\n# Process status viewer",
        isExecutable = true
    )
    bin.children["grep"] = FileSystemNode.File(
        "grep",
        "#!/bin/bash\n# Search text files for patterns",
        isExecutable = true
    )
    bin.children["wget"] = FileSystemNode.File(
        "wget",
        "#!/bin/bash\n# Download files from web servers",
        isExecutable = true
    )
    
    // Add a tools directory for hacking tools
    val tools = FileSystemNode.Directory("tools")
    tools.children["bruteforce.py"] = FileSystemNode.File(
        "bruteforce.py",
        "#!/usr/bin/env python3\n# Custom bruteforce tool for password attacks",
        isExecutable = true
    )
    tools.children["portscanner.py"] = FileSystemNode.File(
        "portscanner.py",
        "#!/usr/bin/env python3\n# Custom port scanner",
        isExecutable = true
    )
    tools.children["exploit_finder.py"] = FileSystemNode.File(
        "exploit_finder.py",
        "#!/usr/bin/env python3\n# Search for exploits in target systems",
        isExecutable = true
    )
    
    user.children["documents"] = documents
    user.children["bin"] = bin
    user.children["tools"] = tools
    
    home.children["user"] = user
    root.children["home"] = home
    
    // Add more system directories
    val etc = FileSystemNode.Directory("etc")
    etc.children["hosts"] = FileSystemNode.File(
        "hosts",
        "127.0.0.1 localhost\n127.0.1.1 hackos"
    )
    etc.children["passwd"] = FileSystemNode.File(
        "passwd",
        "root:x:0:0:root:/root:/bin/bash\nuser:x:1000:1000:HackOS User,,,:/home/user:/bin/bash"
    )
    
    val varDir = FileSystemNode.Directory("var")
    varDir.children["log"] = FileSystemNode.Directory("log")
    
    root.children["etc"] = etc
    root.children["var"] = varDir
    
    return root.children
}

fun defaultRemoteFileSystems(): Map<String, Map<String, FileSystemNode>> {
    val remoteSystems = mutableMapOf<String, Map<String, FileSystemNode>>()
    
    // Web Server (192.168.1.105)
    val webServerRoot = FileSystemNode.Directory("/")
    val webServerEtc = FileSystemNode.Directory("etc")
    val webServerHome = FileSystemNode.Directory("home")
    val webServerVar = FileSystemNode.Directory("var")
    
    webServerEtc.children["shadow"] = FileSystemNode.File(
        "shadow",
        "admin:\$6\$xyz:18000:0:99999:7:::",
        isReadable = false
    )
    
    webServerEtc.children["passwd"] = FileSystemNode.File(
        "passwd",
        "root:x:0:0:root:/root:/bin/bash\nadmin:x:1000:1000:admin:/home/admin:/bin/bash"
    )
    
    val webServerAdmin = FileSystemNode.Directory("admin")
    webServerAdmin.children["user.txt"] = FileSystemNode.File(
        "user.txt",
        "Congratulations! You've found the user flag: FLAG{W3B_S3RV3R_0WN3D}"
    )
    
    webServerHome.children["admin"] = webServerAdmin
    
    val webServerWww = FileSystemNode.Directory("www")
    val webServerHtml = FileSystemNode.Directory("html")
    
    webServerHtml.children["index.html"] = FileSystemNode.File(
        "index.html",
        "<!DOCTYPE html>\n<html>\n<head>\n<title>Company Website</title>\n</head>\n<body>\n<h1>Welcome to Our Company</h1>\n</body>\n</html>"
    )
    
    webServerHtml.children["config.php"] = FileSystemNode.File(
        "config.php",
        "<?php\n// Database configuration\n\$db_host = 'localhost';\n\$db_user = 'dbadmin';\n\$db_pass = 'insecure123';\n\$db_name = 'company_db';\n?>"
    )
    
    webServerWww.children["html"] = webServerHtml
    webServerVar.children["www"] = webServerWww
    
    webServerRoot.children["etc"] = webServerEtc
    webServerRoot.children["home"] = webServerHome
    webServerRoot.children["var"] = webServerVar
    
    // Database Server (192.168.1.240)
    val dbServerRoot = FileSystemNode.Directory("/")
    val dbServerEtc = FileSystemNode.Directory("etc")
    val dbServerHome = FileSystemNode.Directory("home")
    val dbServerVar = FileSystemNode.Directory("var")
    
    dbServerEtc.children["shadow"] = FileSystemNode.File(
        "shadow",
        "admin:\$6\$abc:18000:0:99999:7:::",
        isReadable = false
    )
    
    dbServerEtc.children["passwd"] = FileSystemNode.File(
        "passwd",
        "root:x:0:0:root:/root:/bin/bash\nadmin:x:1000:1000:admin:/home/admin:/bin/bash"
    )
    
    val dbServerAdmin = FileSystemNode.Directory("admin")
    val dbServerLib = FileSystemNode.Directory("lib")
    val dbServerMySql = FileSystemNode.Directory("mysql")
    
    dbServerAdmin.children["notes.txt"] = FileSystemNode.File(
        "notes.txt",
        "Need to change database password. Using 'dbserver2023' for now."
    )
    
    dbServerHome.children["admin"] = dbServerAdmin
    
    dbServerMySql.children["database.sql"] = FileSystemNode.File(
        "database.sql",
        "-- Database dump\n-- Contains sensitive customer information\n\nCREATE TABLE customers (\n  id INT PRIMARY KEY,\n  name VARCHAR(100),\n  email VARCHAR(100),\n  credit_card VARCHAR(16)\n);\n\nINSERT INTO customers VALUES (1, 'John Smith', 'john@example.com', '4532123456789012');"
    )
    
    dbServerLib.children["mysql"] = dbServerMySql
    dbServerVar.children["lib"] = dbServerLib
    
    dbServerRoot.children["etc"] = dbServerEtc
    dbServerRoot.children["home"] = dbServerHome
    dbServerRoot.children["var"] = dbServerVar
    
    // Personal Computer (192.168.1.12)
    val pcRoot = FileSystemNode.Directory("/")
    val pcEtc = FileSystemNode.Directory("etc")
    val pcHome = FileSystemNode.Directory("home")
    
    pcEtc.children["shadow"] = FileSystemNode.File(
        "shadow",
        "user:\$6\$def:18000:0:99999:7:::",
        isReadable = false
    )
    
    pcEtc.children["passwd"] = FileSystemNode.File(
        "passwd",
        "root:x:0:0:root:/root:/bin/bash\nuser:x:1000:1000:Regular User:/home/user:/bin/bash"
    )
    
    val pcUser = FileSystemNode.Directory("user")
    pcUser.children["documents"] = FileSystemNode.Directory("documents")
    
    pcUser.children["documents"].apply {
        if (this is FileSystemNode.Directory) {
            children["passwords.txt"] = FileSystemNode.File(
                "passwords.txt",
                "Email: user@gmail.com - Password: ilovecats123\nBank: 4-digit PIN = 1234"
            )
        }
    }
    
    pcHome.children["user"] = pcUser
    
    pcRoot.children["etc"] = pcEtc
    pcRoot.children["home"] = pcHome
    
    // Add all remote systems to the map
    remoteSystems["192.168.1.105"] = webServerRoot.children
    remoteSystems["192.168.1.240"] = dbServerRoot.children
    remoteSystems["192.168.1.12"] = pcRoot.children
    
    return remoteSystems
}

class TerminalViewModel : ViewModel() {
    // Add terminalState property
    private val _terminalState = MutableStateFlow(TerminalState())
    val terminalState: StateFlow<TerminalState> = _terminalState.asStateFlow()
    
    // Create quest system instance and make it public
    val questSystem = QuestSystem()
    private val _questState = MutableStateFlow(questSystem.state.value)
    val questState: StateFlow<QuestState> = _questState.asStateFlow()
    
    // Skill system
    private val _skillSystem = SkillSystem()
    private val _skillState = MutableStateFlow(_skillSystem.state.value)
    val skillState: StateFlow<SkillTreeState> = _skillState.asStateFlow()
    
    // Equipment system
    private val _equipmentSystem = EquipmentSystem()
    private val _equipmentState = MutableStateFlow(_equipmentSystem.state.value)
    val equipmentState: StateFlow<EquipmentSystemState> = _equipmentState.asStateFlow()
    
    // Mission system
    private val _missionSystem = MissionSystem()
    private val _missionState = MutableStateFlow(_missionSystem.state.value)
    val missionState: StateFlow<MissionSystemState> = _missionState.asStateFlow()
    
    // Navigation callback
    private val _navigateToAchievements = MutableStateFlow(false)
    val navigateToAchievements: StateFlow<Boolean> = _navigateToAchievements.asStateFlow()
    
    init {
        // Auto-start the tutorial quest
        questSystem.startQuest("tutorial")
        updateQuestState()
    }

    // Add addOutput method
    fun addOutput(message: String) {
        _terminalState.update { 
            it.copy(output = it.output + message)
        }
    }
    
    fun dismissQuestNotification() {
        questSystem.dismissNotification()
        updateQuestState()
    }
    
    fun onAchievementsNavigated() {
        _navigateToAchievements.value = false
    }
    
    private fun processCommand(command: String, state: TerminalState): String {
        val parts = command.split(" ")
        val cmd = parts[0].lowercase()
        val args = if (parts.size > 1) parts.subList(1, parts.size) else listOf()
        
        // Check for mission progress first
        checkMissionProgress(cmd, args)
        
        val result = when (cmd) {
            "help" -> {
                """
                Available commands:
                help - Display this help message
                hint - Get a hint for your current objective
                ls - List files in current directory
                cd - Change directory
                cat - Read file contents
                scan/nmap - Scan for potential targets
                ssh - Connect to a remote server
                crack - Attempt to crack password hashes
                clear - Clear the terminal
                pwd - Print working directory
                whoami - Show current user
                exit - Disconnect from target system
                sudo - Escalate privileges (when connected)
                quests - View available quests
                quest - View active quest details
                accept - Accept a quest
                stats - View your hacker stats
                ifconfig - Display network interfaces
                netstat - Show network connections
                ps - Show running processes
                grep <pattern> <file> - Search for patterns
                wget <url> - Download files from web
                achievements - View your achievements
                skills - Open the skill tree menu
                equipment - Manage your hacking equipment
                missions - View available missions
                shop - Browse the digital marketplace
                buy <item_id> - Purchase equipment from the shop
                use <item_id> - Use an item from your inventory
                """.trimIndent()
            }
            "hint" -> {
                provideHint(state)
            }
            "clear" -> {
                _terminalState.update { it.copy(terminalOutput = "") }
                ""
            }
            "pwd" -> {
                state.currentDirectory
            }
            "whoami" -> {
                if (state.isConnectedToTarget) {
                    if (state.hasRootAccess) "root" else "admin"
                } else {
                    "user"
                }
            }
            "exit" -> {
                if (state.isConnectedToTarget) {
                    _terminalState.update { 
                        it.copy(
                            isConnectedToTarget = false,
                            currentTargetIp = "",
                            hasRootAccess = false,
                            currentDirectory = "/home/user"
                        ) 
                    }
                    "Connection closed."
                } else {
                    "Not connected to any remote system."
                }
            }
            "ls" -> {
                val path = if (parts.size > 1) parts[1] else state.currentDirectory
                listFiles(path, state)
            }
            "cd" -> {
                if (parts.size < 2) {
                    "Usage: cd <directory>"
                } else {
                    changeDirectory(parts[1], state)
                }
            }
            "cat" -> {
                if (parts.size < 2) {
                    "Usage: cat <filename>"
                } else {
                    val output = readFile(parts[1], state)
                    
                    // Check for quest objective completion
                    if (output != "No such file: ${parts[1]}" && 
                        output != "Permission denied: ${parts[1].split("/").last()}" &&
                        output != "${parts[1].split("/").last()} is a directory, not a file") {
                        
                        questSystem.checkObjectiveCompletion(
                            ObjectiveType.READ_FILE,
                            targetPath = normalizePath(parts[1], state.currentDirectory)
                        )
                    }
                    
                    output
                }
            }
            "scan" -> {
                val output = scanNetwork(state)
                
                // Check for quest objective completion
                if (output.contains("Found 3 potential targets")) {
                    questSystem.checkObjectiveCompletion(ObjectiveType.SCAN_NETWORK)
                }
                
                output
            }
            "ssh" -> {
                if (parts.size < 2) {
                    "Usage: ssh <ip_address>"
                } else {
                    val output = connectToServer(parts[1], state)
                    
                    // Check for quest objective completion if connection successful
                    if (output.contains("Connection established")) {
                        questSystem.checkObjectiveCompletion(
                            ObjectiveType.CONNECT_TO_SERVER, 
                            targetServer = parts[1]
                        )
                    }
                    
                    output
                }
            }
            "crack" -> {
                if (parts.size < 2) {
                    "Usage: crack <file>"
                } else {
                    val output = crackPassword(parts[1], state)
                    
                    // Check for quest objective completion
                    if (output.contains("Found credentials")) {
                        questSystem.checkObjectiveCompletion(
                            ObjectiveType.CRACK_PASSWORD,
                            targetPath = parts[1]
                        )
                    }
                    
                    output
                }
            }
            "sudo" -> {
                if (!state.isConnectedToTarget) {
                    "Command not found: sudo"
                } else if (state.hasRootAccess) {
                    "You already have root privileges."
                } else {
                    _terminalState.update { it.copy(hasRootAccess = true) }
                    
                    // Check for quest objective completion
                    questSystem.checkObjectiveCompletion(ObjectiveType.ESCALATE_PRIVILEGES)
                    
                    "Root privileges obtained!\nYou now have access to the entire system."
                }
            }
            "quests" -> {
                val availableQuests = questSystem.getAvailableQuests()
                val activeQuest = questSystem.getActiveQuest()
                
                if (availableQuests.isEmpty() && activeQuest == null) {
                    "No quests available at the moment."
                } else {
                    val sb = StringBuilder("Available Quests:\n")
                    
                    if (activeQuest != null) {
                        sb.append("ACTIVE: ${activeQuest.title} - ${activeQuest.description}\n")
                    }
                    
                    availableQuests.forEach { quest ->
                        sb.append("- ${quest.id}: ${quest.title} (Level ${quest.requiredLevel})\n")
                    }
                    
                    sb.append("\nUse 'accept <quest_id>' to start a quest.")
                    sb.toString()
                }
            }
            "quest" -> {
                val activeQuest = questSystem.getActiveQuest()
                if (activeQuest == null) {
                    "No active quest. Use 'quests' to see available quests."
                } else {
                    val sb = StringBuilder("${activeQuest.title}\n")
                    sb.append("${activeQuest.description}\n\n")
                    sb.append("Objectives:\n")
                    
                    activeQuest.objectives.forEach { objective ->
                        val status = when (objective.status) {
                            QuestStatus.COMPLETED -> "[✓]"
                            QuestStatus.IN_PROGRESS -> "[...]"
                            QuestStatus.NOT_STARTED -> "[ ]"
                            QuestStatus.FAILED -> "[✗]"
                        }
                        sb.append("$status ${objective.description}\n")
                    }
                    
                    sb.toString()
                }
            }
            "accept" -> {
                if (parts.size < 2) {
                    "Usage: accept <quest_id>"
                } else {
                    val questId = parts[1]
                    val quest = questSystem.getQuestById(questId)
                    
                    if (quest == null) {
                        "No quest found with ID: $questId"
                    } else {
                        questSystem.startQuest(questId)
                        "Started quest: ${quest.title}"
                    }
                }
            }
            "stats" -> {
                val playerStats = questSystem.state.value.playerStats
                """
                Hacker Stats:
                Level: ${playerStats.level}
                XP: ${playerStats.xp}
                Hacking Skill: ${playerStats.hackingSkill}
                Crypto Skill: ${playerStats.cryptoSkill}
                Social Engineering: ${playerStats.socialEngSkill}
                
                Completed Quests: ${questSystem.state.value.completedQuestIds.size}
                """.trimIndent()
            }
            "nmap" -> {
                val output = nmapNetwork(state)
                
                // Also check for scan objective completion
                if (output.contains("scanning complete")) {
                    questSystem.checkObjectiveCompletion(ObjectiveType.SCAN_NETWORK)
                }
                
                output
            }
            "ifconfig" -> {
                ifconfig()
            }
            "netstat" -> {
                netstat(state)
            }
            "ps" -> {
                ps(state)
            }
            "grep" -> {
                if (parts.size < 3) {
                    "Usage: grep <pattern> <file>"
                } else {
                    grep(parts[1], parts[2], state)
                }
            }
            "wget" -> {
                if (parts.size < 2) {
                    "Usage: wget <url>"
                } else {
                    wget(parts[1], state)
                }
            }
            "achievements" -> {
                // Trigger navigation to achievements screen
                _navigateToAchievements.value = true
                "Opening achievements panel..."
            }
            "skills" -> {
                "Opening skill tree..."
            }
            "equipment" -> {
                "Opening equipment manager..."
            }
            "missions" -> {
                "Opening mission board..."
            }
            "shop" -> {
                "Opening digital marketplace..."
            }
            "buy" -> {
                buyEquipment(parts.drop(1))
                "Processing purchase..."
            }
            "use" -> {
                useEquipment(parts.drop(1))
                "Attempting to use item..."
            }
            else -> "Command not found: $cmd"
        }
        
        return result
    }
    
    private fun getNodeAtPath(path: String, state: TerminalState): FileSystemNode? {
        val normalizedPath = normalizePath(path, state.currentDirectory)
        val pathParts = normalizedPath.split("/").filter { it.isNotEmpty() }
        
        // Determine which file system to use
        val fileSystem = if (state.isConnectedToTarget) {
            state.remoteFileSystems[state.currentTargetIp] ?: return null
        } else {
            state.localFileSystem
        }
        
        if (pathParts.isEmpty()) {
            return FileSystemNode.Directory("/", fileSystem.toMutableMap())
        }
        
        var current: Map<String, FileSystemNode>? = fileSystem
        
        for (i in 0 until pathParts.size - 1) {
            val part = pathParts[i]
            val node = current?.get(part) as? FileSystemNode.Directory ?: return null
            current = node.children
        }
        
        return current?.get(pathParts.last())
    }
    
    private fun normalizePath(path: String, currentDir: String): String {
        val absolutePath = if (path.startsWith("/")) {
            path
        } else {
            if (currentDir.endsWith("/")) {
                currentDir + path
            } else {
                "$currentDir/$path"
            }
        }
        
        val parts = absolutePath.split("/").filter { it.isNotEmpty() }
        val result = mutableListOf<String>()
        
        for (part in parts) {
            when (part) {
                "." -> {}
                ".." -> {
                    if (result.isNotEmpty()) {
                        result.removeAt(result.size - 1)
                    }
                }
                else -> result.add(part)
            }
        }
        
        return "/" + result.joinToString("/")
    }
    
    private fun listFiles(path: String, state: TerminalState): String {
        val node = getNodeAtPath(path, state)
        
        // Check for permission
        if (state.isConnectedToTarget && !state.hasRootAccess && path.startsWith("/root")) {
            return "Permission denied"
        }
        
        return when (node) {
            is FileSystemNode.Directory -> {
                val dirs = node.children.filter { it.value is FileSystemNode.Directory }
                    .map { "${it.key}/" }
                val files = node.children.filter { it.value is FileSystemNode.File }
                    .map { it.key }
                
                (dirs + files).joinToString("  ")
            }
            is FileSystemNode.File -> {
                "${node.name} is a file, not a directory"
            }
            null -> "No such file or directory: $path"
        }
    }
    
    private fun changeDirectory(path: String, state: TerminalState): String {
        // Check for permission
        if (state.isConnectedToTarget && !state.hasRootAccess && path.startsWith("/root")) {
            return "Permission denied"
        }
        
        val normalizedPath = normalizePath(path, state.currentDirectory)
        val node = getNodeAtPath(normalizedPath, state)
        
        return when (node) {
            is FileSystemNode.Directory -> {
                _terminalState.update { it.copy(currentDirectory = normalizedPath) }
                ""
            }
            is FileSystemNode.File -> {
                "${node.name} is a file, not a directory"
            }
            null -> "No such directory: $path"
        }
    }
    
    private fun readFile(filename: String, state: TerminalState): String {
        // Check for permission
        if (state.isConnectedToTarget && !state.hasRootAccess && filename.startsWith("/root")) {
            return "Permission denied"
        }
        
        val node = getNodeAtPath(filename, state)
        
        return when (node) {
            is FileSystemNode.File -> {
                if (node.isReadable || (state.isConnectedToTarget && state.hasRootAccess)) {
                    node.content
                } else {
                    "Permission denied: ${node.name}"
                }
            }
            is FileSystemNode.Directory -> {
                "${node.name} is a directory, not a file"
            }
            null -> "No such file: $filename"
        }
    }
    
    private fun scanNetwork(state: TerminalState): String {
        // Check if we're connected to target
        if (state.isConnectedToTarget) {
            return "This command is not available on remote systems."
        }
        
        // Check if we have the scan executable
        val scanExePath = "/home/user/bin/scan"
        val scanNode = getNodeAtPath(scanExePath, state)
        
        if (scanNode !is FileSystemNode.File || !scanNode.isExecutable) {
            return "Command not found: scan"
        }
        
        // Generate some random IPs for demonstration
        val newTargets = listOf("192.168.1.105", "192.168.1.240", "192.168.1.12")
        _terminalState.update { it.copy(discoveredIps = newTargets) }
        
        return """
        Scanning network...
        [####################] 100%
        
        Found 3 potential targets:
        192.168.1.105 - Web Server (Ports: 22, 80, 443)
        192.168.1.240 - Database Server (Ports: 22, 3306)
        192.168.1.12 - Personal Computer (Ports: 22)
        """.trimIndent()
    }
    
    private fun connectToServer(ip: String, state: TerminalState): String {
        // Can't connect if already connected
        if (state.isConnectedToTarget) {
            return "Already connected to ${state.currentTargetIp}. Use 'exit' first."
        }
        
        // Check if we have the SSH executable
        val sshExePath = "/home/user/bin/ssh"
        val sshNode = getNodeAtPath(sshExePath, state)
        
        if (sshNode !is FileSystemNode.File || !sshNode.isExecutable) {
            return "Command not found: ssh"
        }
        
        if (!state.discoveredIps.contains(ip)) {
            return "Unknown host: $ip"
        }
        
        _terminalState.update { 
            it.copy(
                isConnectedToTarget = true,
                currentTargetIp = ip,
                currentDirectory = "/home/admin"
            ) 
        }
        
        return """
        Connecting to $ip...
        SSH connection established.
        Login required.
        Use 'crack /etc/shadow' to attempt to crack password.
        """.trimIndent()
    }
    
    private fun crackPassword(file: String, state: TerminalState): String {
        // Check if connected
        if (!state.isConnectedToTarget) {
            return "Not connected to any server. Use 'ssh <ip>' first."
        }
        
        // Check if we have the crack executable
        val crackExePath = "/home/user/bin/crack"
        val crackNode = getNodeAtPath(crackExePath, state)
        
        if (crackNode !is FileSystemNode.File || !crackNode.isExecutable) {
            return "Command not found: crack"
        }
        
        if (file != "/etc/shadow") {
            return "File not found: $file"
        }
        
        // Simulate cracking time with visual progress
        return """
        Cracking passwords in /etc/shadow...
        [####################] 100%
        
        Found credentials:
        Username: admin
        Password: password123
        
        You now have access to the system!
        Try 'sudo' to escalate privileges.
        """.trimIndent()
    }
    
    private fun provideHint(state: TerminalState): String {
        val activeQuest = questSystem.getActiveQuest() ?: return "No active quest. Use 'quests' to see available quests."
        
        // Find the first incomplete objective
        val incompleteObjective = activeQuest.objectives.find { 
            it.status != QuestStatus.COMPLETED 
        } ?: return "You've completed all objectives in this quest!"
        
        // Provide different hints based on the quest and objective
        return when (activeQuest.id) {
            "tutorial" -> {
                when (incompleteObjective.id) {
                    "tutorial_1" -> "Try using the 'ls' command to see what's in your current directory. Then use 'cd documents' to navigate to your documents folder, and 'cat hacking_guide.txt' to read the guide."
                    "tutorial_2" -> "Use the 'scan' command to search for potential targets on the network."
                    else -> "Check your quest objectives with the 'quest' command to see what you need to do next."
                }
            }
            "web_server_hack" -> {
                when (incompleteObjective.id) {
                    "web_1" -> "Use 'ssh 192.168.1.105' to connect to the web server."
                    "web_2" -> "Now that you're connected, use 'crack /etc/shadow' to find the admin password."
                    "web_3" -> "Try looking in the admin's home directory with 'ls /home/admin' and then read the file with 'cat'."
                    "web_4" -> "Website files are often stored in /var/www. Use 'ls /var/www' to explore and find the configuration file."
                    else -> "Check your quest objectives with the 'quest' command to see what you need to do next."
                }
            }
            "db_server_hack" -> {
                when (incompleteObjective.id) {
                    "db_1" -> "Use 'ssh 192.168.1.240' to connect to the database server."
                    "db_2" -> "Now that you're connected, use 'crack /etc/shadow' to find the admin password."
                    "db_3" -> "Look in the admin's home directory with 'ls /home/admin' and read their notes."
                    "db_4" -> "Try using 'sudo' to escalate your privileges to root."
                    "db_5" -> "Database files are often stored in /var/lib/mysql. Navigate there and look for database dumps."
                    else -> "Check your quest objectives with the 'quest' command to see what you need to do next."
                }
            }
            "personal_data_theft" -> {
                when (incompleteObjective.id) {
                    "personal_1" -> "Use 'ssh 192.168.1.12' to connect to the personal computer."
                    "personal_2" -> "Now that you're connected, use 'crack /etc/shadow' to find the user password."
                    "personal_3" -> "People often store sensitive information in their documents folder. Try 'ls /home/user/documents'."
                    else -> "Check your quest objectives with the 'quest' command to see what you need to do next."
                }
            }
            else -> "Focus on completing your current objective: ${incompleteObjective.description}"
        }
    }
    
    private fun nmapNetwork(state: TerminalState): String {
        // Check if we're connected to target
        if (state.isConnectedToTarget) {
            return "This command is not available on remote systems."
        }
        
        // Check if we have the nmap executable
        val nmapExePath = "/home/user/bin/nmap"
        val nmapNode = getNodeAtPath(nmapExePath, state)
        
        if (nmapNode !is FileSystemNode.File || !nmapNode.isExecutable) {
            return "Command not found: nmap"
        }
        
        // Generate the same targets as scan for consistency
        val newTargets = listOf("192.168.1.105", "192.168.1.240", "192.168.1.12")
        _terminalState.update { it.copy(discoveredIps = newTargets) }
        
        return """
        Starting Nmap 7.92 ( https://nmap.org ) at ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}
        Nmap scan report for 192.168.1.105
        Host is up (0.015s latency).
        Not shown: 997 closed ports
        PORT    STATE SERVICE
        22/tcp  open  ssh
        80/tcp  open  http
        443/tcp open  https
        
        Nmap scan report for 192.168.1.240
        Host is up (0.020s latency).
        Not shown: 998 closed ports
        PORT     STATE SERVICE
        22/tcp   open  ssh
        3306/tcp open  mysql
        
        Nmap scan report for 192.168.1.12
        Host is up (0.010s latency).
        Not shown: 999 closed ports
        PORT   STATE SERVICE
        22/tcp open  ssh
        
        Nmap scanning complete: 3 IP addresses (3 hosts up) scanned in 2.31 seconds
        """.trimIndent()
    }
    
    private fun ifconfig(): String {
        // Only available on local machine
        // NOTE: Removed unused 'state' parameter and the check for state.isConnectedToTarget
        // as this function should only logically be called on the local machine anyway based on command processing.
        // If context changes, this might need adjustment.
        
        return """
        eth0: flags=4163<UP,BROADCAST,RUNNING,MULTICAST>  mtu 1500
                inet 192.168.1.100  netmask 255.255.255.0  broadcast 192.168.1.255
                inet6 fe80::c112:8c9e:9f5c:7bdb  prefixlen 64  scopeid 0x20<link>
                ether 02:42:ac:11:00:02  txqueuelen 1000  (Ethernet)
                RX packets 1256  bytes 116214 (113.4 KiB)
                RX errors 0  dropped 0  overruns 0  frame 0
                TX packets 421  bytes 46708 (45.6 KiB)
                TX errors 0  dropped 0 overruns 0  carrier 0  collisions 0
        
        lo: flags=73<UP,LOOPBACK,RUNNING>  mtu 65536
                inet 127.0.0.1  netmask 255.0.0.0
                inet6 ::1  prefixlen 128  scopeid 0x10<host>
                loop  txqueuelen 1000  (Local Loopback)
                RX packets 250  bytes 49952 (48.7 KiB)
                RX errors 0  dropped 0  overruns 0  frame 0
                TX packets 250  bytes 49952 (48.7 KiB)
                TX errors 0  dropped 0 overruns 0  carrier 0  collisions 0
        """.trimIndent()
    }
    
    private fun netstat(state: TerminalState): String {
        val baseOutput = """
        Active Internet connections (w/o servers)
        Proto Recv-Q Send-Q Local Address           Foreign Address         State      
        tcp        0      0 localhost:45134         localhost:9050          ESTABLISHED
        """.trimIndent()
        
        // Add SSH connection if connected to remote server
        val sshConnections = if (state.isConnectedToTarget) {
            "\ntcp        0      0 192.168.1.100:22         ${state.currentTargetIp}:44582      ESTABLISHED"
        } else {
            ""
        }
        
        return baseOutput + sshConnections
    }
    
    private fun ps(state: TerminalState): String {
        val standardProcesses = """
        PID TTY          TIME CMD
          1 ?        00:00:02 systemd
          2 ?        00:00:00 kthreadd
         10 ?        00:00:00 ksoftirqd/0
         11 ?        00:00:00 rcu_sched
        300 ?        00:00:01 NetworkManager
        450 ?        00:00:00 sshd
        600 ?        00:00:02 bash
        """.trimIndent()
        
        // Add additional processes if on remote system
        return if (state.isConnectedToTarget) {
            standardProcesses + "\n" + """
            700 ?        00:00:01 apache2
            701 ?        00:00:00 apache2
            702 ?        00:00:00 apache2
            800 ?        00:00:04 mysqld
            """ + (if (state.currentTargetIp == "192.168.1.240") "\n850 ?        00:00:01 backup_script" else "")
        } else {
            standardProcesses
        }
    }
    
    private fun grep(pattern: String, filePath: String, state: TerminalState): String {
        val fileContent = readFile(filePath, state)
        
        // If the file couldn't be read, return the error
        if (fileContent.startsWith("No such file") || 
            fileContent.contains("is a directory") || 
            fileContent.contains("Permission denied")) {
            return fileContent
        }
        
        // Search for pattern in the file content
        val lines = fileContent.split("\n")
        val matches = lines.filter { it.contains(pattern) }
        
        return if (matches.isEmpty()) {
            "No matches found for '$pattern' in $filePath"
        } else {
            matches.joinToString("\n")
        }
    }
    
    private fun wget(url: String, state: TerminalState): String {
        // Simple implementation that simulates downloading files
        val validUrls = mapOf(
            "http://192.168.1.105/backup.zip" to "backup.zip",
            "http://192.168.1.240/dump.sql" to "dump.sql",
            "https://github.com/hacker/exploits/exploit.py" to "exploit.py"
        )
        
        return if (validUrls.containsKey(url)) {
            val filename = validUrls[url]
            
            // Check where we should save the file
            val currentDirPath = state.currentDirectory
            val node = getNodeAtPath(currentDirPath, state)
            
            if (node is FileSystemNode.Directory) {
                """
                --2023-05-10 15:20:30--  $url
                Resolving ${url.substringAfter("://").substringBefore("/")}... connected.
                HTTP request sent, awaiting response... 200 OK
                Length: 4456 (4.4K) [application/zip]
                Saving to: '$filename'
                
                $filename           100%[======================>]   4.35K  --.-KB/s    in 0.1s    
                
                2023-05-10 15:20:30 (44.5 KB/s) - '$filename' saved [4456/4456]
                
                Downloaded file saved to $currentDirPath/$filename
                """.trimIndent()
            } else {
                "Cannot save file: current location is not a directory"
            }
        } else {
            """
            --2023-05-10 15:20:30--  $url
            Resolving ${url.substringAfter("://").substringBefore("/")}...
            Could not resolve host: ${url.substringAfter("://").substringBefore("/")}
            wget: unable to resolve host address '${url.substringAfter("://").substringBefore("/")}'
            """.trimIndent()
        }
    }
    
    // Make this public
    fun buyEquipment(args: List<String>) {
        if (args.isEmpty()) {
            addOutput("Usage: buy [item_id]")
            return
        }
        
        val itemId = args[0]
        val playerLevel = _questState.value.playerLevel
        val playerCredits = _terminalState.value.credits
        
        val result = _equipmentSystem.buyEquipment(itemId, playerLevel, playerCredits)
        if (result) {
            // Update equipment state
            _equipmentState.value = _equipmentSystem.state.value
            
            // Deduct credits
            val equipment = _equipmentSystem.state.value.inventory[itemId]
            if (equipment != null) {
                _terminalState.update { it.copy(credits = it.credits - equipment.basePrice) }
                addOutput("Successfully purchased: ${equipment.name}")
                addOutput("Credits remaining: ${_terminalState.value.credits}")
            }
        } else {
            addOutput("Failed to purchase item. Check requirements or available credits.")
        }
    }

    fun useEquipment(args: List<String>) {
        if (args.isEmpty()) {
            addOutput("Usage: use [item_id]")
            return
        }
        
        val itemId = args[0]
        val result = _equipmentSystem.useConsumable(itemId)
        
        if (result) {
            // Update equipment state
            _equipmentState.value = _equipmentSystem.state.value
            
            addOutput("Used item successfully!")
            
            // Apply equipment effects if needed
            val effects = _equipmentSystem.calculateEquippedEffects()
            // TODO: Apply effects to player stats or gameplay mechanics
        } else {
            addOutput("Failed to use equipment. It might not be a consumable or you don't own it.")
        }
    }

    fun equipItem(itemId: String) {
        val result = _equipmentSystem.equipItem(itemId)
        if (result) {
            _equipmentState.value = _equipmentSystem.state.value
            addOutput("Equipped item: ${getEquipmentName(itemId)}")
        } else {
            addOutput("Could not equip item. You might need to unequip another item in that category first.")
        }
    }

    fun unequipItem(itemId: String) {
        val result = _equipmentSystem.unequipItem(itemId)
        if (result) {
            _equipmentState.value = _equipmentSystem.state.value
            addOutput("Unequipped item: ${getEquipmentName(itemId)}")
        } else {
            addOutput("Could not unequip item.")
        }
    }

    fun upgradeEquipment(itemId: String) {
        // Check if we can afford the upgrade
        val equipment = _equipmentSystem.state.value.inventory[itemId]
        if (equipment == null) {
            addOutput("Equipment not found.")
            return
        }
        
        val upgradeCost = equipment.upgradePrice * equipment.level
        if (upgradeCost > _terminalState.value.credits) {
            addOutput("Not enough credits to upgrade this equipment.")
            return
        }
        
        val result = _equipmentSystem.upgradeEquipment(itemId, _terminalState.value.credits)
        if (result) {
            // Deduct credits
            _terminalState.update { it.copy(credits = it.credits - upgradeCost) }
            _equipmentState.value = _equipmentSystem.state.value
            
            val updatedEquipment = _equipmentSystem.state.value.inventory[itemId]
            addOutput("Upgraded ${getEquipmentName(itemId)} to level ${updatedEquipment?.level}.")
            addOutput("New stats:")
            
            // Removed unused loop: updatedEquipment?.effects?.forEach { effect -> ... } 
            // If you want to display effects, add the logic here.
        } else {
            addOutput("Could not upgrade equipment.")
        }
    }

    // Helper methods for equipment
    private fun getEquipmentName(itemId: String): String {
        return _equipmentSystem.state.value.inventory[itemId]?.name ?: "Unknown Item"
    }
    
    private fun updateQuestState() {
        _questState.value = questSystem.state.value
    }

    // MISSION HANDLING

    fun startMission(missionId: String) {
        val result = _missionSystem.startMission(missionId)
        if (result) {
            _missionState.value = _missionSystem.state.value
            addOutput("Mission started: ${_missionSystem.state.value.activeMission?.title}")
            
            // Show objectives
            _missionSystem.state.value.activeMission?.let { mission ->
                addOutput("Objectives:")
                mission.objectives.forEach { objective ->
                    addOutput("- ${objective.description}")
                }
            }
        } else {
            addOutput("Could not start mission. Check requirements or complete your current mission first.")
        }
    }

    fun failCurrentMission() {
        val result = _missionSystem.failMission()
        if (result) {
            _missionState.value = _missionSystem.state.value
            addOutput("Mission failed.")
        }
    }

    fun generateNewMission() {
        _missionSystem.generateNewMission(_questState.value.playerLevel)
        _missionState.value = _missionSystem.state.value
        addOutput("New mission available on the mission board.")
    }

    // Process command for mission progress
    private fun checkMissionProgress(cmd: String, args: List<String>) {
        // Removed unused variable: val commandInfo = CommandInfo(cmd, args, _terminalState.value)
        // val wasUpdated = _missionSystem.processCommand(commandInfo) // Commented out: MissionSystem likely handles progress internally or differently.
        
        // if (wasUpdated) { // Assuming check below should run if mission state might have changed implicitly
            // Mission state was updated - Re-fetch state in case internal changes occurred
            _missionState.value = _missionSystem.state.value
            
            // Check if a mission objective was completed (check the updated state)
            val activeMission = _missionSystem.state.value.activeMission
            if (activeMission != null) {
                // Find the *last* completed objective as a proxy for recent completion
                val lastCompletedObjective = activeMission.objectives.lastOrNull { it.status == MissionStatus.COMPLETED }
                // TODO: Improve this logic - need a better way to know if an objective *just* completed
                // Maybe compare with previous state or have MissionSystem return info?
                if (lastCompletedObjective != null /* && objective wasn't complete before */) {
                    addOutput("Objective completed: ${lastCompletedObjective.description}")
                }
                
                // Check if mission was completed (activeMission is now null, but was previously not null)
                if (_missionSystem.state.value.activeMission == null && _missionState.value.activeMission != null) {
                    val completedMission = _missionSystem.state.value.completedMissions.lastOrNull { it.id == _missionState.value.activeMission?.id }
                    
                    if (completedMission != null) {
                        // Award rewards
                        val xpReward = completedMission.rewards.find { it.type == RewardType.XP }?.amount ?: 0
                        val creditReward = completedMission.rewards.find { it.type == RewardType.MONEY }?.amount ?: 0
                        val itemReward = completedMission.rewards.find { it.type == RewardType.EQUIPMENT }?.value
                        
                        // questSystem.addXp(xpReward) // Commented out: XP should be handled by QuestSystem internally
                        updateQuestState() // Update quest state to reflect potential level ups from XP gain
                        
                        _terminalState.update { it.copy(credits = it.credits + creditReward) }
                        
                        // Add equipment if there's an item reward
                        if (itemReward != null) {
                            _equipmentSystem.addRewardEquipment(itemReward)
                            _equipmentState.value = _equipmentSystem.state.value
                        }
                        
                        // Show completion message
                        addOutput("")
                        addOutput("==== MISSION COMPLETE ====")
                        addOutput("${completedMission.title} completed successfully!")
                        addOutput("Rewards:")
                        addOutput("- $xpReward XP")
                        addOutput("- $creditReward Credits")
                        if (itemReward != null) {
                            addOutput("- Equipment: $itemReward")
                        }
                        addOutput("==========================")
                    }
                }
            } // else { // If wasUpdated was true but no active mission, maybe handle differently? }
        // }
    }

    // Public function to process a command directly 
    fun processCommand(command: String) {
        val currentState = _terminalState.value
        val parts = command.split(" ")
        val cmd = parts[0].lowercase()
        val args = if (parts.size > 1) parts.subList(1, parts.size) else listOf()
        
        // Check for mission progress first
        checkMissionProgress(cmd, args)
        
        // Get result from processing the command
        val result = processCommand(command, currentState)
        
        // Update terminal state with the output
        _terminalState.update { 
            it.copy(output = it.output + result.split("\n"))
        }
    }
}

// Command info class for processing mission commands
data class CommandInfo(
    val command: String,
    val args: List<String>,
    val terminalState: TerminalState
)

// Default file system structure
private fun defaultFileSystem(): Map<String, Any> {
    return mapOf(
        "home" to mapOf(
            "user" to mapOf(
                "documents" to mapOf(
                    "notes.txt" to "My secret notes",
                    "passwords.txt" to "admin:password123"
                ),
                "desktop" to mapOf(
                    "hack_tool.exe" to "Executable program"
                )
            )
        ),
        "var" to mapOf(
            "log" to mapOf(
                "system.log" to "System log file",
                "auth.log" to "Authentication log file"
            )
        )
    )
} 