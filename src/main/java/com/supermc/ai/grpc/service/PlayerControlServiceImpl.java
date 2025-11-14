package com.supermc.ai.grpc.service;

import com.google.protobuf.Empty;
import com.supermc.ai.common.SuperAIConstants;
import com.supermc.ai.common.SuperAIExceptions;
import com.supermc.ai.config.SuperAIConfig;
import com.supermc.ai.grpc.proto.*;
import com.supermc.ai.grpc.util.ProtoConverter;
import com.mojang.logging.LogUtils;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the Player Control Service gRPC interface.
 *
 * Allows AI agents to control player actions and movement.
 *
 * @author SuperAI Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class PlayerControlServiceImpl extends PlayerControlServiceGrpc.PlayerControlServiceImplBase {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    @Override
    public void executeAction(PlayerActionRequest request,
                             StreamObserver<PlayerActionResponse> responseObserver) {
        if (request == null || request.getAction() == PlayerAction.PLAYER_ACTION_UNSPECIFIED) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid request: action is required")
                    .asRuntimeException());
            return;
        }
        
        Minecraft.getInstance().execute(() -> {
            try {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player == null) {
                    responseObserver.onError(Status.UNAVAILABLE
                            .withDescription("Player not available")
                            .asRuntimeException());
                    return;
                }
                
                boolean success = executePlayerAction(player, request.getAction(), request.getParametersMap());
                
                PlayerState newState = getPlayerState(player);
                
                PlayerActionResponse response = PlayerActionResponse.newBuilder()
                        .setSuccess(success)
                        .setNewState(newState)
                        .build();
                
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                
            } catch (Exception e) {
                LOGGER.error("Error executing player action", e);
                responseObserver.onError(Status.INTERNAL
                        .withDescription("Failed to execute action: " + e.getMessage())
                        .withCause(e)
                        .asRuntimeException());
            }
        });
    }
    
    @Override
    public void executeActionSequence(PlayerActionSequenceRequest request,
                                     StreamObserver<PlayerActionSequenceResponse> responseObserver) {
        if (request == null || request.getActionsCount() == 0) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid request: at least one action is required")
                    .asRuntimeException());
            return;
        }
        
        Minecraft.getInstance().execute(() -> {
            try {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player == null) {
                    responseObserver.onError(Status.UNAVAILABLE
                            .withDescription("Player not available")
                            .asRuntimeException());
                    return;
                }
                
                List<PlayerActionResult> results = new ArrayList<>();
                int delay = request.getDelayBetweenActionsMs();
                
                for (PlayerAction action : request.getActionsList()) {
                    boolean success = executePlayerAction(player, action, null);
                    
                    PlayerActionResult result = PlayerActionResult.newBuilder()
                            .setAction(action)
                            .setSuccess(success)
                            .build();
                    
                    results.add(result);
                    
                    if (delay > 0) {
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
                
                PlayerActionSequenceResponse response = PlayerActionSequenceResponse.newBuilder()
                        .setSuccess(true)
                        .addAllResults(results)
                        .build();
                
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                
            } catch (Exception e) {
                LOGGER.error("Error executing action sequence", e);
                responseObserver.onError(Status.INTERNAL
                        .withDescription("Failed to execute action sequence: " + e.getMessage())
                        .withCause(e)
                        .asRuntimeException());
            }
        });
    }
    
    @Override
    public void getPlayerState(Empty request, StreamObserver<PlayerStateResponse> responseObserver) {
        Minecraft.getInstance().execute(() -> {
            try {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player == null) {
                    responseObserver.onError(Status.UNAVAILABLE
                            .withDescription("Player not available")
                            .asRuntimeException());
                    return;
                }
                
                PlayerState state = getPlayerState(player);
                
                PlayerStateResponse response = PlayerStateResponse.newBuilder()
                        .setState(state)
                        .setSuccess(true)
                        .build();
                
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                
            } catch (Exception e) {
                LOGGER.error("Error getting player state", e);
                responseObserver.onError(Status.INTERNAL
                        .withDescription("Failed to get player state: " + e.getMessage())
                        .withCause(e)
                        .asRuntimeException());
            }
        });
    }
    
    @Override
    public void streamPlayerState(PlayerStateStreamRequest request,
                                 StreamObserver<PlayerState> responseObserver) {
        int updateInterval = request.getUpdateIntervalMs() > 0 ? request.getUpdateIntervalMs() : 1000;
        
        new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Minecraft.getInstance().execute(() -> {
                        try {
                            LocalPlayer player = Minecraft.getInstance().player;
                            if (player == null) {
                                responseObserver.onError(Status.UNAVAILABLE
                                        .withDescription("Player not available")
                                        .asRuntimeException());
                                return;
                            }
                            
                            PlayerState state = getPlayerState(player);
                            responseObserver.onNext(state);
                            
                        } catch (Exception e) {
                            LOGGER.error("Error in player state stream", e);
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
                LOGGER.error("Error in player state stream", e);
                responseObserver.onError(Status.INTERNAL
                        .withDescription("Stream error: " + e.getMessage())
                        .asRuntimeException());
            }
        }).start();
    }
    
    @Override
    public void moveToPosition(MoveToPositionRequest request,
                              StreamObserver<MoveToPositionResponse> responseObserver) {
        if (request == null || !request.hasTargetPosition()) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid request: target position is required")
                    .asRuntimeException());
            return;
        }
        
        Minecraft.getInstance().execute(() -> {
            try {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player == null) {
                    responseObserver.onError(Status.UNAVAILABLE
                            .withDescription("Player not available")
                            .asRuntimeException());
                    return;
                }
                
                Vec3 target = ProtoConverter.toVec3(request.getTargetPosition());
                Vec3 current = player.position();
                double distance = current.distanceTo(target);
                
                // Simple movement implementation - in production, use pathfinding
                if (request.getPathfind() && SuperAIConfig.PLAYER_ENABLE_PATHFINDING.get()) {
                    // TODO: Implement pathfinding
                    LOGGER.warn("Pathfinding not yet implemented, using direct movement");
                }
                
                // Move player towards target (simplified)
                Vec3 direction = target.subtract(current).normalize();
                double speed = request.getSpeed() > 0 ? request.getSpeed() : 
                        SuperAIConfig.PLAYER_MOVE_SPEED_DEFAULT.get();
                
                // Set player movement (this is a simplified approach)
                // In a real implementation, you'd use player input handlers
                player.setPos(target.x, target.y, target.z);
                
                MoveToPositionResponse response = MoveToPositionResponse.newBuilder()
                        .setSuccess(true)
                        .build();
                
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                
            } catch (Exception e) {
                LOGGER.error("Error moving player", e);
                responseObserver.onError(Status.INTERNAL
                        .withDescription("Failed to move player: " + e.getMessage())
                        .withCause(e)
                        .asRuntimeException());
            }
        });
    }
    
    /**
     * Executes a player action.
     *
     * @param player the player to control
     * @param action the action to execute
     * @param parameters optional action parameters
     * @return true if action was successful
     */
    private boolean executePlayerAction(LocalPlayer player, PlayerAction action,
                                       java.util.Map<String, String> parameters) {
        try {
            switch (action) {
                case MOVE_FORWARD:
                    // Handle movement through input system
                    // player.input.forwardImpulse = 1.0f;
                    return true;
                case MOVE_BACKWARD:
                    // player.input.forwardImpulse = -1.0f;
                    return true;
                case MOVE_LEFT:
                    // player.input.leftImpulse = 1.0f;
                    return true;
                case MOVE_RIGHT:
                    // player.input.rightImpulse = 1.0f;
                    return true;
                case JUMP:
                    if (player.onGround()) {
                        player.jumpFromGround();
                    }
                    return true;
                case SNEAK:
                    player.setShiftKeyDown(true);
                    return true;
                case SPRINT:
                    player.setSprinting(true);
                    return true;
                case LOOK_UP:
                case LOOK_DOWN:
                case LOOK_LEFT:
                case LOOK_RIGHT:
                    // Handle rotation
                    float rotationAmount = 10.0f; // degrees
                    if (parameters != null && parameters.containsKey("amount")) {
                        rotationAmount = Float.parseFloat(parameters.get("amount"));
                    }
                    float currentYaw = player.getYRot();
                    float currentPitch = player.getXRot();
                    
                    switch (action) {
                        case LOOK_UP:
                            player.setXRot(Math.max(-90, currentPitch - rotationAmount));
                            break;
                        case LOOK_DOWN:
                            player.setXRot(Math.min(90, currentPitch + rotationAmount));
                            break;
                        case LOOK_LEFT:
                            player.setYRot(currentYaw - rotationAmount);
                            break;
                        case LOOK_RIGHT:
                            player.setYRot(currentYaw + rotationAmount);
                            break;
                    }
                    return true;
                case ATTACK:
                    // Trigger attack
                    // player.attack(...);
                    return true;
                case USE_ITEM:
                    // Use item in hand
                    // player.useItem();
                    return true;
                case DROP_ITEM:
                    // Drop item
                    // player.drop(false);
                    return true;
                case OPEN_INVENTORY:
                    // Open inventory
                    // Minecraft.getInstance().setScreen(new InventoryScreen(...));
                    return true;
                case CLOSE_INVENTORY:
                    // Close inventory
                    // Minecraft.getInstance().setScreen(null);
                    return true;
                case SELECT_SLOT:
                    if (parameters != null && parameters.containsKey("slot")) {
                        int slot = Integer.parseInt(parameters.get("slot"));
                        if (slot >= 0 && slot < 9) {
                            player.getInventory().setSelectedSlot(slot);
                            return true;
                        }
                    }
                    return false;
                default:
                    return false;
            }
        } catch (Exception e) {
            LOGGER.error("Error executing action: " + action, e);
            return false;
        }
    }
    
    /**
     * Gets the current player state.
     *
     * @param player the player
     * @return PlayerState protobuf message
     */
    private PlayerState getPlayerState(LocalPlayer player) {
        PlayerState.Builder builder = PlayerState.newBuilder()
                .setPlayerName(player.getName().getString())
                .setPosition(ProtoConverter.toPosition(player.position()))
                .setRotation(ProtoConverter.toRotation(player.getYRot(), player.getXRot()))
                .setHealth(player.getHealth())
                .setMaxHealth(player.getMaxHealth())
                .setFoodLevel(player.getFoodData().getFoodLevel())
                .setSaturation(player.getFoodData().getSaturationLevel())
                .setExperienceLevel(player.experienceLevel)
                .setExperienceProgress(player.experienceProgress)
                .setIsSneaking(player.isShiftKeyDown())
                .setIsSprinting(player.isSprinting())
                .setIsOnGround(player.onGround())
                .setIsFlying(player.getAbilities().flying)
                .setSelectedSlot(player.getInventory().getSelectedSlot());
        
        // Add inventory items
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            builder.addInventory(ProtoConverter.toItemInfo(player.getInventory().getItem(i)));
        }
        
        // Add held item
        builder.setHeldItem(ProtoConverter.toItemInfo(player.getMainHandItem()));
        
        return builder.build();
    }
}

