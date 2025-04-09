package com.example.myapplication

import java.util.UUID

// Network node types
enum class NodeType {
    ROUTER,
    FIREWALL,
    WEBSERVER,
    FILESERVER,
    DATABASE,
    WORKSTATION,
    IDS, // Intrusion Detection System
    MAILSERVER,
    DOMAINCONTROLLER
}

// Security levels determine difficulty
enum class SecurityLevel {
    LOW,
    MEDIUM,
    HIGH,
    EXTREME
}

// Network segments represent isolated parts of a network
enum class NetworkSegment {
    INTERNET,
    DMZ,
    CORPORATE,
    DEVELOPMENT,
    ADMIN,
    RESTRICTED,
    IOT,
    GOVERNMENT,
    PERSONAL,
    RESEARCH,
    FINANCIAL,
    MILITARY,
    INFRASTRUCTURE
}

// Connection types between nodes
enum class ConnectionType {
    DIRECT, // Direct connection, no restrictions
    FILTERED, // Some ports blocked
    RESTRICTED, // Most ports blocked, needs specific authentication
    ISOLATED // Only accessible after specific conditions are met
}

// Security measures implemented on nodes
data class SecurityMeasures(
    val hasFirewall: Boolean = false,
    val hasIDS: Boolean = false,
    val requiresAuthentication: Boolean = false,
    val maxFailedLogins: Int = 3,
    val passwordComplexity: Int = 1, // 1-5, affects difficulty of cracking
    val patchLevel: Int = 3, // 1-5, lower means more vulnerabilities
    val encryptionLevel: Int = 1 // 1-5, affects difficulty of data exfiltration
)

// Services running on a node
data class NodeService(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val port: Int,
    val version: String,
    val hasVulnerability: Boolean = false,
    val vulnerabilityDetails: String = "",
    val isRunning: Boolean = true
)

// Represents a node in the network
data class NetworkNode(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val ipAddress: String,
    val type: NodeType,
    val segment: NetworkSegment,
    val securityLevel: SecurityLevel,
    val services: MutableList<NodeService> = mutableListOf(),
    val securityMeasures: SecurityMeasures = SecurityMeasures(),
    val isDiscovered: Boolean = false,
    val isCompromised: Boolean = false,
    val compromiseLevel: Int = 0, // 0 = not compromised, 1 = user access, 2 = admin, 3 = system/root
    val fileSystem: Map<String, FileSystemNode> = mapOf()
)

// Represent connections between network nodes
data class NetworkConnection(
    val id: String = UUID.randomUUID().toString(),
    val sourceNodeId: String,
    val targetNodeId: String,
    val type: ConnectionType,
    val allowedPorts: List<Int> = listOf()
)

// Network map representing the entire infrastructure
class NetworkTopology {
    private val nodes = mutableMapOf<String, NetworkNode>()
    private val connections = mutableListOf<NetworkConnection>()
    private val segments = mutableMapOf<NetworkSegment, MutableList<NetworkNode>>()
    
    // Add a node to the network
    fun addNode(node: NetworkNode) {
        nodes[node.id] = node
        if (!segments.containsKey(node.segment)) {
            segments[node.segment] = mutableListOf()
        }
        segments[node.segment]?.add(node)
    }
    
    // Add a connection between nodes
    fun addConnection(connection: NetworkConnection) {
        if (!nodes.containsKey(connection.sourceNodeId) || 
            !nodes.containsKey(connection.targetNodeId)) {
            throw IllegalArgumentException("Source or target node does not exist")
        }
        connections.add(connection)
    }
    
    // Get all nodes in a specific segment
    fun getNodesInSegment(segment: NetworkSegment): List<NetworkNode> {
        return segments[segment]?.toList() ?: listOf()
    }
    
    // Get a node by IP address
    fun getNodeByIp(ipAddress: String): NetworkNode? {
        return nodes.values.find { it.ipAddress == ipAddress }
    }
    
    // Get all connections for a node
    fun getConnectionsForNode(nodeId: String): List<NetworkConnection> {
        return connections.filter { 
            it.sourceNodeId == nodeId || it.targetNodeId == nodeId 
        }
    }
    
    // Get all nodes connected to a specified node
    fun getConnectedNodes(nodeId: String): List<NetworkNode> {
        val connectedNodeIds = connections
            .filter { it.sourceNodeId == nodeId || it.targetNodeId == nodeId }
            .map { 
                if (it.sourceNodeId == nodeId) it.targetNodeId else it.sourceNodeId 
            }
        
        return nodes.values.filter { it.id in connectedNodeIds }
    }
    
    // Check if a node can reach another node
    fun canReach(sourceIp: String, targetIp: String): Boolean {
        val sourceNode = getNodeByIp(sourceIp) ?: return false
        val targetNode = getNodeByIp(targetIp) ?: return false
        
        // Path finding algorithm to see if there's a connection path
        val visited = mutableSetOf<String>()
        val queue = ArrayDeque<String>()
        
        queue.add(sourceNode.id)
        visited.add(sourceNode.id)
        
        while (queue.isNotEmpty()) {
            val currentNodeId = queue.removeFirst()
            
            if (currentNodeId == targetNode.id) {
                return true
            }
            
            getConnectionsForNode(currentNodeId).forEach { connection ->
                val nextNodeId = if (connection.sourceNodeId == currentNodeId) {
                    connection.targetNodeId
                } else {
                    connection.sourceNodeId
                }
                
                if (nextNodeId !in visited) {
                    visited.add(nextNodeId)
                    queue.add(nextNodeId)
                }
            }
        }
        
        return false
    }
    
    // Generate default network topology
    companion object {
        fun createDefaultCorporateNetwork(): NetworkTopology {
            val topology = NetworkTopology()
            
            // Create Internet Router
            val internetRouter = NetworkNode(
                name = "Internet Gateway",
                ipAddress = "203.0.113.1",
                type = NodeType.ROUTER,
                segment = NetworkSegment.INTERNET,
                securityLevel = SecurityLevel.MEDIUM,
                securityMeasures = SecurityMeasures(
                    hasFirewall = true,
                    hasIDS = true,
                    requiresAuthentication = true
                )
            )
            
            // DMZ Web Server
            val webServer = NetworkNode(
                name = "Public Web Server",
                ipAddress = "203.0.113.10",
                type = NodeType.WEBSERVER,
                segment = NetworkSegment.DMZ,
                securityLevel = SecurityLevel.MEDIUM,
                securityMeasures = SecurityMeasures(
                    hasFirewall = true,
                    requiresAuthentication = true,
                    patchLevel = 2 // Slightly outdated, has vulnerabilities
                )
            )
            webServer.services.add(NodeService(
                name = "HTTP",
                port = 80,
                version = "Apache 2.4.41",
                hasVulnerability = true,
                vulnerabilityDetails = "CVE-2020-1234: Directory traversal vulnerability"
            ))
            webServer.services.add(NodeService(
                name = "SSH",
                port = 22,
                version = "OpenSSH 7.9"
            ))
            
            // Corporate Firewall
            val corporateFirewall = NetworkNode(
                name = "Corporate Firewall",
                ipAddress = "10.0.0.1",
                type = NodeType.FIREWALL,
                segment = NetworkSegment.CORPORATE,
                securityLevel = SecurityLevel.HIGH,
                securityMeasures = SecurityMeasures(
                    hasFirewall = true,
                    hasIDS = true,
                    requiresAuthentication = true,
                    passwordComplexity = 4
                )
            )
            
            // Internal Mail Server
            val mailServer = NetworkNode(
                name = "Mail Server",
                ipAddress = "10.0.0.5",
                type = NodeType.MAILSERVER,
                segment = NetworkSegment.CORPORATE,
                securityLevel = SecurityLevel.MEDIUM
            )
            mailServer.services.add(NodeService(
                name = "SMTP",
                port = 25,
                version = "Postfix 3.4.8"
            ))
            mailServer.services.add(NodeService(
                name = "IMAP",
                port = 143,
                version = "Dovecot 2.3.4",
                hasVulnerability = true,
                vulnerabilityDetails = "Weak credential storage"
            ))
            
            // Database Server
            val dbServer = NetworkNode(
                name = "Database Server",
                ipAddress = "10.0.0.20",
                type = NodeType.DATABASE,
                segment = NetworkSegment.CORPORATE,
                securityLevel = SecurityLevel.HIGH,
                securityMeasures = SecurityMeasures(
                    hasFirewall = true,
                    requiresAuthentication = true,
                    passwordComplexity = 3
                )
            )
            dbServer.services.add(NodeService(
                name = "MySQL",
                port = 3306,
                version = "MySQL 5.7.32"
            ))
            
            // Developer Workstation
            val devWorkstation = NetworkNode(
                name = "Developer Workstation",
                ipAddress = "10.0.1.15",
                type = NodeType.WORKSTATION,
                segment = NetworkSegment.DEVELOPMENT,
                securityLevel = SecurityLevel.LOW
            )
            
            // Admin Server
            val adminServer = NetworkNode(
                name = "Admin Server",
                ipAddress = "10.0.2.5",
                type = NodeType.DOMAINCONTROLLER,
                segment = NetworkSegment.ADMIN,
                securityLevel = SecurityLevel.EXTREME,
                securityMeasures = SecurityMeasures(
                    hasFirewall = true,
                    hasIDS = true,
                    requiresAuthentication = true,
                    passwordComplexity = 5,
                    encryptionLevel = 4
                )
            )
            
            // Add all nodes to topology
            topology.addNode(internetRouter)
            topology.addNode(webServer)
            topology.addNode(corporateFirewall)
            topology.addNode(mailServer)
            topology.addNode(dbServer)
            topology.addNode(devWorkstation)
            topology.addNode(adminServer)
            
            // Create connections
            topology.addConnection(NetworkConnection(
                sourceNodeId = internetRouter.id,
                targetNodeId = webServer.id,
                type = ConnectionType.FILTERED,
                allowedPorts = listOf(80, 443, 22) // Only web and SSH allowed
            ))
            
            topology.addConnection(NetworkConnection(
                sourceNodeId = internetRouter.id,
                targetNodeId = corporateFirewall.id,
                type = ConnectionType.RESTRICTED,
                allowedPorts = listOf(22) // Only SSH from outside
            ))
            
            topology.addConnection(NetworkConnection(
                sourceNodeId = corporateFirewall.id,
                targetNodeId = mailServer.id,
                type = ConnectionType.DIRECT
            ))
            
            topology.addConnection(NetworkConnection(
                sourceNodeId = corporateFirewall.id,
                targetNodeId = dbServer.id,
                type = ConnectionType.FILTERED,
                allowedPorts = listOf(3306, 22) // Only MySQL and SSH
            ))
            
            topology.addConnection(NetworkConnection(
                sourceNodeId = corporateFirewall.id,
                targetNodeId = devWorkstation.id,
                type = ConnectionType.DIRECT
            ))
            
            topology.addConnection(NetworkConnection(
                sourceNodeId = adminServer.id,
                targetNodeId = corporateFirewall.id,
                type = ConnectionType.DIRECT
            ))
            
            return topology
        }
    }
} 