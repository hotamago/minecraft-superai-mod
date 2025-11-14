package com.supermc.ai.common;

/**
 * Base exception class for SuperAI Minecraft Bot.
 *
 * All custom exceptions in the SuperAI system should extend this class
 * to provide consistent error handling and logging.
 *
 * @author SuperAI Team
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class SuperAIException extends Exception {

    /** Error code for this exception */
    private final int errorCode;

    /** Additional context information */
    private final String context;

    /**
     * Constructs a new SuperAI exception with the specified message.
     *
     * @param message the detail message
     * @param errorCode the error code
     */
    protected SuperAIException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.context = null;
    }

    /**
     * Constructs a new SuperAI exception with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause of this exception
     * @param errorCode the error code
     */
    protected SuperAIException(String message, Throwable cause, int errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
        this.context = null;
    }


    /**
     * Constructs a new SuperAI exception with the specified message, cause, error code, and context.
     *
     * @param message the detail message
     * @param cause the cause of this exception
     * @param errorCode the error code
     * @param context additional context information
     */
    protected SuperAIException(String message, Throwable cause, int errorCode, String context) {
        super(message, cause);
        this.errorCode = errorCode;
        this.context = context;
    }

    /**
     * Gets the error code for this exception.
     *
     * @return the error code
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Gets the context information for this exception.
     *
     * @return the context, or null if not provided
     */
    public String getContext() {
        return context;
    }

    /**
     * Gets a formatted error message including the error code.
     *
     * @return formatted error message
     */
    public String getFormattedMessage() {
        return String.format("[Error %d] %s", errorCode, getMessage());
    }

    /**
     * Gets a detailed error message including context if available.
     *
     * @return detailed error message
     */
    public String getDetailedMessage() {
        StringBuilder sb = new StringBuilder(getFormattedMessage());
        if (context != null && !context.isEmpty()) {
            sb.append(" (Context: ").append(context).append(")");
        }
        return sb.toString();
    }
}
