package com.supermc.ai.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuration management for SuperAI Minecraft Bot.
 *
 * This class handles all configuration settings for the mod including:
 * - gRPC server settings
 * - Environment scanning parameters
 * - Player control settings
 * - AI agent management settings
 * - Performance and safety parameters
 *
 * @author SuperAI Team
 * @version 1.0.0
 * @since 1.0.0
 */
public final class SuperAIConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Configuration file path
    private static final Path CONFIG_PATH = Paths.get("config", "superai.json");

    // Configuration categories
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // gRPC Server Configuration
    public static final ForgeConfigSpec.ConfigValue<String> GRPC_HOST;
    public static final ForgeConfigSpec.ConfigValue<Integer> GRPC_PORT;
    public static final ForgeConfigSpec.ConfigValue<Integer> GRPC_MAX_CONNECTIONS;
    public static final ForgeConfigSpec.ConfigValue<Integer> GRPC_THREAD_POOL_SIZE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> GRPC_ENABLE_TLS;
    public static final ForgeConfigSpec.ConfigValue<String> GRPC_CERT_PATH;
    public static final ForgeConfigSpec.ConfigValue<String> GRPC_KEY_PATH;

    // Environment Scanning Configuration
    public static final ForgeConfigSpec.ConfigValue<Integer> SCAN_RADIUS_DEFAULT;
    public static final ForgeConfigSpec.ConfigValue<Integer> SCAN_RADIUS_MAX;
    public static final ForgeConfigSpec.ConfigValue<Integer> SCAN_UPDATE_INTERVAL_MS;
    public static final ForgeConfigSpec.ConfigValue<Integer> SCAN_MAX_BLOCKS;
    public static final ForgeConfigSpec.ConfigValue<Integer> SCAN_MAX_ENTITIES;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SCAN_INCLUDE_AIR_BLOCKS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SCAN_INCLUDE_ENTITIES;

    // Player Control Configuration
    public static final ForgeConfigSpec.ConfigValue<Double> PLAYER_MOVE_SPEED_DEFAULT;
    public static final ForgeConfigSpec.ConfigValue<Double> PLAYER_MOVE_SPEED_MAX;
    public static final ForgeConfigSpec.ConfigValue<Integer> PLAYER_ACTION_DELAY_MIN_MS;
    public static final ForgeConfigSpec.ConfigValue<Integer> PLAYER_ACTION_DELAY_MAX_MS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> PLAYER_ENABLE_PATHFINDING;
    public static final ForgeConfigSpec.ConfigValue<Double> PLAYER_PATHFINDING_RANGE_MAX;
    public static final ForgeConfigSpec.ConfigValue<Boolean> PLAYER_ENABLE_SAFETY_CHECKS;

    // AI Agent Management Configuration
    public static final ForgeConfigSpec.ConfigValue<Integer> AGENT_MAX_ACTIVE_AGENTS;
    public static final ForgeConfigSpec.ConfigValue<Integer> AGENT_HEARTBEAT_TIMEOUT_MS;
    public static final ForgeConfigSpec.ConfigValue<Integer> AGENT_SESSION_TIMEOUT_MINUTES;
    public static final ForgeConfigSpec.ConfigValue<Boolean> AGENT_ENABLE_AUTHENTICATION;
    public static final ForgeConfigSpec.ConfigValue<String> AGENT_ALLOWED_TYPES;

    // Performance Configuration
    public static final ForgeConfigSpec.ConfigValue<Integer> PERF_CACHE_SIZE_MAX;
    public static final ForgeConfigSpec.ConfigValue<Integer> PERF_CACHE_TTL_SECONDS;
    public static final ForgeConfigSpec.ConfigValue<Integer> PERF_THREAD_POOL_SIZE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> PERF_ENABLE_METRICS;

    // Safety and Security Configuration
    public static final ForgeConfigSpec.ConfigValue<Boolean> SECURITY_ENABLE_RATE_LIMITING;
    public static final ForgeConfigSpec.ConfigValue<Integer> SECURITY_RATE_LIMIT_REQUESTS_PER_MINUTE;
    public static final ForgeConfigSpec.ConfigValue<Integer> SECURITY_MAX_REQUEST_SIZE_BYTES;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SECURITY_VALIDATE_ALL_INPUTS;

    // Logging Configuration
    public static final ForgeConfigSpec.ConfigValue<String> LOG_LEVEL;
    public static final ForgeConfigSpec.ConfigValue<Boolean> LOG_ENABLE_REQUEST_LOGGING;
    public static final ForgeConfigSpec.ConfigValue<Boolean> LOG_ENABLE_PERFORMANCE_LOGGING;
    public static final ForgeConfigSpec.ConfigValue<String> LOG_FILE_PATH;

    static {
        BUILDER.comment("SuperAI Minecraft Bot Configuration").push("superai");

        // gRPC Server Configuration
        BUILDER.comment("gRPC Server Settings").push("grpc");
        GRPC_HOST = BUILDER
                .comment("Host address for gRPC server")
                .define("host", "localhost");
        GRPC_PORT = BUILDER
                .comment("Port for gRPC server")
                .defineInRange("port", 50051, 1024, 65535);
        GRPC_MAX_CONNECTIONS = BUILDER
                .comment("Maximum concurrent connections")
                .defineInRange("maxConnections", 100, 1, 1000);
        GRPC_THREAD_POOL_SIZE = BUILDER
                .comment("Thread pool size for gRPC server")
                .defineInRange("threadPoolSize", 16, 1, 256);
        GRPC_ENABLE_TLS = BUILDER
                .comment("Enable TLS encryption for gRPC connections")
                .define("enableTLS", false);
        GRPC_CERT_PATH = BUILDER
                .comment("Path to TLS certificate file")
                .define("certPath", "config/superai.crt");
        GRPC_KEY_PATH = BUILDER
                .comment("Path to TLS private key file")
                .define("keyPath", "config/superai.key");
        BUILDER.pop();

        // Environment Scanning Configuration
        BUILDER.comment("Environment Scanning Settings").push("environment");
        SCAN_RADIUS_DEFAULT = BUILDER
                .comment("Default scan radius in blocks")
                .defineInRange("defaultRadius", 16, 1, 128);
        SCAN_RADIUS_MAX = BUILDER
                .comment("Maximum allowed scan radius")
                .defineInRange("maxRadius", 64, 1, 256);
        SCAN_UPDATE_INTERVAL_MS = BUILDER
                .comment("Default update interval for environment scans (milliseconds)")
                .defineInRange("updateIntervalMs", 1000, 100, 10000);
        SCAN_MAX_BLOCKS = BUILDER
                .comment("Maximum blocks to return in a single scan")
                .defineInRange("maxBlocks", 10000, 100, 100000);
        SCAN_MAX_ENTITIES = BUILDER
                .comment("Maximum entities to return in a single scan")
                .defineInRange("maxEntities", 100, 1, 1000);
        SCAN_INCLUDE_AIR_BLOCKS = BUILDER
                .comment("Include air blocks in environment scans")
                .define("includeAirBlocks", false);
        SCAN_INCLUDE_ENTITIES = BUILDER
                .comment("Include entities in environment scans")
                .define("includeEntities", true);
        BUILDER.pop();

        // Player Control Configuration
        BUILDER.comment("Player Control Settings").push("player");
        PLAYER_MOVE_SPEED_DEFAULT = BUILDER
                .comment("Default player movement speed multiplier")
                .defineInRange("defaultMoveSpeed", 1.0, 0.1, 5.0);
        PLAYER_MOVE_SPEED_MAX = BUILDER
                .comment("Maximum allowed player movement speed multiplier")
                .defineInRange("maxMoveSpeed", 3.0, 0.1, 10.0);
        PLAYER_ACTION_DELAY_MIN_MS = BUILDER
                .comment("Minimum delay between player actions (milliseconds)")
                .defineInRange("minActionDelayMs", 50, 0, 1000);
        PLAYER_ACTION_DELAY_MAX_MS = BUILDER
                .comment("Maximum delay between player actions (milliseconds)")
                .defineInRange("maxActionDelayMs", 5000, 100, 30000);
        PLAYER_ENABLE_PATHFINDING = BUILDER
                .comment("Enable pathfinding for player movement")
                .define("enablePathfinding", true);
        PLAYER_PATHFINDING_RANGE_MAX = BUILDER
                .comment("Maximum pathfinding range")
                .defineInRange("maxPathfindingRange", 100.0, 10.0, 1000.0);
        PLAYER_ENABLE_SAFETY_CHECKS = BUILDER
                .comment("Enable safety checks for player actions")
                .define("enableSafetyChecks", true);
        BUILDER.pop();

        // AI Agent Management Configuration
        BUILDER.comment("AI Agent Management Settings").push("agent");
        AGENT_MAX_ACTIVE_AGENTS = BUILDER
                .comment("Maximum number of active AI agents")
                .defineInRange("maxActiveAgents", 10, 1, 100);
        AGENT_HEARTBEAT_TIMEOUT_MS = BUILDER
                .comment("Heartbeat timeout in milliseconds")
                .defineInRange("heartbeatTimeoutMs", 30000, 5000, 300000);
        AGENT_SESSION_TIMEOUT_MINUTES = BUILDER
                .comment("Session timeout in minutes")
                .defineInRange("sessionTimeoutMinutes", 60, 5, 1440);
        AGENT_ENABLE_AUTHENTICATION = BUILDER
                .comment("Enable authentication for AI agents")
                .define("enableAuthentication", true);
        AGENT_ALLOWED_TYPES = BUILDER
                .comment("Comma-separated list of allowed agent types")
                .define("allowedTypes", "reinforcement_learning,behavior_tree,scripted,neural_network");
        BUILDER.pop();

        // Performance Configuration
        BUILDER.comment("Performance Settings").push("performance");
        PERF_CACHE_SIZE_MAX = BUILDER
                .comment("Maximum cache size")
                .defineInRange("maxCacheSize", 10000, 100, 100000);
        PERF_CACHE_TTL_SECONDS = BUILDER
                .comment("Cache TTL in seconds")
                .defineInRange("cacheTtlSeconds", 300, 60, 3600);
        PERF_THREAD_POOL_SIZE = BUILDER
                .comment("Thread pool size for async operations")
                .defineInRange("threadPoolSize", 8, 1, 64);
        PERF_ENABLE_METRICS = BUILDER
                .comment("Enable performance metrics collection")
                .define("enableMetrics", true);
        BUILDER.pop();

        // Safety and Security Configuration
        BUILDER.comment("Security Settings").push("security");
        SECURITY_ENABLE_RATE_LIMITING = BUILDER
                .comment("Enable rate limiting for API calls")
                .define("enableRateLimiting", true);
        SECURITY_RATE_LIMIT_REQUESTS_PER_MINUTE = BUILDER
                .comment("Rate limit: requests per minute")
                .defineInRange("rateLimitRequestsPerMinute", 1000, 10, 10000);
        SECURITY_MAX_REQUEST_SIZE_BYTES = BUILDER
                .comment("Maximum request size in bytes")
                .defineInRange("maxRequestSizeBytes", 1048576, 1024, 10485760); // 1MB default, 10MB max
        SECURITY_VALIDATE_ALL_INPUTS = BUILDER
                .comment("Validate all input parameters")
                .define("validateAllInputs", true);
        BUILDER.pop();

        // Logging Configuration
        BUILDER.comment("Logging Settings").push("logging");
        LOG_LEVEL = BUILDER
                .comment("Logging level (TRACE, DEBUG, INFO, WARN, ERROR)")
                .define("level", "INFO");
        LOG_ENABLE_REQUEST_LOGGING = BUILDER
                .comment("Enable request/response logging")
                .define("enableRequestLogging", true);
        LOG_ENABLE_PERFORMANCE_LOGGING = BUILDER
                .comment("Enable performance logging")
                .define("enablePerformanceLogging", true);
        LOG_FILE_PATH = BUILDER
                .comment("Path to log file")
                .define("filePath", "logs/superai.log");
        BUILDER.pop();

        BUILDER.pop();
    }

    /**
     * Initialize the configuration system.
     * This should be called during mod initialization with the mod loading context.
     *
     * @param context The mod loading context
     */
    public static void initialize(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, BUILDER.build());

        // Create config directory if it doesn't exist
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
        } catch (IOException e) {
            LOGGER.error("Failed to create config directory", e);
        }

        LOGGER.info("SuperAI configuration initialized");
    }

    /**
     * Get the current configuration as a JSON string.
     * Useful for debugging and external monitoring.
     *
     * @return JSON representation of current configuration
     */
    public static String getConfigAsJson() {
        ConfigSnapshot snapshot = new ConfigSnapshot();
        return GSON.toJson(snapshot);
    }

    /**
     * Validate the current configuration.
     * Checks for invalid combinations and potentially problematic settings.
     *
     * @return true if configuration is valid, false otherwise
     */
    public static boolean validateConfiguration() {
        boolean isValid = true;

        // Validate gRPC settings
        if (GRPC_PORT.get() < 1024 || GRPC_PORT.get() > 65535) {
            LOGGER.error("Invalid gRPC port: {}", GRPC_PORT.get());
            isValid = false;
        }

        // Validate scan radius settings
        if (SCAN_RADIUS_DEFAULT.get() > SCAN_RADIUS_MAX.get()) {
            LOGGER.error("Default scan radius ({}) exceeds maximum radius ({})",
                    SCAN_RADIUS_DEFAULT.get(), SCAN_RADIUS_MAX.get());
            isValid = false;
        }

        // Validate player movement settings
        if (PLAYER_MOVE_SPEED_DEFAULT.get() > PLAYER_MOVE_SPEED_MAX.get()) {
            LOGGER.error("Default move speed ({}) exceeds maximum speed ({})",
                    PLAYER_MOVE_SPEED_DEFAULT.get(), PLAYER_MOVE_SPEED_MAX.get());
            isValid = false;
        }

        // Validate action delay settings
        if (PLAYER_ACTION_DELAY_MIN_MS.get() > PLAYER_ACTION_DELAY_MAX_MS.get()) {
            LOGGER.error("Minimum action delay ({}) exceeds maximum delay ({})",
                    PLAYER_ACTION_DELAY_MIN_MS.get(), PLAYER_ACTION_DELAY_MAX_MS.get());
            isValid = false;
        }

        return isValid;
    }

    /**
     * Configuration snapshot for JSON serialization.
     */
    private static class ConfigSnapshot {
        public final GrpcConfig grpc = new GrpcConfig();
        public final EnvironmentConfig environment = new EnvironmentConfig();
        public final PlayerConfig player = new PlayerConfig();
        public final AgentConfig agent = new AgentConfig();
        public final PerformanceConfig performance = new PerformanceConfig();
        public final SecurityConfig security = new SecurityConfig();
        public final LoggingConfig logging = new LoggingConfig();

        private static class GrpcConfig {
            public final String host = GRPC_HOST.get();
            public final int port = GRPC_PORT.get();
            public final int maxConnections = GRPC_MAX_CONNECTIONS.get();
            public final int threadPoolSize = GRPC_THREAD_POOL_SIZE.get();
            public final boolean enableTLS = GRPC_ENABLE_TLS.get();
        }

        private static class EnvironmentConfig {
            public final int defaultRadius = SCAN_RADIUS_DEFAULT.get();
            public final int maxRadius = SCAN_RADIUS_MAX.get();
            public final int updateIntervalMs = SCAN_UPDATE_INTERVAL_MS.get();
            public final int maxBlocks = SCAN_MAX_BLOCKS.get();
            public final int maxEntities = SCAN_MAX_ENTITIES.get();
            public final boolean includeAirBlocks = SCAN_INCLUDE_AIR_BLOCKS.get();
            public final boolean includeEntities = SCAN_INCLUDE_ENTITIES.get();
        }

        private static class PlayerConfig {
            public final double defaultMoveSpeed = PLAYER_MOVE_SPEED_DEFAULT.get();
            public final double maxMoveSpeed = PLAYER_MOVE_SPEED_MAX.get();
            public final int minActionDelayMs = PLAYER_ACTION_DELAY_MIN_MS.get();
            public final int maxActionDelayMs = PLAYER_ACTION_DELAY_MAX_MS.get();
            public final boolean enablePathfinding = PLAYER_ENABLE_PATHFINDING.get();
            public final double maxPathfindingRange = PLAYER_PATHFINDING_RANGE_MAX.get();
            public final boolean enableSafetyChecks = PLAYER_ENABLE_SAFETY_CHECKS.get();
        }

        private static class AgentConfig {
            public final int maxActiveAgents = AGENT_MAX_ACTIVE_AGENTS.get();
            public final int heartbeatTimeoutMs = AGENT_HEARTBEAT_TIMEOUT_MS.get();
            public final int sessionTimeoutMinutes = AGENT_SESSION_TIMEOUT_MINUTES.get();
            public final boolean enableAuthentication = AGENT_ENABLE_AUTHENTICATION.get();
            public final String allowedTypes = AGENT_ALLOWED_TYPES.get();
        }

        private static class PerformanceConfig {
            public final int maxCacheSize = PERF_CACHE_SIZE_MAX.get();
            public final int cacheTtlSeconds = PERF_CACHE_TTL_SECONDS.get();
            public final int threadPoolSize = PERF_THREAD_POOL_SIZE.get();
            public final boolean enableMetrics = PERF_ENABLE_METRICS.get();
        }

        private static class SecurityConfig {
            public final boolean enableRateLimiting = SECURITY_ENABLE_RATE_LIMITING.get();
            public final int rateLimitRequestsPerMinute = SECURITY_RATE_LIMIT_REQUESTS_PER_MINUTE.get();
            public final int maxRequestSizeBytes = SECURITY_MAX_REQUEST_SIZE_BYTES.get();
            public final boolean validateAllInputs = SECURITY_VALIDATE_ALL_INPUTS.get();
        }

        private static class LoggingConfig {
            public final String level = LOG_LEVEL.get();
            public final boolean enableRequestLogging = LOG_ENABLE_REQUEST_LOGGING.get();
            public final boolean enablePerformanceLogging = LOG_ENABLE_PERFORMANCE_LOGGING.get();
            public final String filePath = LOG_FILE_PATH.get();
        }
    }

    // Private constructor to prevent instantiation
    private SuperAIConfig() {}
}
