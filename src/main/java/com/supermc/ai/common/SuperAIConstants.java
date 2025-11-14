package com.supermc.ai.common;

/**
 * Constants used throughout the SuperAI Minecraft Bot system.
 *
 * This class contains all constant values, magic numbers, and configuration
 * defaults that are used across different modules of the system.
 *
 * @author SuperAI Team
 * @version 1.0.0
 * @since 1.0.0
 */
public final class SuperAIConstants {

    // ============================================================================
    // System Constants
    // ============================================================================

    /** The mod identifier */
    public static final String MOD_ID = "superai";

    /** The mod name */
    public static final String MOD_NAME = "SuperAI Minecraft Bot";

    /** The mod version */
    public static final String MOD_VERSION = "1.0.0";

    /** Default gRPC server host */
    public static final String DEFAULT_GRPC_HOST = "localhost";

    /** Default gRPC server port */
    public static final int DEFAULT_GRPC_PORT = 50051;

    // ============================================================================
    // Minecraft-specific Constants
    // ============================================================================

    /** Maximum reach distance for player interactions */
    public static final double MAX_REACH_DISTANCE = 5.0;

    /** Default player eye height */
    public static final double PLAYER_EYE_HEIGHT = 1.62;

    /** Player sneak eye height */
    public static final double PLAYER_SNEAK_EYE_HEIGHT = 1.27;

    /** Default player movement speed */
    public static final double DEFAULT_PLAYER_SPEED = 0.1;

    /** Sprint movement speed multiplier */
    public static final double SPRINT_SPEED_MULTIPLIER = 1.3;

    /** Sneak movement speed multiplier */
    public static final double SNEAK_SPEED_MULTIPLIER = 0.3;

    /** Jump velocity */
    public static final double JUMP_VELOCITY = 0.42;

    /** Gravity acceleration */
    public static final double GRAVITY = 0.08;

    /** Terminal velocity */
    public static final double TERMINAL_VELOCITY = 3.92;

    // ============================================================================
    // Environment Scanning Constants
    // ============================================================================

    /** Default scan radius in blocks */
    public static final int DEFAULT_SCAN_RADIUS = 16;

    /** Maximum scan radius allowed */
    public static final int MAX_SCAN_RADIUS = 64;

    /** Minimum scan radius allowed */
    public static final int MIN_SCAN_RADIUS = 1;

    /** Default maximum blocks to scan */
    public static final int DEFAULT_MAX_SCAN_BLOCKS = 10000;

    /** Default maximum entities to scan */
    public static final int DEFAULT_MAX_SCAN_ENTITIES = 100;

    /** Chunk size for batched scanning */
    public static final int SCAN_CHUNK_SIZE = 16;

    // ============================================================================
    // Player Control Constants
    // ============================================================================

    /** Default delay between actions in milliseconds */
    public static final int DEFAULT_ACTION_DELAY_MS = 100;

    /** Minimum action delay */
    public static final int MIN_ACTION_DELAY_MS = 50;

    /** Maximum action delay */
    public static final int MAX_ACTION_DELAY_MS = 5000;

    /** Default movement speed multiplier */
    public static final double DEFAULT_MOVE_SPEED_MULTIPLIER = 1.0;

    /** Maximum movement speed multiplier */
    public static final double MAX_MOVE_SPEED_MULTIPLIER = 3.0;

    /** Look sensitivity for rotation */
    public static final float LOOK_SENSITIVITY = 0.15f;

    /** Maximum look angle (pitch) */
    public static final float MAX_PITCH = 90.0f;

    /** Minimum look angle (pitch) */
    public static final float MIN_PITCH = -90.0f;

    // ============================================================================
    // AI Agent Management Constants
    // ============================================================================

    /** Default heartbeat interval in milliseconds */
    public static final int DEFAULT_HEARTBEAT_INTERVAL_MS = 10000;

    /** Heartbeat timeout multiplier */
    public static final int HEARTBEAT_TIMEOUT_MULTIPLIER = 3;

    /** Default session timeout in minutes */
    public static final int DEFAULT_SESSION_TIMEOUT_MINUTES = 60;

    /** Maximum active agents */
    public static final int MAX_ACTIVE_AGENTS = 10;

    /** Agent registration timeout in seconds */
    public static final int AGENT_REGISTRATION_TIMEOUT_SECONDS = 30;

    // ============================================================================
    // Performance Constants
    // ============================================================================

    /** Default cache size */
    public static final int DEFAULT_CACHE_SIZE = 10000;

    /** Default cache TTL in seconds */
    public static final int DEFAULT_CACHE_TTL_SECONDS = 300;

    /** Default thread pool size */
    public static final int DEFAULT_THREAD_POOL_SIZE = 8;

    /** Maximum thread pool size */
    public static final int MAX_THREAD_POOL_SIZE = 64;

    /** Queue capacity for thread pools */
    public static final int THREAD_POOL_QUEUE_CAPACITY = 1000;

    // ============================================================================
    // Security Constants
    // ============================================================================

    /** Default rate limit: requests per minute */
    public static final int DEFAULT_RATE_LIMIT_REQUESTS_PER_MINUTE = 1000;

    /** Maximum request size in bytes (1MB) */
    public static final int MAX_REQUEST_SIZE_BYTES = 1048576;

    /** Session token length */
    public static final int SESSION_TOKEN_LENGTH = 32;

    /** Authentication token validity in hours */
    public static final int AUTH_TOKEN_VALIDITY_HOURS = 24;

    // ============================================================================
    // Error Codes
    // ============================================================================

    /** Error code for successful operations */
    public static final int ERROR_SUCCESS = 0;

    /** Error code for invalid parameters */
    public static final int ERROR_INVALID_PARAMETER = 1001;

    /** Error code for operation not allowed */
    public static final int ERROR_OPERATION_NOT_ALLOWED = 1002;

    /** Error code for resource not found */
    public static final int ERROR_RESOURCE_NOT_FOUND = 1003;

    /** Error code for operation timeout */
    public static final int ERROR_TIMEOUT = 1004;

    /** Error code for internal server error */
    public static final int ERROR_INTERNAL_ERROR = 1005;

    /** Error code for authentication failure */
    public static final int ERROR_AUTHENTICATION_FAILED = 1006;

    /** Error code for rate limit exceeded */
    public static final int ERROR_RATE_LIMIT_EXCEEDED = 1007;

    /** Error code for agent not registered */
    public static final int ERROR_AGENT_NOT_REGISTERED = 1008;

    /** Error code for invalid agent state */
    public static final int ERROR_INVALID_AGENT_STATE = 1009;

    // ============================================================================
    // Logging Constants
    // ============================================================================

    /** Default log level */
    public static final String DEFAULT_LOG_LEVEL = "INFO";

    /** Performance log marker */
    public static final String LOG_MARKER_PERFORMANCE = "PERFORMANCE";

    /** Security log marker */
    public static final String LOG_MARKER_SECURITY = "SECURITY";

    /** Agent log marker */
    public static final String LOG_MARKER_AGENT = "AGENT";

    /** Environment log marker */
    public static final String LOG_MARKER_ENVIRONMENT = "ENVIRONMENT";

    /** Player log marker */
    public static final String LOG_MARKER_PLAYER = "PLAYER";

    // ============================================================================
    // File and Path Constants
    // ============================================================================

    /** Configuration directory name */
    public static final String CONFIG_DIR = "config";

    /** Logs directory name */
    public static final String LOGS_DIR = "logs";

    /** Cache directory name */
    public static final String CACHE_DIR = "cache";

    /** Default configuration file name */
    public static final String CONFIG_FILE_NAME = "superai.toml";

    /** Default log file name */
    public static final String LOG_FILE_NAME = "superai.log";

    // ============================================================================
    // Time Constants
    // ============================================================================

    /** Milliseconds in a second */
    public static final int MS_PER_SECOND = 1000;

    /** Milliseconds in a minute */
    public static final int MS_PER_MINUTE = 60000;

    /** Milliseconds in an hour */
    public static final int MS_PER_HOUR = 3600000;

    /** Seconds in a minute */
    public static final int SECONDS_PER_MINUTE = 60;

    /** Seconds in an hour */
    public static final int SECONDS_PER_HOUR = 3600;

    /** Minutes in an hour */
    public static final int MINUTES_PER_HOUR = 60;

    // ============================================================================
    // Math Constants
    // ============================================================================

    /** PI constant */
    public static final double PI = Math.PI;

    /** 2*PI constant */
    public static final double TWO_PI = 2 * Math.PI;

    /** PI/2 constant */
    public static final double HALF_PI = Math.PI / 2;

    /** PI/180 for degree to radian conversion */
    public static final double DEG_TO_RAD = Math.PI / 180.0;

    /** 180/PI for radian to degree conversion */
    public static final double RAD_TO_DEG = 180.0 / Math.PI;

    /** Square root of 2 */
    public static final double SQRT_2 = Math.sqrt(2);

    // ============================================================================
    // String Constants
    // ============================================================================

    /** Empty string constant */
    public static final String EMPTY_STRING = "";

    /** Space character */
    public static final String SPACE = " ";

    /** New line character */
    public static final String NEW_LINE = "\n";

    /** Comma separator */
    public static final String COMMA = ",";

    /** Colon separator */
    public static final String COLON = ":";

    /** Semicolon separator */
    public static final String SEMICOLON = ";";

    /** Default agent type */
    public static final String DEFAULT_AGENT_TYPE = "unknown";

    /** Default dimension */
    public static final String DEFAULT_DIMENSION = "overworld";

    // Private constructor to prevent instantiation
    private SuperAIConstants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }
}
