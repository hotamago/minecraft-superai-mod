package com.supermc.ai.grpc.service;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import com.supermc.ai.common.SuperAIConstants;
import com.supermc.ai.common.SuperAIExceptions;
import com.supermc.ai.config.SuperAIConfig;
import com.supermc.ai.grpc.proto.*;
import com.supermc.ai.grpc.util.ProtoConverter;
import com.mojang.logging.LogUtils;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the Environment Service gRPC interface.
 *
 * Provides environment scanning and perception capabilities for AI agents.
 *
 * @author SuperAI Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class EnvironmentServiceImpl extends EnvironmentServiceGrpc.EnvironmentServiceImplBase {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    @Override
    public void scanEnvironment(EnvironmentScanRequest request,
                                StreamObserver<EnvironmentScanResponse> responseObserver) {
        // Validate request
        if (request == null || !request.hasCenter()) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid request: center position is required")
                    .asRuntimeException());
            return;
        }
        
        int radius = request.getRadius() > 0 ? request.getRadius() : SuperAIConfig.SCAN_RADIUS_DEFAULT.get();
        int maxRadius = SuperAIConfig.SCAN_RADIUS_MAX.get();
        
        if (radius > maxRadius) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(String.format("Scan radius %d exceeds maximum %d", radius, maxRadius))
                    .asRuntimeException());
            return;
        }
        
        // Dispatch to main thread for Minecraft operations
        Minecraft.getInstance().execute(() -> {
            try {
                Level level = Minecraft.getInstance().level;
                if (level == null) {
                    responseObserver.onError(Status.UNAVAILABLE
                            .withDescription("World not loaded")
                            .asRuntimeException());
                    return;
                }
                
                BlockPos center = ProtoConverter.toBlockPos(request.getCenter());
                boolean includeAir = request.getIncludeAirBlocks();
                boolean includeEntities = request.getIncludeEntities();
                int maxBlocks = request.getMaxBlocks() > 0 ? request.getMaxBlocks() : SuperAIConfig.SCAN_MAX_BLOCKS.get();
                int maxEntities = request.getMaxEntities() > 0 ? request.getMaxEntities() : SuperAIConfig.SCAN_MAX_ENTITIES.get();
                
                // Scan blocks
                List<BlockInfo> blocks = new ArrayList<>();
                int blocksScanned = 0;
                
                for (int x = -radius; x <= radius && blocksScanned < maxBlocks; x++) {
                    for (int y = -radius; y <= radius && blocksScanned < maxBlocks; y++) {
                        for (int z = -radius; z <= radius && blocksScanned < maxBlocks; z++) {
                            BlockPos pos = center.offset(x, y, z);
                            BlockState blockState = level.getBlockState(pos);
                            
                            if (includeAir || !blockState.isAir()) {
                                blocks.add(ProtoConverter.toBlockInfo(blockState, pos));
                                blocksScanned++;
                            }
                        }
                    }
                }
                
                // Scan entities
                List<EntityInfo> entities = new ArrayList<>();
                if (includeEntities) {
                    List<Entity> nearbyEntities = level.getEntitiesOfClass(Entity.class,
                            net.minecraft.world.phys.AABB.ofSize(
                                    net.minecraft.world.phys.Vec3.atCenterOf(center),
                                    radius * 2.0, radius * 2.0, radius * 2.0));
                    
                    for (int i = 0; i < Math.min(nearbyEntities.size(), maxEntities); i++) {
                        entities.add(ProtoConverter.toEntityInfo(nearbyEntities.get(i)));
                    }
                }
                
                // Build response
                EnvironmentScan scan = EnvironmentScan.newBuilder()
                        .setCenter(request.getCenter())
                        .setRadius(radius)
                        .addAllBlocks(blocks)
                        .addAllEntities(entities)
                        .setScanTime(Timestamp.newBuilder()
                                .setSeconds(Instant.now().getEpochSecond())
                                .setNanos(Instant.now().getNano()))
                        .setDimension(getDimensionName(level))
                        .setIncludeAirBlocks(includeAir)
                        .setMaxBlocks(maxBlocks)
                        .setMaxEntities(maxEntities)
                        .build();
                
                EnvironmentScanResponse response = EnvironmentScanResponse.newBuilder()
                        .setScan(scan)
                        .setSuccess(true)
                        .build();
                
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                
            } catch (Exception e) {
                LOGGER.error("Error scanning environment", e);
                responseObserver.onError(Status.INTERNAL
                        .withDescription("Failed to scan environment: " + e.getMessage())
                        .withCause(e)
                        .asRuntimeException());
            }
        });
    }
    
    @Override
    public void streamEnvironmentUpdates(EnvironmentUpdateRequest request,
                                        StreamObserver<EnvironmentScan> responseObserver) {
        // Validate request
        if (request == null || !request.hasCenter()) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid request: center position is required")
                    .asRuntimeException());
            return;
        }
        
        int radius = request.getRadius() > 0 ? request.getRadius() : SuperAIConfig.SCAN_RADIUS_DEFAULT.get();
        int updateInterval = request.getUpdateIntervalMs() > 0 ? request.getUpdateIntervalMs() : 
                SuperAIConfig.SCAN_UPDATE_INTERVAL_MS.get();
        
        // Start streaming updates on a background thread
        new Thread(() -> {
            try {
                while (!responseObserver.getClass().getSimpleName().contains("Cancelled")) {
                    // Dispatch scan to main thread
                    Minecraft.getInstance().execute(() -> {
                        try {
                            Level level = Minecraft.getInstance().level;
                            if (level == null) {
                                responseObserver.onError(Status.UNAVAILABLE
                                        .withDescription("World not loaded")
                                        .asRuntimeException());
                                return;
                            }
                            
                            BlockPos center = ProtoConverter.toBlockPos(request.getCenter());
                            
                            // Perform scan (simplified for streaming)
                            EnvironmentScan scan = EnvironmentScan.newBuilder()
                                    .setCenter(request.getCenter())
                                    .setRadius(radius)
                                    .setScanTime(Timestamp.newBuilder()
                                            .setSeconds(Instant.now().getEpochSecond())
                                            .setNanos(Instant.now().getNano()))
                                    .setDimension(getDimensionName(level))
                                    .setIncludeAirBlocks(request.getIncludeAirBlocks())
                                    .build();
                            
                            responseObserver.onNext(scan);
                            
                        } catch (Exception e) {
                            LOGGER.error("Error in environment stream", e);
                            responseObserver.onError(Status.INTERNAL
                                    .withDescription("Stream error: " + e.getMessage())
                                    .asRuntimeException());
                        }
                    });
                    
                    Thread.sleep(updateInterval);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                responseObserver.onCompleted();
            } catch (Exception e) {
                LOGGER.error("Error in environment update stream", e);
                responseObserver.onError(Status.INTERNAL
                        .withDescription("Stream error: " + e.getMessage())
                        .asRuntimeException());
            }
        }).start();
    }
    
    @Override
    public void getBlockInfo(BlockInfoRequest request,
                            StreamObserver<BlockInfoResponse> responseObserver) {
        if (request == null || !request.hasPosition()) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid request: position is required")
                    .asRuntimeException());
            return;
        }
        
        Minecraft.getInstance().execute(() -> {
            try {
                Level level = Minecraft.getInstance().level;
                if (level == null) {
                    responseObserver.onError(Status.UNAVAILABLE
                            .withDescription("World not loaded")
                            .asRuntimeException());
                    return;
                }
                
                BlockPos pos = ProtoConverter.toBlockPos(request.getPosition());
                BlockState blockState = level.getBlockState(pos);
                
                BlockInfo blockInfo = ProtoConverter.toBlockInfo(blockState, pos);
                
                BlockInfoResponse response = BlockInfoResponse.newBuilder()
                        .setBlock(blockInfo)
                        .setSuccess(true)
                        .build();
                
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                
            } catch (Exception e) {
                LOGGER.error("Error getting block info", e);
                responseObserver.onError(Status.INTERNAL
                        .withDescription("Failed to get block info: " + e.getMessage())
                        .withCause(e)
                        .asRuntimeException());
            }
        });
    }
    
    @Override
    public void getEntityInfo(EntityInfoRequest request,
                             StreamObserver<EntityInfoResponse> responseObserver) {
        if (request == null || request.getEntityId().isEmpty()) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid request: entity ID is required")
                    .asRuntimeException());
            return;
        }
        
        Minecraft.getInstance().execute(() -> {
            try {
                Level level = Minecraft.getInstance().level;
                if (level == null) {
                    responseObserver.onError(Status.UNAVAILABLE
                            .withDescription("World not loaded")
                            .asRuntimeException());
                    return;
                }
                
                // Find entity by ID (simplified - in real implementation, maintain entity registry)
                // For now, return error as entity lookup by ID requires tracking
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("Entity lookup by ID not yet implemented")
                        .asRuntimeException());
                
            } catch (Exception e) {
                LOGGER.error("Error getting entity info", e);
                responseObserver.onError(Status.INTERNAL
                        .withDescription("Failed to get entity info: " + e.getMessage())
                        .withCause(e)
                        .asRuntimeException());
            }
        });
    }
    
    /**
     * Gets the dimension name from the level.
     *
     * @param level the Minecraft level
     * @return dimension name string
     */
    private String getDimensionName(Level level) {
        if (level.dimension() == net.minecraft.world.level.Level.OVERWORLD) {
            return "overworld";
        } else if (level.dimension() == net.minecraft.world.level.Level.NETHER) {
            return "nether";
        } else if (level.dimension() == net.minecraft.world.level.Level.END) {
            return "end";
        }
        return "unknown";
    }
}

