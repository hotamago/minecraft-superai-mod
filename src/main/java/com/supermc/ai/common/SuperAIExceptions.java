package com.supermc.ai.common;

/**
 * Specific exception classes for different SuperAI modules.
 *
 * This class contains all the specific exception types used throughout
 * the SuperAI system, organized by module and functionality.
 *
 * @author SuperAI Team
 * @version 1.0.0
 * @since 1.0.0
 */
public final class SuperAIExceptions {

    // ============================================================================
    // Configuration Exceptions
    // ============================================================================

    /**
     * Exception thrown when configuration is invalid or missing.
     */
    public static class ConfigurationException extends SuperAIException {
        public ConfigurationException(String message) {
            super(message, SuperAIConstants.ERROR_INVALID_PARAMETER);
        }

        public ConfigurationException(String message, Throwable cause) {
            super(message, cause, SuperAIConstants.ERROR_INVALID_PARAMETER);
        }

        public ConfigurationException(String message, String context) {
            super(message, null, SuperAIConstants.ERROR_INVALID_PARAMETER, context);
        }
    }

    // ============================================================================
    // gRPC Service Exceptions
    // ============================================================================

    /**
     * Exception thrown when gRPC service encounters an error.
     */
    public static class GrpcServiceException extends SuperAIException {
        public GrpcServiceException(String message) {
            super(message, SuperAIConstants.ERROR_INTERNAL_ERROR);
        }

        public GrpcServiceException(String message, Throwable cause) {
            super(message, cause, SuperAIConstants.ERROR_INTERNAL_ERROR);
        }

        public GrpcServiceException(String message, int errorCode) {
            super(message, errorCode);
        }

        public GrpcServiceException(String message, Throwable cause, int errorCode) {
            super(message, cause, errorCode);
        }
    }

    /**
     * Exception thrown when authentication fails.
     */
    public static class AuthenticationException extends GrpcServiceException {
        public AuthenticationException(String message) {
            super(message, SuperAIConstants.ERROR_AUTHENTICATION_FAILED);
        }

        public AuthenticationException(String message, String context) {
            super(message, null, SuperAIConstants.ERROR_AUTHENTICATION_FAILED);
            // Note: context is not used in this simplified constructor
        }
    }

    /**
     * Exception thrown when rate limit is exceeded.
     */
    public static class RateLimitException extends GrpcServiceException {
        public RateLimitException(String message) {
            super(message, SuperAIConstants.ERROR_RATE_LIMIT_EXCEEDED);
        }

        public RateLimitException(String message, String context) {
            super(message, null, SuperAIConstants.ERROR_RATE_LIMIT_EXCEEDED);
            // Note: context is not used in this simplified constructor
        }
    }

    // ============================================================================
    // AI Agent Exceptions
    // ============================================================================

    /**
     * Exception thrown when AI agent operations fail.
     */
    public static class AgentException extends SuperAIException {
        public AgentException(String message) {
            super(message, SuperAIConstants.ERROR_AGENT_NOT_REGISTERED);
        }

        public AgentException(String message, Throwable cause) {
            super(message, cause, SuperAIConstants.ERROR_AGENT_NOT_REGISTERED);
        }

        public AgentException(String message, int errorCode) {
            super(message, errorCode);
        }

        public AgentException(String message, String context, int errorCode) {
            super(message, null, errorCode, context);
        }
    }

    /**
     * Exception thrown when agent registration fails.
     */
    public static class AgentRegistrationException extends AgentException {
        public AgentRegistrationException(String message) {
            super(message, SuperAIConstants.ERROR_INVALID_PARAMETER);
        }

        public AgentRegistrationException(String message, String context) {
            super(message, SuperAIConstants.ERROR_INVALID_PARAMETER);
            // Note: context is not used in this simplified constructor
        }
    }

    /**
     * Exception thrown when agent session is invalid or expired.
     */
    public static class AgentSessionException extends AgentException {
        public AgentSessionException(String message) {
            super(message, SuperAIConstants.ERROR_AUTHENTICATION_FAILED);
        }

        public AgentSessionException(String message, String context) {
            super(message, SuperAIConstants.ERROR_AUTHENTICATION_FAILED);
            // Note: context is not used in this simplified constructor
        }
    }

    // ============================================================================
    // Environment Perception Exceptions
    // ============================================================================

    /**
     * Exception thrown when environment scanning fails.
     */
    public static class EnvironmentException extends SuperAIException {
        public EnvironmentException(String message) {
            super(message, SuperAIConstants.ERROR_INTERNAL_ERROR);
        }

        public EnvironmentException(String message, Throwable cause) {
            super(message, cause, SuperAIConstants.ERROR_INTERNAL_ERROR);
        }

        public EnvironmentException(String message, int errorCode) {
            super(message, errorCode);
        }
    }

    /**
     * Exception thrown when scan parameters are invalid.
     */
    public static class InvalidScanParametersException extends EnvironmentException {
        public InvalidScanParametersException(String message) {
            super(message, SuperAIConstants.ERROR_INVALID_PARAMETER);
        }

        public InvalidScanParametersException(String message, String context) {
            super(message, SuperAIConstants.ERROR_INVALID_PARAMETER);
            // Note: context is not used in this simplified constructor
        }
    }

    /**
     * Exception thrown when trying to access blocks outside allowed range.
     */
    public static class ScanOutOfRangeException extends EnvironmentException {
        public ScanOutOfRangeException(String message) {
            super(message, SuperAIConstants.ERROR_OPERATION_NOT_ALLOWED);
        }

        public ScanOutOfRangeException(String message, String context) {
            super(message, SuperAIConstants.ERROR_OPERATION_NOT_ALLOWED);
            // Note: context is not used in this simplified constructor
        }
    }

    // ============================================================================
    // Player Control Exceptions
    // ============================================================================

    /**
     * Exception thrown when player control operations fail.
     */
    public static class PlayerControlException extends SuperAIException {
        public PlayerControlException(String message) {
            super(message, SuperAIConstants.ERROR_INTERNAL_ERROR);
        }

        public PlayerControlException(String message, Throwable cause) {
            super(message, cause, SuperAIConstants.ERROR_INTERNAL_ERROR);
        }

        public PlayerControlException(String message, int errorCode) {
            super(message, errorCode);
        }
    }

    /**
     * Exception thrown when player action is not allowed.
     */
    public static class InvalidPlayerActionException extends PlayerControlException {
        public InvalidPlayerActionException(String message) {
            super(message, SuperAIConstants.ERROR_OPERATION_NOT_ALLOWED);
        }

        public InvalidPlayerActionException(String message, String context) {
            super(message, SuperAIConstants.ERROR_OPERATION_NOT_ALLOWED);
            // Note: context is not used in this simplified constructor
        }
    }

    /**
     * Exception thrown when player movement fails.
     */
    public static class PlayerMovementException extends PlayerControlException {
        public PlayerMovementException(String message) {
            super(message, SuperAIConstants.ERROR_INTERNAL_ERROR);
        }

        public PlayerMovementException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // ============================================================================
    // Inventory Exceptions
    // ============================================================================

    /**
     * Exception thrown when inventory operations fail.
     */
    public static class InventoryException extends SuperAIException {
        public InventoryException(String message) {
            super(message, SuperAIConstants.ERROR_INTERNAL_ERROR);
        }

        public InventoryException(String message, Throwable cause) {
            super(message, cause, SuperAIConstants.ERROR_INTERNAL_ERROR);
        }

        public InventoryException(String message, int errorCode) {
            super(message, errorCode);
        }
    }

    /**
     * Exception thrown when trying to access invalid inventory slot.
     */
    public static class InvalidInventorySlotException extends InventoryException {
        public InvalidInventorySlotException(String message) {
            super(message, SuperAIConstants.ERROR_INVALID_PARAMETER);
        }

        public InvalidInventorySlotException(String message, String context) {
            super(message, SuperAIConstants.ERROR_INVALID_PARAMETER);
            // Note: context is not used in this simplified constructor
        }
    }

    /**
     * Exception thrown when inventory operation is not possible.
     */
    public static class InventoryOperationException extends InventoryException {
        public InventoryOperationException(String message) {
            super(message, SuperAIConstants.ERROR_OPERATION_NOT_ALLOWED);
        }

        public InventoryOperationException(String message, String context) {
            super(message, SuperAIConstants.ERROR_OPERATION_NOT_ALLOWED);
            // Note: context is not used in this simplified constructor
        }
    }

    // ============================================================================
    // Utility Methods
    // ============================================================================

    /**
     * Gets a human-readable description for an error code.
     *
     * @param errorCode the error code
     * @return human-readable description
     */
    public static String getErrorDescription(int errorCode) {
        return switch (errorCode) {
            case SuperAIConstants.ERROR_SUCCESS -> "Success";
            case SuperAIConstants.ERROR_INVALID_PARAMETER -> "Invalid parameter";
            case SuperAIConstants.ERROR_OPERATION_NOT_ALLOWED -> "Operation not allowed";
            case SuperAIConstants.ERROR_RESOURCE_NOT_FOUND -> "Resource not found";
            case SuperAIConstants.ERROR_TIMEOUT -> "Operation timeout";
            case SuperAIConstants.ERROR_INTERNAL_ERROR -> "Internal server error";
            case SuperAIConstants.ERROR_AUTHENTICATION_FAILED -> "Authentication failed";
            case SuperAIConstants.ERROR_RATE_LIMIT_EXCEEDED -> "Rate limit exceeded";
            case SuperAIConstants.ERROR_AGENT_NOT_REGISTERED -> "Agent not registered";
            case SuperAIConstants.ERROR_INVALID_AGENT_STATE -> "Invalid agent state";
            default -> "Unknown error";
        };
    }

    /**
     * Creates an exception with formatted message.
     *
     * @param exceptionClass the exception class to instantiate
     * @param messageFormat format string
     * @param args format arguments
     * @return new exception instance
     */
    public static SuperAIException createFormatted(Class<? extends SuperAIException> exceptionClass,
                                                   String messageFormat, Object... args) {
        String message = String.format(messageFormat, args);
        try {
            return exceptionClass.getConstructor(String.class).newInstance(message);
        } catch (Exception e) {
            // Fallback to generic exception - create a concrete implementation
            return new GrpcServiceException(message, SuperAIConstants.ERROR_INTERNAL_ERROR);
        }
    }

    // Private constructor to prevent instantiation
    private SuperAIExceptions() {
        throw new UnsupportedOperationException("Exceptions class cannot be instantiated");
    }
}
