package com.example.myapplication

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Skill categories
enum class SkillCategory {
    NETWORK,        // Network infrastructure hacking
    WEB,            // Web application exploitation
    SOCIAL,         // Social engineering
    CRYPTOGRAPHY,   // Encryption and code breaking
    REVERSE,        // Reverse engineering
    STEALTH,        // Avoiding detection
    MALWARE,        // Creating and using malware
    FORENSICS       // Digital forensics
}

// Type alias to maintain compatibility with TerminalViewModel
typealias SkillSystemState = SkillTreeState

// Skill data class representing a specific skill
data class Skill(
    val id: String,
    val name: String,
    val description: String,
    val category: SkillCategory,
    val maxLevel: Int = 5,
    val requiredSkills: List<String> = listOf(), // Prerequisite skills
    val unlockLevel: Int = 1, // Player level required to learn this skill
    val unlockXp: Int = 0, // XP cost to learn/level up this skill
    val effects: List<SkillEffect> = listOf() // Effects that this skill provides
)

// Skill effects apply gameplay bonuses
data class SkillEffect(
    val type: SkillEffectType,
    val value: Float, // Percentage/multiplier value of the effect
    val scope: String = "" // Optional scope for specific contexts
)

// Types of effects that skills can provide
enum class SkillEffectType {
    HACKING_SPEED,
    HACKING_SUCCESS,
    STEALTH,
    DEFENSE,
    VIRUS_STRENGTH,
    SCAN_SPEED,
    SCAN_DEPTH,
    CREDIT_BONUS,
    XP_BONUS,
    UNLOCK_ABILITY,
    CRACK_SPEED,        // Increase password cracking speed
    SCAN_DETAIL,        // More detailed scan results
    DETECTION_CHANCE,   // Reduce chance of being detected
    FIREWALL_BYPASS,    // Ease of bypassing firewalls
    SERVICE_EXPLOIT,    // Better service exploitation
    TRACE_REDUCTION,    // Reduce trace chance
    DATA_EXFIL_SPEED,   // Speed up data exfiltration
    ENCRYPTION_BOOST,   // Better encryption/decryption
    VULN_DISCOVERY,     // Better vulnerability discovery
    PERSISTENCE,        // Better persistence after compromise
    SOCIAL_SUCCESS,     // Social engineering success chance
    SCRIPT_EFFICIENCY,  // Improve script efficiency
    IDS_EVASION         // Better IDS evasion
}

// Player's skill tree state
data class SkillTreeState(
    val skills: Map<String, PlayerSkill> = defaultSkills(),
    val availableSkillPoints: Int = 0,
    val unlockedCategories: Set<SkillCategory> = setOf(SkillCategory.NETWORK, SkillCategory.SOCIAL) // Starting categories
)

// Player's version of a skill with current level
data class PlayerSkill(
    val skill: Skill,
    val currentLevel: Int = 0, // 0 = not learned
    val isUnlocked: Boolean = false // Whether this skill is available to learn
)

// Default skill definitions
fun defaultSkills(): Map<String, PlayerSkill> {
    val skills = mutableMapOf<String, PlayerSkill>()
    
    // Network skills
    skills["network_scanning"] = PlayerSkill(
        Skill(
            id = "network_scanning",
            name = "Network Scanning",
            description = "Improve your ability to discover network devices and services.",
            category = SkillCategory.NETWORK,
            effects = listOf(
                SkillEffect(SkillEffectType.SCAN_DETAIL, 0.2f)
            )
        ),
        isUnlocked = true // Starting skill
    )
    
    skills["port_analysis"] = PlayerSkill(
        Skill(
            id = "port_analysis",
            name = "Port Analysis",
            description = "Better identify open ports and running services.",
            category = SkillCategory.NETWORK,
            requiredSkills = listOf("network_scanning"),
            unlockLevel = 2,
            effects = listOf(
                SkillEffect(SkillEffectType.VULN_DISCOVERY, 0.15f, "network")
            )
        )
    )
    
    skills["packet_analysis"] = PlayerSkill(
        Skill(
            id = "packet_analysis",
            name = "Packet Analysis",
            description = "Analyze network traffic to extract useful information.",
            category = SkillCategory.NETWORK,
            requiredSkills = listOf("port_analysis"),
            unlockLevel = 3,
            effects = listOf(
                SkillEffect(SkillEffectType.DATA_EXFIL_SPEED, 0.2f),
                SkillEffect(SkillEffectType.ENCRYPTION_BOOST, 0.1f)
            )
        )
    )
    
    skills["firewall_tactics"] = PlayerSkill(
        Skill(
            id = "firewall_tactics",
            name = "Firewall Tactics",
            description = "Advanced techniques for bypassing network firewalls.",
            category = SkillCategory.NETWORK,
            requiredSkills = listOf("packet_analysis"),
            unlockLevel = 5,
            effects = listOf(
                SkillEffect(SkillEffectType.FIREWALL_BYPASS, 0.25f)
            )
        )
    )
    
    // Web skills
    skills["web_recon"] = PlayerSkill(
        Skill(
            id = "web_recon",
            name = "Web Reconnaissance",
            description = "Gather information from web applications and servers.",
            category = SkillCategory.WEB,
            unlockLevel = 2,
            effects = listOf(
                SkillEffect(SkillEffectType.VULN_DISCOVERY, 0.15f, "web")
            )
        )
    )
    
    skills["injection_techniques"] = PlayerSkill(
        Skill(
            id = "injection_techniques",
            name = "Injection Techniques",
            description = "Master SQL, XSS, and other injection vulnerabilities.",
            category = SkillCategory.WEB,
            requiredSkills = listOf("web_recon"),
            unlockLevel = 3,
            effects = listOf(
                SkillEffect(SkillEffectType.SERVICE_EXPLOIT, 0.25f, "web")
            )
        )
    )
    
    // Social engineering skills
    skills["social_recon"] = PlayerSkill(
        Skill(
            id = "social_recon",
            name = "Social Reconnaissance",
            description = "Gather information about targets through social channels.",
            category = SkillCategory.SOCIAL,
            unlockLevel = 1,
            effects = listOf(
                SkillEffect(SkillEffectType.SOCIAL_SUCCESS, 0.15f)
            )
        ),
        isUnlocked = true // Starting skill
    )
    
    skills["phishing"] = PlayerSkill(
        Skill(
            id = "phishing",
            name = "Phishing Techniques",
            description = "Create convincing phishing attacks to steal credentials.",
            category = SkillCategory.SOCIAL,
            requiredSkills = listOf("social_recon"),
            unlockLevel = 2,
            effects = listOf(
                SkillEffect(SkillEffectType.SOCIAL_SUCCESS, 0.2f, "phishing")
            )
        )
    )
    
    // Cryptography skills
    skills["crypto_basics"] = PlayerSkill(
        Skill(
            id = "crypto_basics",
            name = "Cryptography Basics",
            description = "Learn the foundations of cryptographic systems.",
            category = SkillCategory.CRYPTOGRAPHY,
            unlockLevel = 3,
            effects = listOf(
                SkillEffect(SkillEffectType.ENCRYPTION_BOOST, 0.15f)
            )
        )
    )
    
    skills["hash_cracking"] = PlayerSkill(
        Skill(
            id = "hash_cracking",
            name = "Hash Cracking",
            description = "Break password hashes more efficiently.",
            category = SkillCategory.CRYPTOGRAPHY,
            requiredSkills = listOf("crypto_basics"),
            unlockLevel = 4,
            effects = listOf(
                SkillEffect(SkillEffectType.CRACK_SPEED, 0.25f)
            )
        )
    )
    
    // Reverse engineering skills
    skills["binary_analysis"] = PlayerSkill(
        Skill(
            id = "binary_analysis",
            name = "Binary Analysis",
            description = "Analyze compiled code to find vulnerabilities.",
            category = SkillCategory.REVERSE,
            unlockLevel = 5,
            effects = listOf(
                SkillEffect(SkillEffectType.VULN_DISCOVERY, 0.2f, "binary")
            )
        )
    )
    
    // Stealth skills
    skills["cover_tracks"] = PlayerSkill(
        Skill(
            id = "cover_tracks",
            name = "Cover Your Tracks",
            description = "Hide evidence of your activities.",
            category = SkillCategory.STEALTH,
            unlockLevel = 3,
            effects = listOf(
                SkillEffect(SkillEffectType.DETECTION_CHANCE, -0.2f)
            )
        )
    )
    
    skills["ids_evasion"] = PlayerSkill(
        Skill(
            id = "ids_evasion",
            name = "IDS Evasion",
            description = "Advanced techniques to avoid intrusion detection systems.",
            category = SkillCategory.STEALTH,
            requiredSkills = listOf("cover_tracks"),
            unlockLevel = 6,
            effects = listOf(
                SkillEffect(SkillEffectType.IDS_EVASION, 0.3f)
            )
        )
    )
    
    // Malware skills
    skills["script_creation"] = PlayerSkill(
        Skill(
            id = "script_creation",
            name = "Script Creation",
            description = "Write effective hacking scripts.",
            category = SkillCategory.MALWARE,
            unlockLevel = 4,
            effects = listOf(
                SkillEffect(SkillEffectType.SCRIPT_EFFICIENCY, 0.2f)
            )
        )
    )
    
    skills["backdoor_implantation"] = PlayerSkill(
        Skill(
            id = "backdoor_implantation",
            name = "Backdoor Implantation",
            description = "Create and deploy effective backdoors.",
            category = SkillCategory.MALWARE,
            requiredSkills = listOf("script_creation"),
            unlockLevel = 5,
            effects = listOf(
                SkillEffect(SkillEffectType.PERSISTENCE, 0.25f)
            )
        )
    )
    
    // Forensics skills
    skills["log_analysis"] = PlayerSkill(
        Skill(
            id = "log_analysis",
            name = "Log Analysis",
            description = "Analyze system logs to find useful information.",
            category = SkillCategory.FORENSICS,
            unlockLevel = 3,
            effects = listOf(
                SkillEffect(SkillEffectType.VULN_DISCOVERY, 0.15f, "logs")
            )
        )
    )
    
    skills["data_recovery"] = PlayerSkill(
        Skill(
            id = "data_recovery",
            name = "Data Recovery",
            description = "Recover deleted or corrupted files.",
            category = SkillCategory.FORENSICS,
            requiredSkills = listOf("log_analysis"),
            unlockLevel = 4,
            effects = listOf(
                SkillEffect(SkillEffectType.DATA_EXFIL_SPEED, 0.2f, "recovery")
            )
        )
    )
    
    return skills
}

class SkillSystem {
    private val _state = MutableStateFlow(SkillTreeState())
    val state: StateFlow<SkillTreeState> = _state.asStateFlow()
    
    // Add skill points when player levels up
    fun addSkillPoints(points: Int) {
        _state.update { 
            it.copy(availableSkillPoints = it.availableSkillPoints + points) 
        }
        updateSkillAvailability()
    }
    
    // Unlock a category when certain conditions are met
    fun unlockCategory(category: SkillCategory) {
        _state.update { 
            it.copy(unlockedCategories = it.unlockedCategories + category) 
        }
        updateSkillAvailability()
    }
    
    // Spend points to learn or upgrade a skill
    fun learnSkill(skillId: String, playerLevel: Int): Boolean {
        val currentState = _state.value
        val playerSkill = currentState.skills[skillId] ?: return false
        
        // Check if skill can be learned/upgraded
        if (!playerSkill.isUnlocked) return false
        if (playerSkill.currentLevel >= playerSkill.skill.maxLevel) return false
        if (playerLevel < playerSkill.skill.unlockLevel) return false
        if (currentState.availableSkillPoints < playerSkill.skill.unlockXp) return false
        
        // Upgrade the skill
        val updatedSkills = currentState.skills.toMutableMap()
        updatedSkills[skillId] = playerSkill.copy(
            currentLevel = playerSkill.currentLevel + 1
        )
        
        _state.update { 
            it.copy(
                skills = updatedSkills,
                availableSkillPoints = it.availableSkillPoints - playerSkill.skill.unlockXp
            ) 
        }
        
        updateSkillAvailability()
        return true
    }
    
    // Check skill level
    fun getSkillLevel(skillId: String): Int {
        return state.value.skills[skillId]?.currentLevel ?: 0
    }
    
    // Get all skills in a category
    fun getSkillsByCategory(category: SkillCategory): List<PlayerSkill> {
        return state.value.skills
            .filter { it.value.skill.category == category }
            .map { it.value }
    }
    
    // Get all unlocked skills
    fun getUnlockedSkills(): List<PlayerSkill> {
        return state.value.skills
            .filter { it.value.isUnlocked }
            .map { it.value }
    }
    
    // Get the total effect value for a specific effect type
    fun getEffectValue(effectType: SkillEffectType, scope: String = ""): Float {
        var totalValue = 0.0f
        
        state.value.skills.values.forEach { playerSkill ->
            if (playerSkill.currentLevel > 0) {
                playerSkill.skill.effects.forEach { effect ->
                    if (effect.type == effectType && (scope.isEmpty() || effect.scope == scope)) {
                        // Apply effect based on skill level (scales with level)
                        totalValue += effect.value * playerSkill.currentLevel / playerSkill.skill.maxLevel
                    }
                }
            }
        }
        
        return totalValue
    }
    
    // Update which skills are available to learn based on prerequisites and unlocked categories
    private fun updateSkillAvailability() {
        val currentState = _state.value
        val updatedSkills = currentState.skills.toMutableMap()
        
        updatedSkills.forEach { (skillId, playerSkill) ->
            val skill = playerSkill.skill
            
            // Check if this skill's category is unlocked
            val categoryUnlocked = skill.category in currentState.unlockedCategories
            
            // Check if prerequisites are met
            val prerequisitesMet = skill.requiredSkills.all { 
                (currentState.skills[it]?.currentLevel ?: 0) > 0 
            }
            
            // Update skill unlocked status
            updatedSkills[skillId] = playerSkill.copy(
                isUnlocked = categoryUnlocked && prerequisitesMet
            )
        }
        
        _state.update { it.copy(skills = updatedSkills) }
    }
    
    // Reset the skill tree
    fun resetSkills() {
        _state.update { SkillTreeState() }
        updateSkillAvailability()
    }
} 