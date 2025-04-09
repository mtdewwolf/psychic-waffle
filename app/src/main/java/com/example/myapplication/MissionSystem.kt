package com.example.myapplication

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import kotlin.random.Random

// Types of missions
enum class MissionType {
    TUTORIAL,        // Teaching mechanics
    NETWORK_SCAN,    // Discover network topology
    SERVER_BREACH,   // Gain access to a server
    DATA_THEFT,      // Steal specific data
    VULNERABILITY,   // Exploit a specific vulnerability
    PERSISTENCE,     // Maintain access to a system
    SOCIAL,          // Social engineering
    FORENSIC,        // Analyze evidence
    STEALTH,         // Operate without detection
    CUSTOM_EXPLOIT,  // Create a custom exploit
    STORY,           // Main story progression
    HACK,            // Break into a system
    EXTRACT,         // Extract data from a system
    PLANT,           // Plant data or malware
    SCAN,            // Scan and map a network
    PROTECT,         // Stop hackers from accessing a system
    ANALYZE          // Analyze data or code
}

// Mission difficulty levels
enum class DifficultyLevel {
    BEGINNER,
    EASY,
    MEDIUM,
    HARD,
    EXPERT
}

// Different mission statuses
enum class MissionStatus {
    LOCKED,          // Not yet available
    AVAILABLE,       // Can be started
    IN_PROGRESS,     // Currently active
    COMPLETED,       // Successfully finished
    FAILED           // Failed to complete
}

// Mission objectives that must be completed
data class MissionObjective(
    val id: String = UUID.randomUUID().toString(),
    val description: String,
    val type: ObjectiveType,
    val targetId: String = "", // ID of target (IP, file, etc.)
    val targetDetail: String = "", // Additional detail
    val isOptional: Boolean = false,
    val status: MissionStatus = MissionStatus.LOCKED,
    val timeLimit: Int = 0, // Time limit in seconds (0 = no limit)
    val requiresSkill: String = "" // Skill ID required to complete this objective
)

// Types of objectives
enum class ObjectiveType {
    SCAN_NETWORK,
    CONNECT_TO_SERVER,
    CRACK_PASSWORD,
    FIND_FILE,
    READ_FILE,
    DOWNLOAD_FILE,
    UPLOAD_FILE,
    EXPLOIT_SERVICE,
    ESCALATE_PRIVILEGES,
    INSTALL_BACKDOOR,
    ERASE_LOGS,
    SOCIAL_ATTACK,
    ANALYZE_DATA,
    AVOID_DETECTION,
    CREATE_SCRIPT,
    CUSTOM_COMMAND
}

// Mission requirements to start
data class MissionRequirement(
    val type: RequirementType,
    val value: String = "",
    val amount: Int = 1
)

// Different types of requirements
enum class RequirementType {
    PLAYER_LEVEL,    // Minimum player level
    SKILL,           // Specific skill required
    SKILL_CATEGORY,  // Skill category required
    PREVIOUS_MISSION, // Previous mission must be completed
    REPUTATION,      // Minimum reputation with faction
    EQUIPMENT,       // Specific equipment needed
    STORY_PROGRESS   // Story progress required
}

// Rewards given for mission completion
data class MissionReward(
    val type: RewardType,
    val value: String = "",
    val amount: Int = 0
)

// Types of rewards
enum class RewardType {
    XP,              // Experience points
    SKILL_POINTS,    // Skill points to spend
    MONEY,           // Virtual currency
    EQUIPMENT,       // New equipment
    SKILL_UNLOCK,    // Unlock a new skill
    REPUTATION,      // Reputation with faction
    STORY_PROGRESS,  // Advance the story
    INTEL,           // Information for future missions
    NEW_COMMANDS,    // Unlock new terminal commands
    CUSTOM_SCRIPT    // Custom script for future use
}

// Location/context for the mission
data class MissionLocation(
    val name: String,
    val description: String,
    val networkSegment: NetworkSegment? = null,
    val ipAddresses: List<String> = listOf(),
    val isVirtual: Boolean = false // Whether this is a virtual/training environment
)

// A mission that can be assigned to the player
data class Mission(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val briefing: String,
    val type: MissionType,
    val difficulty: DifficultyLevel,
    val objectives: List<MissionObjective>,
    val requirements: List<MissionRequirement> = listOf(),
    val rewards: List<MissionReward> = listOf(),
    val location: MissionLocation? = null,
    val followUpMissionIds: List<String> = listOf(),
    val timeLimit: Int = 0, // Time limit in seconds (0 = no limit)
    val isStoryMission: Boolean = false,
    val isRepeatable: Boolean = false,
    val failureText: String = "Mission failed.",
    val successText: String = "Mission completed successfully!",
    val hints: List<String> = listOf(),
    var status: MissionStatus = MissionStatus.LOCKED
)

// State of the mission system
data class MissionSystemState(
    val missions: Map<String, Mission> = mapOf(),
    val missionStatus: Map<String, MissionStatus> = mapOf(),
    val activeMissionId: String? = null,
    val completedMissionIds: Set<String> = setOf(),
    val storyProgress: Int = 0, // Overall story progress (0-100)
    val showMissionNotification: Boolean = false,
    val missionNotification: String = "",
    val missionLog: List<String> = listOf(), // History of mission events
    val availableMissions: List<Mission> = listOf(),
    val activeMission: Mission? = null,
    val completedMissions: List<Mission> = listOf(),
    val failedMissions: List<Mission> = listOf()
)

// Add to the top of the file after package declaration
enum class MissionDifficulty {
    BEGINNER,
    EASY,
    MEDIUM,
    HARD,
    EXPERT
}

class MissionSystem {
    private val _state = MutableStateFlow(MissionSystemState(missions = defaultMissions()))
    val state: StateFlow<MissionSystemState> = _state.asStateFlow()
    
    private val _missionGenerator = MissionGenerator()
    
    init {
        // Initialize mission statuses
        val initialStatuses = mutableMapOf<String, MissionStatus>()
        _state.value.missions.forEach { (id, mission) ->
            // Tutorial and first story mission are available by default
            initialStatuses[id] = when {
                mission.type == MissionType.TUTORIAL -> MissionStatus.AVAILABLE
                mission.isStoryMission && mission.requirements.isEmpty() -> MissionStatus.AVAILABLE
                else -> MissionStatus.LOCKED
            }
        }
        
        _state.update { 
            it.copy(missionStatus = initialStatuses) 
        }
        
        // Generate initial missions
        generateInitialMissions()
    }
    
    // Start a mission
    fun startMission(missionId: String): Boolean {
        val currentState = _state.value
        
        // Check if mission exists and is available
        if (currentState.missionStatus[missionId] != MissionStatus.AVAILABLE) {
            return false
        }
        
        // Check if another mission is already active
        if (currentState.activeMissionId != null) {
            return false
        }
        
        // Update mission and objectives status
        val mission = currentState.missions[missionId] ?: return false
        val updatedStatuses = currentState.missionStatus.toMutableMap()
        updatedStatuses[missionId] = MissionStatus.IN_PROGRESS
        
        _state.update { 
            it.copy(
                activeMissionId = missionId,
                missionStatus = updatedStatuses,
                showMissionNotification = true,
                missionNotification = "Mission Started: ${mission.title}",
                missionLog = it.missionLog + "Started mission: ${mission.title}"
            ) 
        }
        
        return true
    }
    
    // Get the active mission
    fun getActiveMission(): Mission? {
        val missionId = _state.value.activeMissionId ?: return null
        return _state.value.missions[missionId]
    }
    
    // Complete a mission objective
    fun completeObjective(missionId: String, objectiveId: String): Boolean {
        val currentState = _state.value
        
        // Check if mission is active
        if (currentState.activeMissionId != missionId || 
            currentState.missionStatus[missionId] != MissionStatus.IN_PROGRESS) {
            return false
        }
        
        val mission = currentState.missions[missionId] ?: return false
        val objective = mission.objectives.find { it.id == objectiveId } ?: return false
        
        // Update objective status
        val updatedMission = mission.copy(
            objectives = mission.objectives.map { 
                if (it.id == objectiveId) it.copy(status = MissionStatus.COMPLETED) else it 
            }
        )
        
        val updatedMissions = currentState.missions.toMutableMap()
        updatedMissions[missionId] = updatedMission
        
        _state.update { 
            it.copy(
                missions = updatedMissions,
                showMissionNotification = true,
                missionNotification = "Objective Completed: ${objective.description}",
                missionLog = it.missionLog + "Completed objective: ${objective.description}"
            ) 
        }
        
        // Check if all objectives are completed
        checkMissionCompletion(missionId)
        
        return true
    }
    
    // Check if a mission is completed (all required objectives done)
    private fun checkMissionCompletion(missionId: String) {
        val currentState = _state.value
        val mission = currentState.missions[missionId] ?: return
        
        // Check if all non-optional objectives are completed
        val allRequiredCompleted = mission.objectives
            .filter { !it.isOptional }
            .all { it.status == MissionStatus.COMPLETED }
        
        if (allRequiredCompleted) {
            completeMission(missionId)
        }
    }
    
    // Complete a mission
    private fun completeMission(missionId: String) {
        val currentState = _state.value
        val mission = currentState.missions[missionId] ?: return
        
        // Update mission status
        val updatedStatuses = currentState.missionStatus.toMutableMap()
        updatedStatuses[missionId] = MissionStatus.COMPLETED
        
        // Unlock follow-up missions
        mission.followUpMissionIds.forEach { followUpId ->
            if (currentState.missions.containsKey(followUpId)) {
                updatedStatuses[followUpId] = MissionStatus.AVAILABLE
            }
        }
        
        // Update story progress if it's a story mission
        val updatedStoryProgress = if (mission.isStoryMission) {
            currentState.storyProgress + 5 // Increment by 5% for each story mission
        } else {
            currentState.storyProgress
        }
        
        _state.update { 
            it.copy(
                activeMissionId = null,
                missionStatus = updatedStatuses,
                completedMissionIds = it.completedMissionIds + missionId,
                storyProgress = updatedStoryProgress,
                showMissionNotification = true,
                missionNotification = mission.successText,
                missionLog = it.missionLog + "Completed mission: ${mission.title}"
            ) 
        }
    }
    
    // Fail a mission
    fun failMission(missionId: String, reason: String) {
        val currentState = _state.value
        val mission = currentState.missions[missionId] ?: return
        
        // Update mission status
        val updatedStatuses = currentState.missionStatus.toMutableMap()
        updatedStatuses[missionId] = if (mission.isRepeatable) {
            MissionStatus.AVAILABLE
        } else {
            MissionStatus.FAILED
        }
        
        _state.update { 
            it.copy(
                activeMissionId = null,
                missionStatus = updatedStatuses,
                showMissionNotification = true,
                missionNotification = "${mission.failureText} $reason",
                missionLog = it.missionLog + "Failed mission: ${mission.title} - $reason"
            ) 
        }
    }
    
    // Fail the currently active mission
    fun failMission(): Boolean {
        val currentState = _state.value
        val activeMissionId = currentState.activeMissionId ?: return false
        
        failMission(activeMissionId, "Mission aborted by user.")
        return true
    }
    
    // Check if a mission is available based on requirements
    fun checkMissionAvailability(
        missionId: String, 
        playerLevel: Int, 
        skills: Map<String, Int>,
        unlockedCategories: Set<SkillCategory>
    ): Boolean {
        val currentState = _state.value
        val mission = currentState.missions[missionId] ?: return false
        
        // Mission must not be completed already (unless repeatable)
        if (currentState.completedMissionIds.contains(missionId) && !mission.isRepeatable) {
            return false
        }
        
        // Check all requirements
        return mission.requirements.all { req ->
            when (req.type) {
                RequirementType.PLAYER_LEVEL -> playerLevel >= req.amount
                RequirementType.SKILL -> (skills[req.value] ?: 0) >= req.amount
                RequirementType.SKILL_CATEGORY -> {
                    val category = try {
                        SkillCategory.valueOf(req.value)
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                    category != null && category in unlockedCategories
                }
                RequirementType.PREVIOUS_MISSION -> {
                    currentState.completedMissionIds.contains(req.value)
                }
                RequirementType.STORY_PROGRESS -> currentState.storyProgress >= req.amount
                else -> false // Other requirement types not yet implemented
            }
        }
    }
    
    // Get all available missions
    fun getAvailableMissions(): List<Mission> {
        return _state.value.missions.values.filter { 
            _state.value.missionStatus[it.id] == MissionStatus.AVAILABLE 
        }
    }
    
    // Get missions by type
    fun getMissionsByType(type: MissionType): List<Mission> {
        return _state.value.missions.values.filter { it.type == type }
    }
    
    // Dismiss mission notification
    fun dismissNotification() {
        _state.update { it.copy(showMissionNotification = false) }
    }
    
    // Create default missions
    companion object {
        fun defaultMissions(): Map<String, Mission> {
            val missions = mutableMapOf<String, Mission>()
            
            // Tutorial Mission
            val tutorialMission = Mission(
                id = "tutorial_1",
                title = "Learning the Ropes",
                description = "Learn the basic commands and techniques in a safe environment.",
                briefing = """
                    Welcome to your first hacking mission!
                    
                    This tutorial will teach you the basic commands and tools
                    that you'll need to succeed as a hacker.
                    
                    Follow the objectives to gain a complete understanding
                    of how to navigate systems and gather information.
                """.trimIndent(),
                type = MissionType.TUTORIAL,
                difficulty = DifficultyLevel.BEGINNER,
                objectives = listOf(
                    MissionObjective(
                        id = "tutorial_1_obj1",
                        description = "Use the 'help' command to view available commands",
                        type = ObjectiveType.CUSTOM_COMMAND,
                        targetDetail = "help",
                        status = MissionStatus.IN_PROGRESS
                    ),
                    MissionObjective(
                        id = "tutorial_1_obj2",
                        description = "Navigate to your documents directory using 'cd documents'",
                        type = ObjectiveType.CUSTOM_COMMAND,
                        targetDetail = "cd documents"
                    ),
                    MissionObjective(
                        id = "tutorial_1_obj3",
                        description = "List files in the directory using 'ls'",
                        type = ObjectiveType.CUSTOM_COMMAND,
                        targetDetail = "ls"
                    ),
                    MissionObjective(
                        id = "tutorial_1_obj4",
                        description = "Read the hacking guide using 'cat hacking_guide.txt'",
                        type = ObjectiveType.READ_FILE,
                        targetDetail = "hacking_guide.txt"
                    ),
                    MissionObjective(
                        id = "tutorial_1_obj5",
                        description = "Use the 'scan' command to scan your local network",
                        type = ObjectiveType.SCAN_NETWORK
                    )
                ),
                rewards = listOf(
                    MissionReward(RewardType.XP, amount = 100),
                    MissionReward(RewardType.SKILL_POINTS, amount = 1)
                ),
                location = MissionLocation(
                    name = "Training Environment",
                    description = "A safe environment for learning hacking skills",
                    isVirtual = true
                ),
                followUpMissionIds = listOf("network_scan_1"),
                isStoryMission = true,
                successText = "Tutorial completed! You've learned the basic commands."
            )
            
            // Network Scanning Mission
            val networkScanMission = Mission(
                id = "network_scan_1",
                title = "First Foothold",
                description = "Scan a target network and identify vulnerable systems.",
                briefing = """
                    Now that you know the basics, it's time for your first real challenge.
                    
                    You've been given access to a small corporate network.
                    Your job is to scan for systems and identify potential targets.
                    
                    Be careful not to trigger any alarms!
                """.trimIndent(),
                type = MissionType.NETWORK_SCAN,
                difficulty = DifficultyLevel.EASY,
                objectives = listOf(
                    MissionObjective(
                        id = "network_scan_1_obj1",
                        description = "Use 'nmap' to perform a detailed scan of the network",
                        type = ObjectiveType.SCAN_NETWORK
                    ),
                    MissionObjective(
                        id = "network_scan_1_obj2",
                        description = "Identify at least 3 systems on the network",
                        type = ObjectiveType.SCAN_NETWORK
                    ),
                    MissionObjective(
                        id = "network_scan_1_obj3",
                        description = "Connect to the web server at 203.0.113.10",
                        type = ObjectiveType.CONNECT_TO_SERVER,
                        targetId = "203.0.113.10"
                    ),
                    MissionObjective(
                        id = "network_scan_1_obj4",
                        description = "Find a vulnerability in the web server",
                        type = ObjectiveType.EXPLOIT_SERVICE,
                        targetId = "203.0.113.10"
                    )
                ),
                requirements = listOf(
                    MissionRequirement(RequirementType.PREVIOUS_MISSION, "tutorial_1")
                ),
                rewards = listOf(
                    MissionReward(RewardType.XP, amount = 200),
                    MissionReward(RewardType.SKILL_POINTS, amount = 1),
                    MissionReward(
                        RewardType.SKILL_UNLOCK,
                        value = "port_analysis"
                    )
                ),
                location = MissionLocation(
                    name = "Corporate Network",
                    description = "A small corporate network with various systems",
                    networkSegment = NetworkSegment.INTERNET,
                    ipAddresses = listOf("203.0.113.1", "203.0.113.10")
                ),
                followUpMissionIds = listOf("server_breach_1"),
                isStoryMission = true
            )
            
            // Server Breach Mission
            val serverBreachMission = Mission(
                id = "server_breach_1",
                title = "Breach Protocol",
                description = "Gain access to the corporate web server.",
                briefing = """
                    Good work identifying vulnerabilities in the web server!
                    
                    Now it's time to exploit those vulnerabilities to gain access
                    to the system. Once inside, locate sensitive data and
                    establish a persistent connection.
                    
                    The server appears to have a directory traversal vulnerability.
                """.trimIndent(),
                type = MissionType.SERVER_BREACH,
                difficulty = DifficultyLevel.MEDIUM,
                objectives = listOf(
                    MissionObjective(
                        id = "server_breach_1_obj1",
                        description = "Use directory traversal to access server files",
                        type = ObjectiveType.EXPLOIT_SERVICE,
                        targetId = "203.0.113.10"
                    ),
                    MissionObjective(
                        id = "server_breach_1_obj2",
                        description = "Find the config.php file",
                        type = ObjectiveType.FIND_FILE,
                        targetId = "config.php"
                    ),
                    MissionObjective(
                        id = "server_breach_1_obj3",
                        description = "Extract database credentials from config file",
                        type = ObjectiveType.READ_FILE,
                        targetId = "config.php"
                    ),
                    MissionObjective(
                        id = "server_breach_1_obj4",
                        description = "Crack SSH password for admin user",
                        type = ObjectiveType.CRACK_PASSWORD,
                        targetId = "203.0.113.10"
                    ),
                    MissionObjective(
                        id = "server_breach_1_obj5",
                        description = "Connect via SSH to gain shell access",
                        type = ObjectiveType.CONNECT_TO_SERVER,
                        targetId = "203.0.113.10"
                    ),
                    MissionObjective(
                        id = "server_breach_1_obj6",
                        description = "Install a backdoor for persistent access",
                        type = ObjectiveType.INSTALL_BACKDOOR,
                        targetId = "203.0.113.10",
                        isOptional = true,
                        requiresSkill = "script_creation"
                    )
                ),
                requirements = listOf(
                    MissionRequirement(RequirementType.PREVIOUS_MISSION, "network_scan_1"),
                    MissionRequirement(RequirementType.PLAYER_LEVEL, amount = 3)
                ),
                rewards = listOf(
                    MissionReward(RewardType.XP, amount = 400),
                    MissionReward(RewardType.SKILL_POINTS, amount = 2),
                    MissionReward(
                        RewardType.NEW_COMMANDS,
                        amount = 0,
                        value = "makescript"
                    ),
                    MissionReward(
                        RewardType.INTEL,
                        amount = 0,
                        value = "database_location"
                    )
                ),
                location = MissionLocation(
                    name = "Web Server",
                    description = "The corporate web server hosting their public website",
                    ipAddresses = listOf("203.0.113.10")
                ),
                followUpMissionIds = listOf("data_theft_1"),
                isStoryMission = true
            )
            
            // Data Theft Mission
            val dataTheftMission = Mission(
                id = "data_theft_1",
                title = "Data Breach",
                description = "Infiltrate the database server and exfiltrate customer data.",
                briefing = """
                    With access to the web server, you've discovered information about
                    the internal database server (10.0.0.20).
                    
                    Your objective is to pivot through the network to reach this server
                    and extract sensitive customer information stored in the database.
                    
                    Be careful - this server likely has more security measures in place.
                """.trimIndent(),
                type = MissionType.DATA_THEFT,
                difficulty = DifficultyLevel.HARD,
                objectives = listOf(
                    MissionObjective(
                        id = "data_theft_1_obj1",
                        description = "Use the web server as a pivot point to reach the database server",
                        type = ObjectiveType.CONNECT_TO_SERVER,
                        targetId = "10.0.0.20"
                    ),
                    MissionObjective(
                        id = "data_theft_1_obj2",
                        description = "Crack the database server password",
                        type = ObjectiveType.CRACK_PASSWORD,
                        targetId = "10.0.0.20"
                    ),
                    MissionObjective(
                        id = "data_theft_1_obj3",
                        description = "Find and access the customer database",
                        type = ObjectiveType.FIND_FILE,
                        targetId = "database.sql"
                    ),
                    MissionObjective(
                        id = "data_theft_1_obj4",
                        description = "Extract customer credit card information",
                        type = ObjectiveType.DOWNLOAD_FILE,
                        targetId = "database.sql"
                    ),
                    MissionObjective(
                        id = "data_theft_1_obj5",
                        description = "Cover your tracks by erasing access logs",
                        type = ObjectiveType.ERASE_LOGS,
                        targetId = "10.0.0.20"
                    )
                ),
                requirements = listOf(
                    MissionRequirement(RequirementType.PREVIOUS_MISSION, "server_breach_1"),
                    MissionRequirement(RequirementType.PLAYER_LEVEL, amount = 5),
                    MissionRequirement(RequirementType.SKILL, "port_analysis", 1)
                ),
                rewards = listOf(
                    MissionReward(RewardType.XP, amount = 800),
                    MissionReward(RewardType.SKILL_POINTS, amount = 3),
                    MissionReward(
                        RewardType.SKILL_UNLOCK,
                        amount = 0,
                        value = "cover_tracks"
                    )
                ),
                location = MissionLocation(
                    name = "Corporate Internal Network",
                    description = "The internal network containing sensitive servers",
                    networkSegment = NetworkSegment.CORPORATE,
                    ipAddresses = listOf("10.0.0.20")
                ),
                timeLimit = 600, // 10 minutes
                isStoryMission = true
            )
            
            // Social Engineering Mission
            val socialMission = Mission(
                id = "social_1",
                title = "Human Factor",
                description = "Use social engineering to gain access to an employee account.",
                briefing = """
                    Technical security measures are only one aspect of cybersecurity.
                    The human element is often the weakest link.
                    
                    For this mission, you'll need to create a convincing phishing attack
                    to trick an employee into revealing their credentials.
                    
                    Use the information you've gathered about the company to make
                    your attack more convincing.
                """.trimIndent(),
                type = MissionType.SOCIAL,
                difficulty = DifficultyLevel.MEDIUM,
                objectives = listOf(
                    MissionObjective(
                        id = "social_1_obj1",
                        description = "Gather information about target employees from the company website",
                        type = ObjectiveType.SOCIAL_ATTACK,
                        targetDetail = "reconnaissance"
                    ),
                    MissionObjective(
                        id = "social_1_obj2",
                        description = "Create a convincing phishing email",
                        type = ObjectiveType.SOCIAL_ATTACK,
                        targetDetail = "phishing",
                        requiresSkill = "phishing"
                    ),
                    MissionObjective(
                        id = "social_1_obj3",
                        description = "Set up a fake login page",
                        type = ObjectiveType.SOCIAL_ATTACK,
                        targetDetail = "fake_site"
                    ),
                    MissionObjective(
                        id = "social_1_obj4",
                        description = "Capture employee credentials",
                        type = ObjectiveType.SOCIAL_ATTACK,
                        targetDetail = "credential_harvest"
                    )
                ),
                requirements = listOf(
                    MissionRequirement(RequirementType.PLAYER_LEVEL, amount = 4),
                    MissionRequirement(RequirementType.SKILL_CATEGORY, "SOCIAL")
                ),
                rewards = listOf(
                    MissionReward(RewardType.XP, amount = 500),
                    MissionReward(RewardType.SKILL_POINTS, amount = 2),
                    MissionReward(
                        RewardType.SKILL_UNLOCK,
                        amount = 0,
                        value = "phishing"
                    )
                ),
                isStoryMission = false
            )
            
            missions["tutorial_1"] = tutorialMission
            missions["network_scan_1"] = networkScanMission
            missions["server_breach_1"] = serverBreachMission
            missions["data_theft_1"] = dataTheftMission
            missions["social_1"] = socialMission
            
            return missions
        }
    }
    
    // Generate initial set of missions
    private fun generateInitialMissions() {
        val missions = mutableListOf<Mission>()
        
        // Generate 3 starting missions of varying difficulties
        missions.add(_missionGenerator.generateMission(1)) // Easy mission
        missions.add(_missionGenerator.generateMission(5)) // Medium mission
        missions.add(_missionGenerator.generateMission(10)) // Hard mission
        
        _state.update { it.copy(availableMissions = missions) }
    }
    
    // Generate a new mission based on player level
    fun generateNewMission(playerLevel: Int = 1) {
        val newMission = _missionGenerator.generateMission(playerLevel)
        
        _state.update { it.copy(availableMissions = it.availableMissions + newMission) }
    }
}

// Mission generator for dynamically creating missions
class MissionGenerator {
    // Generate a random mission based on player level
    fun generateMission(playerLevel: Int): Mission {
        val difficulty = when {
            playerLevel <= 5 -> DifficultyLevel.EASY
            playerLevel <= 15 -> DifficultyLevel.MEDIUM
            playerLevel <= 25 -> DifficultyLevel.HARD
            else -> DifficultyLevel.EXPERT
        }
        
        val missionType = MissionType.values().random()
        val targetSystem = TargetSystem.values().random()
        
        // Scale rewards based on difficulty
        val baseXpReward = when (difficulty) {
            DifficultyLevel.BEGINNER -> 25
            DifficultyLevel.EASY -> 50
            DifficultyLevel.MEDIUM -> 150
            DifficultyLevel.HARD -> 350
            DifficultyLevel.EXPERT -> 750
        }
        
        val baseCreditReward = when (difficulty) {
            DifficultyLevel.BEGINNER -> 50
            DifficultyLevel.EASY -> 100
            DifficultyLevel.MEDIUM -> 300
            DifficultyLevel.HARD -> 700
            DifficultyLevel.EXPERT -> 1500
        }
        
        // Apply some randomness to rewards
        val xpReward = (baseXpReward * Random.nextDouble(0.8, 1.2)).toInt()
        val creditReward = (baseCreditReward * Random.nextDouble(0.8, 1.2)).toInt()
        
        // Generate steps based on mission type
        val steps = generateStepsForMission(missionType, difficulty)
        
        // Generate mission ID
        val id = "mission_${System.currentTimeMillis()}_${Random.nextInt(1000, 9999)}"
        
        // Generate mission title and description
        val (title, description) = generateMissionDetails(missionType, targetSystem, difficulty)
        
        return Mission(
            id = id,
            title = title,
            description = description,
            briefing = "",
            type = missionType,
            difficulty = difficulty,
            objectives = steps.map { MissionObjective(description = it.description, type = ObjectiveType.CUSTOM_COMMAND) },
            requirements = listOf(
                MissionRequirement(RequirementType.PLAYER_LEVEL, playerLevel.toString()),
                MissionRequirement(RequirementType.SKILL, "basic_hacking"),
                MissionRequirement(RequirementType.SKILL_CATEGORY, "SOCIAL")
            ),
            rewards = listOf(
                MissionReward(RewardType.XP, amount = xpReward),
                MissionReward(RewardType.MONEY, amount = creditReward),
                MissionReward(RewardType.EQUIPMENT, value = generateRandomItemReward(difficulty))
            ),
            location = MissionLocation(
                name = targetSystem.name,
                description = description,
                networkSegment = when (targetSystem) {
                    TargetSystem.CORPORATE -> NetworkSegment.CORPORATE
                    TargetSystem.GOVERNMENT -> NetworkSegment.GOVERNMENT
                    TargetSystem.PERSONAL -> NetworkSegment.PERSONAL
                    TargetSystem.RESEARCH -> NetworkSegment.RESEARCH
                    TargetSystem.FINANCIAL -> NetworkSegment.FINANCIAL
                    TargetSystem.MILITARY -> NetworkSegment.MILITARY
                    TargetSystem.INFRASTRUCTURE -> NetworkSegment.INFRASTRUCTURE
                },
                ipAddresses = when (targetSystem) {
                    TargetSystem.CORPORATE -> listOf("10.0.0.20")
                    TargetSystem.GOVERNMENT -> listOf("192.168.1.1")
                    TargetSystem.PERSONAL -> listOf("192.168.1.10")
                    TargetSystem.RESEARCH -> listOf("192.168.1.20")
                    TargetSystem.FINANCIAL -> listOf("192.168.1.30")
                    TargetSystem.MILITARY -> listOf("192.168.1.40")
                    TargetSystem.INFRASTRUCTURE -> listOf("192.168.1.50")
                },
                isVirtual = false
            ),
            timeLimit = if (difficulty == DifficultyLevel.EXPERT) 300 else 0,
            isStoryMission = false,
            isRepeatable = false,
            failureText = "Mission failed.",
            successText = "Mission completed successfully!",
            hints = listOf(),
            status = if (playerLevel >= when (difficulty) {
                DifficultyLevel.BEGINNER -> 1
                DifficultyLevel.EASY -> 1
                DifficultyLevel.MEDIUM -> 5
                DifficultyLevel.HARD -> 15
                DifficultyLevel.EXPERT -> 25
            }) MissionStatus.AVAILABLE else MissionStatus.LOCKED
        )
    }
    
    // Generate steps for a mission based on type and difficulty
    private fun generateStepsForMission(type: MissionType, difficulty: DifficultyLevel): List<MissionStep> {
        val numberOfSteps = when (difficulty) {
            DifficultyLevel.BEGINNER -> 2
            DifficultyLevel.EASY -> 3
            DifficultyLevel.MEDIUM -> 5
            DifficultyLevel.HARD -> 7
            DifficultyLevel.EXPERT -> 10
        }
        
        return when (type) {
            MissionType.TUTORIAL -> generateTutorialMissionSteps(numberOfSteps)
            MissionType.NETWORK_SCAN -> generateScanMissionSteps(numberOfSteps)
            MissionType.SERVER_BREACH -> generateHackMissionSteps(numberOfSteps)
            MissionType.DATA_THEFT -> generateExtractMissionSteps(numberOfSteps)
            MissionType.VULNERABILITY -> generateHackMissionSteps(numberOfSteps)
            MissionType.PERSISTENCE -> generatePlantMissionSteps(numberOfSteps)
            MissionType.SOCIAL -> generateSocialMissionSteps(numberOfSteps)
            MissionType.FORENSIC -> generateAnalyzeMissionSteps(numberOfSteps)
            MissionType.STEALTH -> generateStealthMissionSteps(numberOfSteps)
            MissionType.CUSTOM_EXPLOIT -> generateHackMissionSteps(numberOfSteps)
            MissionType.STORY -> generateStoryMissionSteps(numberOfSteps)
            MissionType.HACK -> generateHackMissionSteps(numberOfSteps)
            MissionType.EXTRACT -> generateExtractMissionSteps(numberOfSteps)
            MissionType.PLANT -> generatePlantMissionSteps(numberOfSteps)
            MissionType.SCAN -> generateScanMissionSteps(numberOfSteps)
            MissionType.PROTECT -> generateProtectMissionSteps(numberOfSteps)
            MissionType.ANALYZE -> generateAnalyzeMissionSteps(numberOfSteps)
        }
    }
    
    // Generate steps for a tutorial mission
    private fun generateTutorialMissionSteps(count: Int): List<MissionStep> {
        val steps = mutableListOf<MissionStep>()
        
        steps.add(MissionStep(
            description = "Learn basic commands",
            targetCommand = "help",
            alternativeCommands = listOf("commands", "tutorial")
        ))
        
        steps.add(MissionStep(
            description = "Navigate the file system",
            targetCommand = "ls",
            alternativeCommands = listOf("dir", "list")
        ))
        
        steps.add(MissionStep(
            description = "Read a file",
            targetCommand = "cat",
            alternativeCommands = listOf("read", "view")
        ))
        
        if (count > 3) {
            steps.add(MissionStep(
                description = "Connect to a server",
                targetCommand = "ssh",
                alternativeCommands = listOf("connect")
            ))
            
            steps.add(MissionStep(
                description = "Scan the network",
                targetCommand = "scan",
                alternativeCommands = listOf("nmap")
            ))
        }
        
        return steps.take(count)
    }
    
    // Generate steps for a social engineering mission
    private fun generateSocialMissionSteps(count: Int): List<MissionStep> {
        val steps = mutableListOf<MissionStep>()
        
        steps.add(MissionStep(
            description = "Gather target information",
            targetCommand = "research",
            alternativeCommands = listOf("info_gather")
        ))
        
        steps.add(MissionStep(
            description = "Create phishing message",
            targetCommand = "create_phish",
            alternativeCommands = listOf("craft_message")
        ))
        
        steps.add(MissionStep(
            description = "Send phishing message",
            targetCommand = "send_phish",
            alternativeCommands = listOf("deliver")
        ))
        
        if (count > 3) {
            steps.add(MissionStep(
                description = "Monitor response",
                targetCommand = "monitor",
                alternativeCommands = listOf("watch")
            ))
            
            steps.add(MissionStep(
                description = "Exploit response",
                targetCommand = "exploit",
                alternativeCommands = listOf("take_advantage")
            ))
        }
        
        return steps.take(count)
    }
    
    // Generate steps for a stealth mission
    private fun generateStealthMissionSteps(count: Int): List<MissionStep> {
        val steps = mutableListOf<MissionStep>()
        
        steps.add(MissionStep(
            description = "Disable security systems",
            targetCommand = "disable_security",
            alternativeCommands = listOf("bypass_security")
        ))
        
        steps.add(MissionStep(
            description = "Hide your presence",
            targetCommand = "hide",
            alternativeCommands = listOf("conceal")
        ))
        
        steps.add(MissionStep(
            description = "Avoid detection",
            targetCommand = "avoid_detection",
            alternativeCommands = listOf("stealth")
        ))
        
        if (count > 3) {
            steps.add(MissionStep(
                description = "Cover your tracks",
                targetCommand = "clean_logs",
                alternativeCommands = listOf("erase_evidence")
            ))
            
            steps.add(MissionStep(
                description = "Maintain stealth",
                targetCommand = "maintain_stealth",
                alternativeCommands = listOf("stay_hidden")
            ))
        }
        
        return steps.take(count)
    }
    
    // Generate steps for a story mission
    private fun generateStoryMissionSteps(count: Int): List<MissionStep> {
        val steps = mutableListOf<MissionStep>()
        
        steps.add(MissionStep(
            description = "Follow story objective",
            targetCommand = "story",
            alternativeCommands = listOf("main_quest")
        ))
        
        steps.add(MissionStep(
            description = "Progress story",
            targetCommand = "progress",
            alternativeCommands = listOf("advance")
        ))
        
        steps.add(MissionStep(
            description = "Complete story objective",
            targetCommand = "complete",
            alternativeCommands = listOf("finish")
        ))
        
        if (count > 3) {
            steps.add(MissionStep(
                description = "Unlock next story part",
                targetCommand = "unlock",
                alternativeCommands = listOf("reveal")
            ))
            
            steps.add(MissionStep(
                description = "Continue story",
                targetCommand = "continue",
                alternativeCommands = listOf("proceed")
            ))
        }
        
        return steps.take(count)
    }
    
    // Generate steps for a hack mission
    private fun generateHackMissionSteps(count: Int): List<MissionStep> {
        val steps = mutableListOf<MissionStep>()
        
        // Always start with a scan
        steps.add(MissionStep(
            description = "Scan the target network",
            targetCommand = "scan",
            alternativeCommands = listOf("nmap", "portscan")
        ))
        
        // Add vulnerability identification
        steps.add(MissionStep(
            description = "Identify system vulnerabilities",
            targetCommand = "analyze",
            alternativeCommands = listOf("vuln_scan", "find_exploit")
        ))
        
        // Add exploit steps
        steps.add(MissionStep(
            description = "Exploit system vulnerability",
            targetCommand = "exploit",
            alternativeCommands = listOf("hack", "bypass")
        ))
        
        // If more steps required, add advanced steps
        if (count > 3) {
            steps.add(MissionStep(
                description = "Bypass firewall",
                targetCommand = "bypass_firewall",
                alternativeCommands = listOf("firewall_hack")
            ))
            
            steps.add(MissionStep(
                description = "Escalate privileges",
                targetCommand = "escalate",
                alternativeCommands = listOf("sudo", "root_access")
            ))
        }
        
        // If more steps required, add more advanced steps
        if (count > 5) {
            steps.add(MissionStep(
                description = "Cover your tracks",
                targetCommand = "clean_logs",
                alternativeCommands = listOf("remove_evidence")
            ))
            
            steps.add(MissionStep(
                description = "Install backdoor",
                targetCommand = "install_backdoor",
                alternativeCommands = listOf("backdoor")
            ))
        }
        
        // Return the specified number of steps
        return steps.take(count)
    }
    
    // Generate steps for an extract mission
    private fun generateExtractMissionSteps(count: Int): List<MissionStep> {
        val steps = mutableListOf<MissionStep>()
        
        // Basic extraction steps
        steps.add(MissionStep(
            description = "Access the target system",
            targetCommand = "connect",
            alternativeCommands = listOf("ssh", "access")
        ))
        
        steps.add(MissionStep(
            description = "Locate target data",
            targetCommand = "find",
            alternativeCommands = listOf("search", "locate")
        ))
        
        steps.add(MissionStep(
            description = "Download target data",
            targetCommand = "download",
            alternativeCommands = listOf("extract", "copy")
        ))
        
        // More steps if needed
        if (count > 3) {
            steps.add(MissionStep(
                description = "Decrypt protected files",
                targetCommand = "decrypt",
                alternativeCommands = listOf("crack_encryption")
            ))
            
            steps.add(MissionStep(
                description = "Erase extraction evidence",
                targetCommand = "erase_logs",
                alternativeCommands = listOf("clean_evidence")
            ))
        }
        
        return steps.take(count)
    }
    
    // Generate steps for a plant mission
    private fun generatePlantMissionSteps(count: Int): List<MissionStep> {
        val steps = mutableListOf<MissionStep>()
        
        steps.add(MissionStep(
            description = "Access the target system",
            targetCommand = "connect",
            alternativeCommands = listOf("ssh", "access")
        ))
        
        steps.add(MissionStep(
            description = "Locate insertion point",
            targetCommand = "find_insertion",
            alternativeCommands = listOf("locate_target")
        ))
        
        steps.add(MissionStep(
            description = "Upload payload",
            targetCommand = "upload",
            alternativeCommands = listOf("inject", "plant")
        ))
        
        if (count > 3) {
            steps.add(MissionStep(
                description = "Configure payload",
                targetCommand = "configure",
                alternativeCommands = listOf("setup")
            ))
            
            steps.add(MissionStep(
                description = "Activate payload",
                targetCommand = "activate",
                alternativeCommands = listOf("execute", "run")
            ))
        }
        
        return steps.take(count)
    }
    
    // Generate steps for a scan mission
    private fun generateScanMissionSteps(count: Int): List<MissionStep> {
        val steps = mutableListOf<MissionStep>()
        
        steps.add(MissionStep(
            description = "Perform initial network scan",
            targetCommand = "scan",
            alternativeCommands = listOf("nmap", "net_scan")
        ))
        
        steps.add(MissionStep(
            description = "Identify network devices",
            targetCommand = "identify",
            alternativeCommands = listOf("discover_hosts")
        ))
        
        steps.add(MissionStep(
            description = "Map network topology",
            targetCommand = "map_network",
            alternativeCommands = listOf("topology")
        ))
        
        if (count > 3) {
            steps.add(MissionStep(
                description = "Detect security measures",
                targetCommand = "security_scan",
                alternativeCommands = listOf("detect_security")
            ))
            
            steps.add(MissionStep(
                description = "Identify vulnerabilities",
                targetCommand = "vuln_scan",
                alternativeCommands = listOf("find_vulnerabilities")
            ))
        }
        
        return steps.take(count)
    }
    
    // Generate steps for a protect mission
    private fun generateProtectMissionSteps(count: Int): List<MissionStep> {
        val steps = mutableListOf<MissionStep>()
        
        steps.add(MissionStep(
            description = "Secure access points",
            targetCommand = "secure_access",
            alternativeCommands = listOf("lockdown")
        ))
        
        steps.add(MissionStep(
            description = "Update firewall rules",
            targetCommand = "firewall_update",
            alternativeCommands = listOf("firewall_config")
        ))
        
        steps.add(MissionStep(
            description = "Patch system vulnerabilities",
            targetCommand = "patch",
            alternativeCommands = listOf("update_system")
        ))
        
        if (count > 3) {
            steps.add(MissionStep(
                description = "Install monitoring tools",
                targetCommand = "install_monitor",
                alternativeCommands = listOf("setup_ids")
            ))
            
            steps.add(MissionStep(
                description = "Deploy honeypot",
                targetCommand = "deploy_honeypot",
                alternativeCommands = listOf("setup_trap")
            ))
        }
        
        return steps.take(count)
    }
    
    // Generate steps for an analyze mission
    private fun generateAnalyzeMissionSteps(count: Int): List<MissionStep> {
        val steps = mutableListOf<MissionStep>()
        
        steps.add(MissionStep(
            description = "Access data repository",
            targetCommand = "access_data",
            alternativeCommands = listOf("connect_db")
        ))
        
        steps.add(MissionStep(
            description = "Extract relevant data",
            targetCommand = "extract_data",
            alternativeCommands = listOf("select_data")
        ))
        
        steps.add(MissionStep(
            description = "Run analysis algorithms",
            targetCommand = "analyze",
            alternativeCommands = listOf("process_data")
        ))
        
        if (count > 3) {
            steps.add(MissionStep(
                description = "Detect patterns",
                targetCommand = "find_patterns",
                alternativeCommands = listOf("pattern_recognition")
            ))
            
            steps.add(MissionStep(
                description = "Generate report",
                targetCommand = "report",
                alternativeCommands = listOf("output_results")
            ))
        }
        
        return steps.take(count)
    }
    
    // Generate mission title and description based on type and target
    private fun generateMissionDetails(type: MissionType, target: TargetSystem, difficulty: DifficultyLevel): Pair<String, String> {
        val titlePrefix = when (type) {
            MissionType.TUTORIAL -> "Tutorial"
            MissionType.NETWORK_SCAN -> "Network Reconnaissance"
            MissionType.SERVER_BREACH -> "System Breach"
            MissionType.DATA_THEFT -> "Data Extraction"
            MissionType.VULNERABILITY -> "Vulnerability Exploitation"
            MissionType.PERSISTENCE -> "Persistence Operation"
            MissionType.SOCIAL -> "Social Engineering"
            MissionType.FORENSIC -> "Forensic Analysis"
            MissionType.STEALTH -> "Stealth Operation"
            MissionType.CUSTOM_EXPLOIT -> "Custom Exploit"
            MissionType.STORY -> "Story Mission"
            MissionType.HACK -> "System Breach"
            MissionType.EXTRACT -> "Data Extraction"
            MissionType.PLANT -> "Payload Deployment"
            MissionType.SCAN -> "Network Reconnaissance"
            MissionType.PROTECT -> "System Defense"
            MissionType.ANALYZE -> "Data Analysis"
        }
        
        val targetName = when (target) {
            TargetSystem.CORPORATE -> when (Random.nextInt(3)) {
                0 -> "NexusCorp"
                1 -> "Global Dynamics"
                else -> "Omni Consumer Products"
            }
            TargetSystem.GOVERNMENT -> when (Random.nextInt(3)) {
                0 -> "Central Intelligence"
                1 -> "National Security"
                else -> "City Administration"
            }
            TargetSystem.PERSONAL -> when (Random.nextInt(3)) {
                0 -> "Executive's Laptop"
                1 -> "Researcher's Home System"
                else -> "Politician's Private Server"
            }
            TargetSystem.RESEARCH -> when (Random.nextInt(3)) {
                0 -> "Quantum Research Lab"
                1 -> "BioTech Institute"
                else -> "Advanced Materials Lab"
            }
            TargetSystem.FINANCIAL -> when (Random.nextInt(3)) {
                0 -> "Global Bank"
                1 -> "Investment Firm"
                else -> "Cryptocurrency Exchange"
            }
            TargetSystem.MILITARY -> when (Random.nextInt(3)) {
                0 -> "Defense Network"
                1 -> "Weapons Research"
                else -> "Strategic Command"
            }
            TargetSystem.INFRASTRUCTURE -> when (Random.nextInt(3)) {
                0 -> "Power Grid"
                1 -> "Water Treatment Facility"
                else -> "Transportation System"
            }
        }
        
        val difficultyPrefix = when (difficulty) {
            DifficultyLevel.BEGINNER -> "Basic "
            DifficultyLevel.EASY -> ""
            DifficultyLevel.MEDIUM -> "Advanced "
            DifficultyLevel.HARD -> "Critical "
            DifficultyLevel.EXPERT -> "Elite "
        }
        
        val title = "$difficultyPrefix$titlePrefix: $targetName"
        
        val description = when (type) {
            MissionType.TUTORIAL -> "Learn the basics of hacking in a safe training environment."
            MissionType.NETWORK_SCAN -> "Perform comprehensive reconnaissance of $targetName network architecture and security."
            MissionType.SERVER_BREACH -> "Gain unauthorized access to the $targetName systems and establish persistent control."
            MissionType.DATA_THEFT -> "Infiltrate $targetName and extract sensitive data without being detected."
            MissionType.VULNERABILITY -> "Identify and exploit vulnerabilities in $targetName systems."
            MissionType.PERSISTENCE -> "Establish and maintain persistent access to $targetName systems."
            MissionType.SOCIAL -> "Use social engineering techniques to gain access to $targetName systems."
            MissionType.FORENSIC -> "Analyze digital evidence from $targetName to uncover critical information."
            MissionType.STEALTH -> "Conduct operations within $targetName without detection."
            MissionType.CUSTOM_EXPLOIT -> "Develop and deploy a custom exploit against $targetName systems."
            MissionType.STORY -> "Progress through the main storyline by completing objectives in $targetName."
            MissionType.HACK -> "Gain unauthorized access to the $targetName systems and establish persistent control."
            MissionType.EXTRACT -> "Infiltrate $targetName and extract sensitive data without being detected."
            MissionType.PLANT -> "Infiltrate $targetName systems and deploy specialized software for future operations."
            MissionType.SCAN -> "Perform comprehensive reconnaissance of $targetName network architecture and security."
            MissionType.PROTECT -> "Defend $targetName systems from ongoing sophisticated cyber attacks."
            MissionType.ANALYZE -> "Analyze intercepted data from $targetName to extract actionable intelligence."
        }
        
        return Pair(title, description)
    }
    
    // Generate a random item reward based on difficulty
    private fun generateRandomItemReward(difficulty: DifficultyLevel): String {
        return when (difficulty) {
            DifficultyLevel.BEGINNER -> listOf(
                "starter_kit",
                "basic_scanner",
                "simple_tool"
            ).random()
            DifficultyLevel.EASY -> listOf(
                "basic_firewall",
                "simple_scanner",
                "crypto_tool_v1"
            ).random()
            DifficultyLevel.MEDIUM -> listOf(
                "advanced_firewall",
                "network_scanner_pro",
                "crypto_tool_v2"
            ).random()
            DifficultyLevel.HARD -> listOf(
                "quantum_firewall",
                "neural_scanner",
                "crypto_master"
            ).random()
            DifficultyLevel.EXPERT -> listOf(
                "ai_defense_suite",
                "quantum_analyzer",
                "neural_decryptor"
            ).random()
        }
    }
}

// Target system types
enum class TargetSystem {
    CORPORATE, // Corporate networks
    GOVERNMENT, // Government systems
    PERSONAL, // Personal computers
    RESEARCH, // Research institutes
    FINANCIAL, // Banks and financial institutions
    MILITARY, // Military networks
    INFRASTRUCTURE // Critical infrastructure
}

// Mission step - represents a single task in a mission
data class MissionStep(
    val description: String,
    val targetCommand: String? = null, // Command that completes this step
    val alternativeCommands: List<String> = listOf(), // Other commands that can complete this step
    val isComplete: Boolean = false
) 