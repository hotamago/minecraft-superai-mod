package com.supermc.ai.grpc.service;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import com.supermc.ai.common.SuperAIConstants;
import com.supermc.ai.common.SuperAIExceptions;
import com.supermc.ai.config.SuperAIConfig;
import com.supermc.ai.grpc.proto.*;
import com.mojang.logging.LogUtils;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the AI Agent Service gRPC interface.
 *
 * Manages AI agent registration, heartbeat, and session management.
 *
 * @author SuperAI Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class AIAgentServiceImpl extends AIAgentServiceGrpc.AIAgentServiceImplBase {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    // Agent registry
    private final Map<String, AgentSession> activeAgents = new ConcurrentHashMap<>();
    private final Map<String, String> sessionTokens = new ConcurrentHashMap<>(); // token -> agentId
    
    @Override
    public void registerAgent(RegisterAgentRequest request,
                             StreamObserver<RegisterAgentResponse> responseObserver) {
        if (request == null || request.getAgentId().isEmpty()) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid request: agent ID is required")
                    .asRuntimeException());
            return;
        }
        
        try {
            // Check max agents limit
            int maxAgents = SuperAIConfig.AGENT_MAX_ACTIVE_AGENTS.get();
            if (activeAgents.size() >= maxAgents) {
                responseObserver.onError(Status.RESOURCE_EXHAUSTED
                        .withDescription("Maximum number of active agents reached: " + maxAgents)
                        .asRuntimeException());
                return;
            }
            
            // Check if agent already registered
            String agentId = request.getAgentId();
            if (activeAgents.containsKey(agentId)) {
                responseObserver.onError(Status.ALREADY_EXISTS
                        .withDescription("Agent already registered: " + agentId)
                        .asRuntimeException());
                return;
            }
            
            // Validate agent type if authentication is enabled
            if (SuperAIConfig.AGENT_ENABLE_AUTHENTICATION.get()) {
                String allowedTypes = SuperAIConfig.AGENT_ALLOWED_TYPES.get();
                String agentType = request.getAgentType();
                if (!allowedTypes.contains(agentType)) {
                    responseObserver.onError(Status.PERMISSION_DENIED
                            .withDescription("Agent type not allowed: " + agentType)
                            .asRuntimeException());
                    return;
                }
            }
            
            // Generate session token
            String sessionToken = generateSessionToken();
            
            // Create agent session
            AgentSession session = new AgentSession(
                    agentId,
                    request.getAgentName(),
                    request.getAgentType(),
                    request.getDescription(),
                    request.getCapabilitiesMap(),
                    request.getHeartbeatIntervalMs() > 0 ? request.getHeartbeatIntervalMs() :
                            SuperAIConstants.DEFAULT_HEARTBEAT_INTERVAL_MS,
                    Instant.now()
            );
            
            activeAgents.put(agentId, session);
            sessionTokens.put(sessionToken, agentId);
            
            LOGGER.info("Agent registered: {} ({})", agentId, request.getAgentName());
            
            RegisterAgentResponse response = RegisterAgentResponse.newBuilder()
                    .setSuccess(true)
                    .setSessionToken(sessionToken)
                    .setRegisteredAt(Timestamp.newBuilder()
                            .setSeconds(session.getRegisteredAt().getEpochSecond())
                            .setNanos(session.getRegisteredAt().getNano()))
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            LOGGER.error("Error registering agent", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to register agent: " + e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        }
    }
    
    @Override
    public void unregisterAgent(UnregisterAgentRequest request,
                               StreamObserver<UnregisterAgentResponse> responseObserver) {
        if (request == null || request.getAgentId().isEmpty() || request.getSessionToken().isEmpty()) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid request: agent ID and session token are required")
                    .asRuntimeException());
            return;
        }
        
        try {
            String agentId = request.getAgentId();
            String sessionToken = request.getSessionToken();
            
            // Validate session token
            if (!validateSession(agentId, sessionToken)) {
                responseObserver.onError(Status.UNAUTHENTICATED
                        .withDescription("Invalid session token")
                        .asRuntimeException());
                return;
            }
            
            // Remove agent
            activeAgents.remove(agentId);
            sessionTokens.remove(sessionToken);
            
            LOGGER.info("Agent unregistered: {}", agentId);
            
            UnregisterAgentResponse response = UnregisterAgentResponse.newBuilder()
                    .setSuccess(true)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            LOGGER.error("Error unregistering agent", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to unregister agent: " + e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        }
    }
    
    @Override
    public void getActiveAgents(Empty request,
                               StreamObserver<ActiveAgentsResponse> responseObserver) {
        try {
            ActiveAgentsResponse.Builder responseBuilder = ActiveAgentsResponse.newBuilder()
                    .setSuccess(true);
            
            // Clean up expired sessions
            cleanupExpiredSessions();
            
            // Build agent info list
            for (AgentSession session : activeAgents.values()) {
                AgentInfo agentInfo = AgentInfo.newBuilder()
                        .setAgentId(session.getAgentId())
                        .setAgentName(session.getAgentName())
                        .setAgentType(session.getAgentType())
                        .setDescription(session.getDescription())
                        .setRegisteredAt(Timestamp.newBuilder()
                                .setSeconds(session.getRegisteredAt().getEpochSecond())
                                .setNanos(session.getRegisteredAt().getNano()))
                        .setLastHeartbeat(Timestamp.newBuilder()
                                .setSeconds(session.getLastHeartbeat().getEpochSecond())
                                .setNanos(session.getLastHeartbeat().getNano()))
                        .setIsActive(isSessionActive(session))
                        .putAllCapabilities(session.getCapabilities())
                        .build();
                
                responseBuilder.addAgents(agentInfo);
            }
            
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            LOGGER.error("Error getting active agents", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to get active agents: " + e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        }
    }
    
    @Override
    public void heartbeat(HeartbeatRequest request,
                         StreamObserver<HeartbeatResponse> responseObserver) {
        if (request == null || request.getAgentId().isEmpty() || request.getSessionToken().isEmpty()) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid request: agent ID and session token are required")
                    .asRuntimeException());
            return;
        }
        
        try {
            String agentId = request.getAgentId();
            String sessionToken = request.getSessionToken();
            
            // Validate session
            if (!validateSession(agentId, sessionToken)) {
                responseObserver.onError(Status.UNAUTHENTICATED
                        .withDescription("Invalid session token")
                        .asRuntimeException());
                return;
            }
            
            // Update heartbeat
            AgentSession session = activeAgents.get(agentId);
            if (session != null) {
                session.updateHeartbeat();
            }
            
            HeartbeatResponse response = HeartbeatResponse.newBuilder()
                    .setSuccess(true)
                    .setServerTime(Timestamp.newBuilder()
                            .setSeconds(Instant.now().getEpochSecond())
                            .setNanos(Instant.now().getNano()))
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            LOGGER.error("Error processing heartbeat", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to process heartbeat: " + e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        }
    }
    
    /**
     * Validates a session token for an agent.
     *
     * @param agentId the agent ID
     * @param sessionToken the session token
     * @return true if session is valid
     */
    private boolean validateSession(String agentId, String sessionToken) {
        String registeredAgentId = sessionTokens.get(sessionToken);
        return agentId.equals(registeredAgentId) && activeAgents.containsKey(agentId);
    }
    
    /**
     * Generates a random session token.
     *
     * @return session token string
     */
    private String generateSessionToken() {
        Random random = new Random();
        StringBuilder token = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < SuperAIConstants.SESSION_TOKEN_LENGTH; i++) {
            token.append(chars.charAt(random.nextInt(chars.length())));
        }
        return token.toString();
    }
    
    /**
     * Checks if a session is still active.
     *
     * @param session the agent session
     * @return true if session is active
     */
    private boolean isSessionActive(AgentSession session) {
        long timeoutMs = SuperAIConfig.AGENT_HEARTBEAT_TIMEOUT_MS.get();
        long timeSinceHeartbeat = Instant.now().toEpochMilli() - session.getLastHeartbeat().toEpochMilli();
        return timeSinceHeartbeat < timeoutMs;
    }
    
    /**
     * Cleans up expired agent sessions.
     */
    private void cleanupExpiredSessions() {
        long timeoutMs = SuperAIConfig.AGENT_HEARTBEAT_TIMEOUT_MS.get();
        long sessionTimeoutMs = SuperAIConfig.AGENT_SESSION_TIMEOUT_MINUTES.get() * 60L * 1000L;
        Instant now = Instant.now();
        
        List<String> toRemove = new ArrayList<>();
        for (Map.Entry<String, AgentSession> entry : activeAgents.entrySet()) {
            AgentSession session = entry.getValue();
            long timeSinceHeartbeat = now.toEpochMilli() - session.getLastHeartbeat().toEpochMilli();
            long timeSinceRegistration = now.toEpochMilli() - session.getRegisteredAt().toEpochMilli();
            
            if (timeSinceHeartbeat > timeoutMs || timeSinceRegistration > sessionTimeoutMs) {
                toRemove.add(entry.getKey());
            }
        }
        
        for (String agentId : toRemove) {
            activeAgents.remove(agentId);
            sessionTokens.values().removeIf(token -> sessionTokens.get(token).equals(agentId));
            LOGGER.info("Removed expired agent session: {}", agentId);
        }
    }
    
    /**
     * Represents an active AI agent session.
     */
    private static class AgentSession {
        private final String agentId;
        private final String agentName;
        private final String agentType;
        private final String description;
        private final Map<String, String> capabilities;
        private final int heartbeatIntervalMs;
        private final Instant registeredAt;
        private Instant lastHeartbeat;
        
        public AgentSession(String agentId, String agentName, String agentType, String description,
                          Map<String, String> capabilities, int heartbeatIntervalMs, Instant registeredAt) {
            this.agentId = agentId;
            this.agentName = agentName;
            this.agentType = agentType;
            this.description = description;
            this.capabilities = new HashMap<>(capabilities);
            this.heartbeatIntervalMs = heartbeatIntervalMs;
            this.registeredAt = registeredAt;
            this.lastHeartbeat = registeredAt;
        }
        
        public String getAgentId() {
            return agentId;
        }
        
        public String getAgentName() {
            return agentName;
        }
        
        public String getAgentType() {
            return agentType;
        }
        
        public String getDescription() {
            return description;
        }
        
        public Map<String, String> getCapabilities() {
            return capabilities;
        }
        
        public int getHeartbeatIntervalMs() {
            return heartbeatIntervalMs;
        }
        
        public Instant getRegisteredAt() {
            return registeredAt;
        }
        
        public Instant getLastHeartbeat() {
            return lastHeartbeat;
        }
        
        public void updateHeartbeat() {
            this.lastHeartbeat = Instant.now();
        }
    }
}

