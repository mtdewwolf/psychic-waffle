package com.example.myapplication

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Quest statuses
enum class QuestStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED,
    FAILED
}

// Quest objective types
enum class ObjectiveType {
    SCAN_NETWORK,
    CONNECT_TO_SERVER,
    CRACK_PASSWORD,
    FIND_FILE,
    READ_FILE,
    ESCALATE_PRIVILEGES
}

// A single objective within a quest
data class QuestObjective(
    val id: String,
    val description: String,
    val type: ObjectiveType,
    val targetPath: String = "",
    val targetServer: String = "",
    val status: QuestStatus = QuestStatus.NOT_STARTED,
    val rewardXp: Int = 50
)

// A complete quest with multiple objectives
data class Quest(
    val id: String,
    val title: String,
    val description: String,
    val objectives: List<QuestObjective>,
    val status: QuestStatus = QuestStatus.NOT_STARTED,
    val rewardXp: Int = 100,
    val requiredLevel: Int = 1,
    val prerequisiteQuestIds: List<String> = listOf()
)

// Player stats
data class PlayerStats(
    val level: Int = 1,
    val xp: Int = 0,
    val hackingSkill: Int = 1,
    val cryptoSkill: Int = 1,
    val socialEngSkill: Int = 1
)

// Achievements 
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val isUnlocked: Boolean = false,
    val xpReward: Int = 50,
    val iconEmoji: String = "🏆"
)

// Notification types
enum class NotificationType {
    QUEST_STARTED,
    OBJECTIVE_COMPLETED,
    QUEST_COMPLETED,
    ACHIEVEMENT_UNLOCKED,
    LEVEL_UP
}

// Notification
data class Notification(
    val type: NotificationType,
    val message: String,
    val iconEmoji: String
)

// Quest system state
data class QuestSystemState(
    val quests: List<Quest> = defaultQuests(),
    val activeQuestId: String? = null,
    val completedQuestIds: Set<String> = setOf(),
    val playerStats: PlayerStats = PlayerStats(),
    val showQuestNotification: Boolean = false,
    val questNotification: String = "",
    val notificationType: NotificationType = NotificationType.QUEST_STARTED,
    val notificationIconEmoji: String = "📋",
    val achievements: List<Achievement> = defaultAchievements(),
    val notifications: List<Notification> = listOf(),
    val showActiveQuestBanner: Boolean = true
)

// Define default quests
fun defaultQuests(): List<Quest> {
    return listOf(
        // Tutorial quest
        Quest(
            id = "tutorial",
            title = "Learning the Ropes",
            description = "Learn the basics of hacking by exploring your system and scanning the network.",
            objectives = listOf(
                QuestObjective(
                    id = "tutorial_1",
                    description = "Read the hacking guide in your documents folder",
                    type = ObjectiveType.READ_FILE,
                    targetPath = "/home/user/documents/hacking_guide.txt"
                ),
                QuestObjective(
                    id = "tutorial_2",
                    description = "Scan the network for potential targets",
                    type = ObjectiveType.SCAN_NETWORK
                )
            )
        ),
        
        // First real mission
        Quest(
            id = "web_server_hack",
            title = "Web Server Infiltration",
            description = "Hack into a vulnerable web server and find the user flag.",
            requiredLevel = 1,
            prerequisiteQuestIds = listOf("tutorial"),
            objectives = listOf(
                QuestObjective(
                    id = "web_1",
                    description = "Connect to the web server at 192.168.1.105",
                    type = ObjectiveType.CONNECT_TO_SERVER,
                    targetServer = "192.168.1.105"
                ),
                QuestObjective(
                    id = "web_2",
                    description = "Crack the admin password",
                    type = ObjectiveType.CRACK_PASSWORD,
                    targetPath = "/etc/shadow"
                ),
                QuestObjective(
                    id = "web_3",
                    description = "Find the user flag in the admin's home directory",
                    type = ObjectiveType.READ_FILE,
                    targetPath = "/home/admin/user.txt"
                ),
                QuestObjective(
                    id = "web_4",
                    description = "Find database credentials in the website config",
                    type = ObjectiveType.READ_FILE,
                    targetPath = "/var/www/html/config.php"
                )
            )
        ),
        
        // Second mission
        Quest(
            id = "db_server_hack",
            title = "Database Server Breach",
            description = "Gain access to the database server and steal customer records.",
            requiredLevel = 2,
            prerequisiteQuestIds = listOf("web_server_hack"),
            objectives = listOf(
                QuestObjective(
                    id = "db_1",
                    description = "Connect to the database server at 192.168.1.240",
                    type = ObjectiveType.CONNECT_TO_SERVER,
                    targetServer = "192.168.1.240"
                ),
                QuestObjective(
                    id = "db_2",
                    description = "Crack the admin password",
                    type = ObjectiveType.CRACK_PASSWORD,
                    targetPath = "/etc/shadow"
                ),
                QuestObjective(
                    id = "db_3",
                    description = "Find the admin's notes with password hints",
                    type = ObjectiveType.READ_FILE,
                    targetPath = "/home/admin/notes.txt"
                ),
                QuestObjective(
                    id = "db_4",
                    description = "Escalate to root privileges",
                    type = ObjectiveType.ESCALATE_PRIVILEGES
                ),
                QuestObjective(
                    id = "db_5",
                    description = "Extract customer data from the database",
                    type = ObjectiveType.READ_FILE,
                    targetPath = "/var/lib/mysql/database.sql"
                )
            )
        ),
        
        // Final mission
        Quest(
            id = "personal_data_theft",
            title = "Personal Data Theft",
            description = "Steal personal information from a home computer.",
            requiredLevel = 3,
            prerequisiteQuestIds = listOf("db_server_hack"),
            objectives = listOf(
                QuestObjective(
                    id = "personal_1",
                    description = "Connect to the personal computer at 192.168.1.12",
                    type = ObjectiveType.CONNECT_TO_SERVER,
                    targetServer = "192.168.1.12"
                ),
                QuestObjective(
                    id = "personal_2",
                    description = "Crack the user password",
                    type = ObjectiveType.CRACK_PASSWORD,
                    targetPath = "/etc/shadow"
                ),
                QuestObjective(
                    id = "personal_3",
                    description = "Find personal credentials in documents",
                    type = ObjectiveType.READ_FILE,
                    targetPath = "/home/user/documents/passwords.txt"
                )
            )
        )
    )
}

// Define default achievements
fun defaultAchievements(): List<Achievement> {
    return listOf(
        Achievement(
            id = "master_hacker",
            title = "Master Hacker",
            description = "Complete all quests in the game",
            iconEmoji = "🔓"
        ),
        Achievement(
            id = "level_5",
            title = "Leveled Up",
            description = "Reach level 5",
            iconEmoji = "⬆️"
        ),
        Achievement(
            id = "skilled_hacker",
            title = "Skilled Hacker",
            description = "Reach hacking skill level 3",
            iconEmoji = "💻"
        ),
        Achievement(
            id = "first_steps",
            title = "First Steps",
            description = "Complete the tutorial",
            iconEmoji = "🔰"
        ),
        Achievement(
            id = "web_infiltrator",
            title = "Web Infiltrator",
            description = "Hack the web server",
            iconEmoji = "🌐"
        ),
        Achievement(
            id = "data_thief",
            title = "Data Thief",
            description = "Steal database records",
            iconEmoji = "💾"
        ),
        Achievement(
            id = "privacy_invader",
            title = "Privacy Invader",
            description = "Access personal data",
            iconEmoji = "🔍"
        ),
        Achievement(
            id = "network_explorer",
            title = "Network Explorer",
            description = "Discover at least 3 targets",
            iconEmoji = "🔭"
        )
    )
}

class QuestSystem {
    private val _state = MutableStateFlow(QuestSystemState())
    val state: StateFlow<QuestSystemState> = _state.asStateFlow()
    
    // Get available quests that can be started
    fun getAvailableQuests(): List<Quest> {
        val currentState = _state.value
        return currentState.quests.filter { quest ->
            val levelRequirementMet = currentState.playerStats.level >= quest.requiredLevel
            val prerequisitesMet = quest.prerequisiteQuestIds.all { 
                currentState.completedQuestIds.contains(it) 
            }
            val notStarted = quest.status == QuestStatus.NOT_STARTED
            val notCompleted = !currentState.completedQuestIds.contains(quest.id)
            
            levelRequirementMet && prerequisitesMet && notStarted && notCompleted
        }
    }
    
    // Start a quest
    fun startQuest(questId: String) {
        val currentState = _state.value
        val questIndex = currentState.quests.indexOfFirst { it.id == questId }
        
        if (questIndex == -1) return
        
        val updatedQuests = currentState.quests.toMutableList()
        updatedQuests[questIndex] = updatedQuests[questIndex].copy(status = QuestStatus.IN_PROGRESS)
        
        // Create notification
        val quest = updatedQuests[questIndex]
        val notification = Notification(
            type = NotificationType.QUEST_STARTED,
            message = "Quest started: ${quest.title}",
            iconEmoji = "📋"
        )
        
        _state.value = currentState.copy(
            quests = updatedQuests,
            activeQuestId = questId,
            showQuestNotification = true,
            questNotification = "Quest started: ${updatedQuests[questIndex].title}",
            notificationType = NotificationType.QUEST_STARTED,
            notificationIconEmoji = "📋",
            notifications = currentState.notifications + notification,
            showActiveQuestBanner = true
        )
    }
    
    // Update objective status
    fun updateObjectiveStatus(questId: String, objectiveId: String, status: QuestStatus) {
        val currentState = _state.value
        val questIndex = currentState.quests.indexOfFirst { it.id == questId }
        
        if (questIndex == -1) return
        
        val quest = currentState.quests[questIndex]
        val objectiveIndex = quest.objectives.indexOfFirst { it.id == objectiveId }
        
        if (objectiveIndex == -1) return
        
        // Create updated objectives list
        val updatedObjectives = quest.objectives.toMutableList()
        updatedObjectives[objectiveIndex] = updatedObjectives[objectiveIndex].copy(status = status)
        
        // Check if all objectives are completed
        val allCompleted = updatedObjectives.all { it.status == QuestStatus.COMPLETED }
        val questStatus = if (allCompleted) QuestStatus.COMPLETED else quest.status
        
        // Create updated quests list
        val updatedQuests = currentState.quests.toMutableList()
        updatedQuests[questIndex] = quest.copy(
            objectives = updatedObjectives,
            status = questStatus
        )
        
        // Calculate rewards if quest just completed
        val updatedPlayerStats = if (questStatus == QuestStatus.COMPLETED && quest.status != QuestStatus.COMPLETED) {
            // Add quest XP and objective XP
            val totalXp = currentState.playerStats.xp + quest.rewardXp + 
                    updatedObjectives.filter { it.status == QuestStatus.COMPLETED }
                        .sumOf { it.rewardXp }
            
            // Calculate new level (every 100 XP = 1 level)
            val newLevel = (totalXp / 100) + 1
            
            // Update player stats
            currentState.playerStats.copy(
                level = newLevel,
                xp = totalXp,
                hackingSkill = currentState.playerStats.hackingSkill + 1
            )
        } else {
            currentState.playerStats
        }
        
        // Update completed quests set if needed
        val updatedCompletedQuestIds = if (questStatus == QuestStatus.COMPLETED) {
            currentState.completedQuestIds + questId
        } else {
            currentState.completedQuestIds
        }
        
        // Calculate which achievements to unlock
        val updatedAchievements = currentState.achievements.map { achievement ->
            when (achievement.id) {
                "first_steps" -> achievement.copy(isUnlocked = updatedCompletedQuestIds.contains("tutorial"))
                "web_infiltrator" -> achievement.copy(isUnlocked = updatedCompletedQuestIds.contains("web_server_hack"))
                "data_thief" -> achievement.copy(isUnlocked = updatedCompletedQuestIds.contains("db_server_hack"))
                "privacy_invader" -> achievement.copy(isUnlocked = updatedCompletedQuestIds.contains("personal_data_theft"))
                "master_hacker" -> achievement.copy(isUnlocked = 
                    updatedCompletedQuestIds.containsAll(listOf("tutorial", "web_server_hack", "db_server_hack", "personal_data_theft")))
                "level_5" -> achievement.copy(isUnlocked = updatedPlayerStats.level >= 5)
                "skilled_hacker" -> achievement.copy(isUnlocked = updatedPlayerStats.hackingSkill >= 3)
                "network_explorer" -> achievement.copy(isUnlocked = true)
                else -> achievement
            }
        }
        
        // Find newly unlocked achievements
        val newlyUnlockedAchievements = updatedAchievements.zip(currentState.achievements)
            .filter { (updated, current) -> updated.isUnlocked && !current.isUnlocked }
        
        // Create notifications
        val notifications = mutableListOf<Notification>()
        
        // Objective completion notification
        if (status == QuestStatus.COMPLETED) {
            notifications.add(
                Notification(
                    type = NotificationType.OBJECTIVE_COMPLETED,
                    message = "Objective completed: ${updatedObjectives[objectiveIndex].description}",
                    iconEmoji = "✅"
                )
            )
        }
        
        // Quest completion notification
        if (questStatus == QuestStatus.COMPLETED && quest.status != QuestStatus.COMPLETED) {
            notifications.add(
                Notification(
                    type = NotificationType.QUEST_COMPLETED,
                    message = "Quest completed: ${quest.title}! +${quest.rewardXp} XP",
                    iconEmoji = "🎉"
                )
            )
        }
        
        // Level up notification
        if (updatedPlayerStats.level > currentState.playerStats.level) {
            notifications.add(
                Notification(
                    type = NotificationType.LEVEL_UP,
                    message = "Level up! You're now level ${updatedPlayerStats.level}",
                    iconEmoji = "⬆️"
                )
            )
        }
        
        // Add achievement notifications
        newlyUnlockedAchievements.forEach { (achievement, _) ->
            notifications.add(
                Notification(
                    type = NotificationType.ACHIEVEMENT_UNLOCKED,
                    message = "Achievement unlocked: ${achievement.title}",
                    iconEmoji = achievement.iconEmoji
                )
            )
        }
        
        // Determine notification to show (prioritize achievements and level ups)
        val notificationToShow = notifications.lastOrNull() ?: return
        
        // Update state with everything
        _state.value = currentState.copy(
            quests = updatedQuests,
            playerStats = updatedPlayerStats,
            completedQuestIds = updatedCompletedQuestIds,
            achievements = updatedAchievements,
            showQuestNotification = notifications.isNotEmpty(),
            questNotification = notificationToShow.message,
            notificationType = notificationToShow.type,
            notificationIconEmoji = notificationToShow.iconEmoji,
            notifications = currentState.notifications + notifications,
            // Hide quest banner if quest is completed (for a cleaner UI)
            showActiveQuestBanner = !(questStatus == QuestStatus.COMPLETED && questId == currentState.activeQuestId)
        )
        
        // If the active quest was completed, automatically set the next available quest as active
        if (questStatus == QuestStatus.COMPLETED && questId == currentState.activeQuestId) {
            val nextAvailableQuest = getAvailableQuests().firstOrNull()
            if (nextAvailableQuest != null) {
                startQuest(nextAvailableQuest.id)
            }
        }
    }
    
    // Check if an action completes an objective
    fun checkObjectiveCompletion(
        action: ObjectiveType,
        targetPath: String = "",
        targetServer: String = ""
    ) {
        val currentState = _state.value
        val activeQuestId = currentState.activeQuestId ?: return
        
        val quest = currentState.quests.find { it.id == activeQuestId } ?: return
        
        // Check each objective in the active quest
        for (objective in quest.objectives) {
            if (objective.status != QuestStatus.COMPLETED && objective.type == action) {
                // For objectives with paths, check if path matches
                if (objective.type == ObjectiveType.READ_FILE || 
                    objective.type == ObjectiveType.FIND_FILE) {
                    if (targetPath == objective.targetPath) {
                        updateObjectiveStatus(activeQuestId, objective.id, QuestStatus.COMPLETED)
                    }
                } 
                // For server connection objectives
                else if (objective.type == ObjectiveType.CONNECT_TO_SERVER) {
                    if (targetServer == objective.targetServer) {
                        updateObjectiveStatus(activeQuestId, objective.id, QuestStatus.COMPLETED)
                    }
                }
                // For other types of objectives
                else {
                    updateObjectiveStatus(activeQuestId, objective.id, QuestStatus.COMPLETED)
                }
            }
        }
    }
    
    // Dismiss the current notification
    fun dismissNotification() {
        _state.value = _state.value.copy(
            showQuestNotification = false,
            questNotification = ""
        )
    }
    
    // Get active quest details
    fun getActiveQuest(): Quest? {
        val currentState = _state.value
        return currentState.quests.find { it.id == currentState.activeQuestId }
    }
    
    // Get quest by ID
    fun getQuestById(questId: String): Quest? {
        return _state.value.quests.find { it.id == questId }
    }
    
    // This function is used by the achievements screen
    fun getAllAchievements(): List<Achievement> {
        return _state.value.achievements
    }
} 