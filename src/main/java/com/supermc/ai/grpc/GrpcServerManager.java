package com.supermc.ai.grpc;

import com.supermc.ai.common.SuperAIConstants;
import com.supermc.ai.common.SuperAIExceptions;
import com.supermc.ai.config.SuperAIConfig;
import com.supermc.ai.grpc.service.AIAgentServiceImpl;
import com.supermc.ai.grpc.service.EnvironmentServiceImpl;
import com.supermc.ai.grpc.service.InventoryServiceImpl;
import com.supermc.ai.grpc.service.PlayerControlServiceImpl;
import com.mojang.logging.LogUtils;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Manages the gRPC server lifecycle for SuperAI Minecraft Bot.
 *
 * This class handles starting, stopping, and managing the gRPC server
 * that exposes the SuperAI API to external AI agents.
 *
 * @author SuperAI Team
 * @version 1.0.0
 * @since 1.0.0
 */
public final class GrpcServerManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private static GrpcServerManager instance;
    private Server grpcServer;
    private boolean isRunning = false;
    
    /**
     * Private constructor for singleton pattern.
     */
    private GrpcServerManager() {
    }
    
    /**
     * Gets the singleton instance of GrpcServerManager.
     *
     * @return the singleton instance
     */
    public static synchronized GrpcServerManager getInstance() {
        if (instance == null) {
            instance = new GrpcServerManager();
        }
        return instance;
    }
    
    /**
     * Starts the gRPC server with all registered services.
     *
     * @throws SuperAIExceptions.GrpcServiceException if server fails to start
     */
    public void start() throws SuperAIExceptions.GrpcServiceException {
        if (isRunning) {
            LOGGER.warn("gRPC server is already running");
            return;
        }
        
        try {
            String host = SuperAIConfig.GRPC_HOST.get();
            int port = SuperAIConfig.GRPC_PORT.get();
            
            LOGGER.info("Starting gRPC server on {}:{}", host, port);
            
            // Build server with all services
            ServerBuilder<?> serverBuilder = NettyServerBuilder.forPort(port)
                    .addService(new EnvironmentServiceImpl())
                    .addService(new PlayerControlServiceImpl())
                    .addService(new InventoryServiceImpl())
                    .addService(new AIAgentServiceImpl())
                    .maxInboundMessageSize(SuperAIConfig.SECURITY_MAX_REQUEST_SIZE_BYTES.get())
                    .permitKeepAliveWithoutCalls(true)
                    .permitKeepAliveTime(30, TimeUnit.SECONDS);
            
            // Add interceptors if needed (authentication, rate limiting)
            // serverBuilder.intercept(new AuthenticationInterceptor());
            // serverBuilder.intercept(new RateLimitingInterceptor());
            
            grpcServer = serverBuilder.build();
            grpcServer.start();
            
            isRunning = true;
            LOGGER.info("gRPC server started successfully on {}:{}", host, port);
            
            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
            
        } catch (IOException e) {
            LOGGER.error("Failed to start gRPC server", e);
            throw new SuperAIExceptions.GrpcServiceException("Failed to start gRPC server", e);
        }
    }
    
    /**
     * Stops the gRPC server gracefully.
     */
    public void stop() {
        if (!isRunning || grpcServer == null) {
            return;
        }
        
        LOGGER.info("Stopping gRPC server...");
        isRunning = false;
        
        try {
            if (grpcServer != null) {
                grpcServer.shutdown();
                if (!grpcServer.awaitTermination(5, TimeUnit.SECONDS)) {
                    LOGGER.warn("gRPC server did not terminate gracefully, forcing shutdown");
                    grpcServer.shutdownNow();
                    if (!grpcServer.awaitTermination(5, TimeUnit.SECONDS)) {
                        LOGGER.error("gRPC server did not terminate");
                    }
                }
            }
            LOGGER.info("gRPC server stopped");
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted while stopping gRPC server", e);
            Thread.currentThread().interrupt();
            if (grpcServer != null) {
                grpcServer.shutdownNow();
            }
        }
    }
    
    /**
     * Checks if the gRPC server is currently running.
     *
     * @return true if server is running, false otherwise
     */
    public boolean isRunning() {
        return isRunning && grpcServer != null && !grpcServer.isShutdown();
    }
    
    /**
     * Gets the port the server is listening on.
     *
     * @return the port number, or -1 if server is not running
     */
    public int getPort() {
        if (grpcServer != null && isRunning) {
            return grpcServer.getPort();
        }
        return -1;
    }
}

