package com.example.myapplication

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.myapplication.ObjectiveType // Import ObjectiveType

// Data Classes

enum class QuestStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED,
    FAILED
}

enum class NotificationType {
    QUEST_STARTED,
    QUEST_COMPLETED,
    OBJECTIVE_COMPLETED,
    LEVEL_UP,
    ACHIEVEMENT_UNLOCKED
}

// ObjectiveType enum removed - It's now imported from MissionSystem.kt

data class QuestObjective(
    val id: String,
    val description: String,
    val type: ObjectiveType, // Use imported ObjectiveType
    val targetPath: String = "",
    val targetServer: String = "",
    var status: QuestStatus = QuestStatus.NOT_STARTED,
    val rewardXp: Int = 10 // XP for completing this objective
)

data class Quest(
    val id: String,
    val title: String,
    val description: String,
    val objectives: List<QuestObjective>,
    val requiredLevel: Int = 1,
    val prerequisiteQuestIds: List<String> = listOf(),
    var status: QuestStatus = QuestStatus.NOT_STARTED,
    val rewardXp: Int = 50 // Base XP for completing the quest
)

data class PlayerStats(
    val level: Int = 1,
    val xp: Int = 0,
    val hackingSkill: Int = 1,
    val cryptoSkill: Int = 1,
    val socialEngSkill: Int = 1
)

data class Notification(
    val type: NotificationType,
    val message: String,
    val iconEmoji: String
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    var isUnlocked: Boolean = false
)

// Quest System State
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

// Type alias for compatibility
typealias QuestState = QuestSystemState

// Extension properties
val QuestState.playerLevel: Int
    get() = this.playerStats.level

val QuestState.notification: Notification?
    get() = this.notifications.firstOrNull()


// Default Quests and Achievements (Keep these functions from original)
fun defaultQuests(): List<Quest> {
    // ... (Implementation from original file - Assuming it exists) ...
    // Add placeholder if needed
    return listOf(
        Quest(
            id = "tutorial", title = "Tutorial", description = "Learn basics.",
            objectives = listOf(
                QuestObjective(id = "t1", description = "Do something", type = ObjectiveType.CUSTOM_COMMAND)
            )
        )
    )

}

fun defaultAchievements(): List<Achievement> {
    // ... (Implementation from original file - Assuming it exists) ...
     // Add placeholder if needed
    return listOf(
        Achievement(id = "a1", title = "First Step", description = "Completed Tutorial", iconEmoji = "✅")
    )
}


// QuestSystem Class
class QuestSystem {
    private val _state = MutableStateFlow(QuestSystemState())
    val state: StateFlow<QuestSystemState> = _state.asStateFlow()

    init {
        // Initialize state if needed
    }

    // --- Core QuestSystem Logic Methods ---
    // (Add back essential methods like startQuest, updateObjectiveStatus,
    //  checkObjectiveCompletion, dismissNotification, getActiveQuest, etc.
    //  based on how they were used in TerminalViewModel)

    fun startQuest(questId: String) {
        val currentState = _state.value
        val quest = currentState.quests.find { it.id == questId && it.status == QuestStatus.NOT_STARTED } ?: return

        // Check prerequisites (level, previous quests)
        if (currentState.playerStats.level < quest.requiredLevel) {
            // Optionally add notification: "Level requirement not met."
            return
        }
        if (!currentState.completedQuestIds.containsAll(quest.prerequisiteQuestIds)) {
             // Optionally add notification: "Prerequisite quests not completed."
            return
        }
        // Cannot start if another quest is active
        if (currentState.activeQuestId != null) {
             // Optionally add notification: "Complete your active quest first."
            return
        }


        val updatedQuests = currentState.quests.map {
            if (it.id == questId) it.copy(status = QuestStatus.IN_PROGRESS) else it
        }

         val notification = Notification(
            type = NotificationType.QUEST_STARTED,
            message = "Quest Started: ${quest.title}",
            iconEmoji = "🚀"
         )

        _state.update {
            it.copy(
                quests = updatedQuests,
                activeQuestId = questId,
                showQuestNotification = true,
                questNotification = notification.message,
                notificationType = notification.type,
                notificationIconEmoji = notification.iconEmoji,
                notifications = it.notifications + notification,
                showActiveQuestBanner = true // Show banner for new quest
            )
        }
    }

     fun updateObjectiveStatus(questId: String, objectiveId: String, newStatus: QuestStatus) {
        val currentState = _state.value
        val questIndex = currentState.quests.indexOfFirst { it.id == questId }
        if (questIndex == -1) return

        val quest = currentState.quests[questIndex]
        val objectiveIndex = quest.objectives.indexOfFirst { it.id == objectiveId }
        if (objectiveIndex == -1 || quest.objectives[objectiveIndex].status == newStatus) return // No change

        // --- Update Objective ---
        val updatedObjectives = quest.objectives.toMutableList()
        updatedObjectives[objectiveIndex] = updatedObjectives[objectiveIndex].copy(status = newStatus)

        // --- Determine Quest Status ---
        val allObjectivesCompleted = updatedObjectives.all { it.status == QuestStatus.COMPLETED }
        val questStatus = if (allObjectivesCompleted) QuestStatus.COMPLETED else QuestStatus.IN_PROGRESS

        // --- Update Quest ---
        val updatedQuests = currentState.quests.toMutableList()
        updatedQuests[questIndex] = quest.copy(
            objectives = updatedObjectives,
            status = questStatus
        )

        // --- Calculate Rewards & Stats (if quest completed) ---
        var updatedPlayerStats = currentState.playerStats
        val notifications = mutableListOf<Notification>()

        if (newStatus == QuestStatus.COMPLETED) {
             notifications.add(Notification(NotificationType.OBJECTIVE_COMPLETED, "Objective: ${updatedObjectives[objectiveIndex].description}", "✔️"))
        }


        if (questStatus == QuestStatus.COMPLETED && quest.status != QuestStatus.COMPLETED) {
            val objectiveXp = updatedObjectives.sumOf { it.rewardXp }
            val totalXpGained = quest.rewardXp + objectiveXp
            val currentTotalXp = currentState.playerStats.xp + totalXpGained
            val currentLevel = currentState.playerStats.level
            val newLevel = (currentTotalXp / 100) + 1 // Simple level up every 100 XP

            updatedPlayerStats = currentState.playerStats.copy(
                level = newLevel,
                xp = currentTotalXp
                // Add other stat increases if applicable
            )

             notifications.add(Notification(NotificationType.QUEST_COMPLETED, "Quest Completed: ${quest.title}! +${totalXpGained} XP", "🎉"))

            if (newLevel > currentLevel) {
                 notifications.add(Notification(NotificationType.LEVEL_UP, "Level Up! Reached Level $newLevel", "⬆️"))
                 // TODO: Award skill points? -> Requires interaction with SkillSystem
            }
        }

        // --- Update Completed Set & Active Quest ID ---
        val updatedCompletedIds = if (questStatus == QuestStatus.COMPLETED) {
            currentState.completedQuestIds + questId
        } else {
            currentState.completedQuestIds
        }
        val newActiveQuestId = if (questStatus == QuestStatus.COMPLETED) null else currentState.activeQuestId


        // --- Check Achievements ---
         val updatedAchievements = checkAchievements(currentState.achievements, updatedCompletedIds, updatedPlayerStats)
         val newlyUnlocked = updatedAchievements.filter { newAch ->
             !currentState.achievements.any { oldAch -> oldAch.id == newAch.id && oldAch.isUnlocked } && newAch.isUnlocked
         }
         newlyUnlocked.forEach { achievement ->
             notifications.add(Notification(NotificationType.ACHIEVEMENT_UNLOCKED, "Achievement: ${achievement.title}", achievement.iconEmoji))
         }


        // --- Update State ---
        val notificationToShow = notifications.lastOrNull()

        _state.update {
            it.copy(
                quests = updatedQuests,
                activeQuestId = newActiveQuestId,
                completedQuestIds = updatedCompletedIds,
                playerStats = updatedPlayerStats,
                achievements = updatedAchievements,
                showQuestNotification = notificationToShow != null,
                questNotification = notificationToShow?.message ?: "",
                notificationType = notificationToShow?.type ?: NotificationType.QUEST_STARTED,
                notificationIconEmoji = notificationToShow?.iconEmoji ?: "✅",
                notifications = it.notifications + notifications,
                showActiveQuestBanner = newActiveQuestId != null // Hide banner if no active quest
            )
        }

         // Auto-start next quest if current one completed
         if (questStatus == QuestStatus.COMPLETED) {
             // Simplified: find first available quest (needs proper logic)
             val nextQuest = getAvailableQuests().firstOrNull { nextQ -> nextQ.id != questId } // Basic next logic
             nextQuest?.let { startQuest(it.id) }
         }

    }

      fun checkObjectiveCompletion(
        action: ObjectiveType,
        targetPath: String = "",
        targetServer: String = ""
        // Add other relevant parameters like command used, result, etc.
    ) {
        val currentState = _state.value
        val activeQuestId = currentState.activeQuestId ?: return
        val quest = currentState.quests.find { it.id == activeQuestId } ?: return

        quest.objectives.forEach { objective ->
            if (objective.status != QuestStatus.COMPLETED && objective.type == action) {
                var completed = false
                 when (objective.type) {
                    ObjectiveType.READ_FILE, ObjectiveType.FIND_FILE -> {
                        if (targetPath == objective.targetPath) completed = true
                    }
                    ObjectiveType.CONNECT_TO_SERVER -> {
                         if (targetServer == objective.targetServer) completed = true
                    }
                     // Add cases for other ObjectiveTypes
                     ObjectiveType.SCAN_NETWORK, // Needs condition
                     ObjectiveType.CRACK_PASSWORD, // Needs condition (e.g., targetPath match + success flag)
                     ObjectiveType.ESCALATE_PRIVILEGES -> { // No target needed, just the action itself
                          completed = true
                     }
                    // ... other types
                     else -> { /* Handle other types or ignore */ }
                 }

                 if (completed) {
                     updateObjectiveStatus(activeQuestId, objective.id, QuestStatus.COMPLETED)
                 }
            }
        }
    }

     fun dismissNotification() {
        val currentNotifications = _state.value.notifications
        if (currentNotifications.isNotEmpty()) {
             _state.update {
                 it.copy(
                     notifications = currentNotifications.drop(1), // Remove the first notification
                     showQuestNotification = currentNotifications.size > 1 // Show next if available
                     // Optionally update the displayed notification details based on the new first notification
                 )
             }
        } else {
             _state.update { it.copy(showQuestNotification = false, questNotification = "") }
        }
     }


     fun getActiveQuest(): Quest? {
        return _state.value.quests.find { it.id == _state.value.activeQuestId }
    }

     fun getQuestById(questId: String): Quest? {
        return _state.value.quests.find { it.id == questId }
    }

    fun getAvailableQuests(): List<Quest> {
         val currentState = _state.value
         return currentState.quests.filter { quest ->
             quest.status == QuestStatus.NOT_STARTED &&
             currentState.playerStats.level >= quest.requiredLevel &&
             currentState.completedQuestIds.containsAll(quest.prerequisiteQuestIds)
         }
     }

      fun getAllAchievements(): List<Achievement> {
        return _state.value.achievements
    }

     // Helper to check achievements
     private fun checkAchievements(currentAchievements: List<Achievement>, completedIds: Set<String>, stats: PlayerStats): List<Achievement> {
          return currentAchievements.map { ach ->
              if (ach.isUnlocked) ach // Already unlocked
              else {
                  val unlocked = when (ach.id) {
                      "first_steps" -> completedIds.contains("tutorial")
                      "web_infiltrator" -> completedIds.contains("web_server_hack") // Example IDs
                      "data_thief" -> completedIds.contains("db_server_hack") // Example IDs
                      "level_5" -> stats.level >= 5
                      "skilled_hacker" -> stats.hackingSkill >= 3
                      "master_hacker" -> completedIds.size == _state.value.quests.count { it.id != "tutorial" } // Example completion logic
                       // Add other achievement checks
                      else -> false
                  }
                  ach.copy(isUnlocked = unlocked)
              }
          }
      }

     // ... other methods as needed ...

} 