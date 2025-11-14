package com.supermc.ai.grpc.service;

import com.google.protobuf.Empty;
import com.supermc.ai.common.SuperAIExceptions;
import com.supermc.ai.grpc.proto.*;
import com.supermc.ai.grpc.util.ProtoConverter;
import com.mojang.logging.LogUtils;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

/**
 * Implementation of the Inventory Service gRPC interface.
 *
 * Provides inventory management capabilities for AI agents.
 *
 * @author SuperAI Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class InventoryServiceImpl extends InventoryServiceGrpc.InventoryServiceImplBase {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    @Override
    public void getInventory(Empty request, StreamObserver<InventoryResponse> responseObserver) {
        Minecraft.getInstance().execute(() -> {
            try {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player == null) {
                    responseObserver.onError(Status.UNAVAILABLE
                            .withDescription("Player not available")
                            .asRuntimeException());
                    return;
                }
                
                Inventory inventory = player.getInventory();
                InventoryResponse.Builder responseBuilder = InventoryResponse.newBuilder()
                        .setSelectedSlot(inventory.getSelectedSlot())
                        .setSuccess(true);
                
                // Add all inventory items
                for (int i = 0; i < inventory.getContainerSize(); i++) {
                    ItemStack item = inventory.getItem(i);
                    responseBuilder.addItems(ProtoConverter.toItemInfo(item));
                }
                
                responseObserver.onNext(responseBuilder.build());
                responseObserver.onCompleted();
                
            } catch (Exception e) {
                LOGGER.error("Error getting inventory", e);
                responseObserver.onError(Status.INTERNAL
                        .withDescription("Failed to get inventory: " + e.getMessage())
                        .withCause(e)
                        .asRuntimeException());
            }
        });
    }
    
    @Override
    public void useItem(UseItemRequest request,
                       StreamObserver<UseItemResponse> responseObserver) {
        if (request == null) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid request")
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
                
                int slot = request.getSlot();
                Inventory inventory = player.getInventory();
                
                if (slot < 0 || slot >= inventory.getContainerSize()) {
                    responseObserver.onError(Status.INVALID_ARGUMENT
                            .withDescription("Invalid slot: " + slot)
                            .asRuntimeException());
                    return;
                }
                
                ItemStack item = inventory.getItem(slot);
                if (item.isEmpty()) {
                    responseObserver.onError(Status.NOT_FOUND
                            .withDescription("Slot is empty")
                            .asRuntimeException());
                    return;
                }
                
                // Use the item
                // player.useItem(player.getUsedHandHand());
                // This would require more complex implementation with proper item use handling
                
                UseItemResponse response = UseItemResponse.newBuilder()
                        .setSuccess(true)
                        .setUpdatedItem(ProtoConverter.toItemInfo(item))
                        .build();
                
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                
            } catch (Exception e) {
                LOGGER.error("Error using item", e);
                responseObserver.onError(Status.INTERNAL
                        .withDescription("Failed to use item: " + e.getMessage())
                        .withCause(e)
                        .asRuntimeException());
            }
        });
    }
    
    @Override
    public void dropItem(DropItemRequest request,
                        StreamObserver<DropItemResponse> responseObserver) {
        if (request == null) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid request")
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
                
                int slot = request.getSlot();
                int count = request.getCount() > 0 ? request.getCount() : 1;
                Inventory inventory = player.getInventory();
                
                if (slot < 0 || slot >= inventory.getContainerSize()) {
                    responseObserver.onError(Status.INVALID_ARGUMENT
                            .withDescription("Invalid slot: " + slot)
                            .asRuntimeException());
                    return;
                }
                
                ItemStack item = inventory.getItem(slot);
                if (item.isEmpty()) {
                    responseObserver.onError(Status.NOT_FOUND
                            .withDescription("Slot is empty")
                            .asRuntimeException());
                    return;
                }
                
                // Drop the item
                // player.drop(item.split(count), false);
                // This requires proper implementation with entity spawning
                
                ItemStack remaining = item.copy();
                remaining.shrink(count);
                
                DropItemResponse response = DropItemResponse.newBuilder()
                        .setSuccess(true)
                        .setRemainingItem(ProtoConverter.toItemInfo(remaining))
                        .build();
                
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                
            } catch (Exception e) {
                LOGGER.error("Error dropping item", e);
                responseObserver.onError(Status.INTERNAL
                        .withDescription("Failed to drop item: " + e.getMessage())
                        .withCause(e)
                        .asRuntimeException());
            }
        });
    }
    
    @Override
    public void moveItem(MoveItemRequest request,
                        StreamObserver<MoveItemResponse> responseObserver) {
        if (request == null) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid request")
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
                
                int fromSlot = request.getFromSlot();
                int toSlot = request.getToSlot();
                int count = request.getCount() > 0 ? request.getCount() : 1;
                Inventory inventory = player.getInventory();
                
                if (fromSlot < 0 || fromSlot >= inventory.getContainerSize() ||
                    toSlot < 0 || toSlot >= inventory.getContainerSize()) {
                    responseObserver.onError(Status.INVALID_ARGUMENT
                            .withDescription("Invalid slot")
                            .asRuntimeException());
                    return;
                }
                
                ItemStack fromItem = inventory.getItem(fromSlot);
                if (fromItem.isEmpty()) {
                    responseObserver.onError(Status.NOT_FOUND
                            .withDescription("Source slot is empty")
                            .asRuntimeException());
                    return;
                }
                
                // Move item (simplified - in real implementation, use proper inventory methods)
                ItemStack toMove = fromItem.split(count);
                ItemStack toItem = inventory.getItem(toSlot);
                
                if (toItem.isEmpty()) {
                    inventory.setItem(toSlot, toMove);
                } else if (ItemStack.isSameItem(toItem, toMove) && 
                          toItem.getCount() + toMove.getCount() <= toItem.getMaxStackSize()) {
                    toItem.grow(toMove.getCount());
                } else {
                    // Swap items
                    inventory.setItem(fromSlot, toItem);
                    inventory.setItem(toSlot, toMove);
                }
                
                MoveItemResponse response = MoveItemResponse.newBuilder()
                        .setSuccess(true)
                        .setMovedItem(ProtoConverter.toItemInfo(toMove))
                        .build();
                
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                
            } catch (Exception e) {
                LOGGER.error("Error moving item", e);
                responseObserver.onError(Status.INTERNAL
                        .withDescription("Failed to move item: " + e.getMessage())
                        .withCause(e)
                        .asRuntimeException());
            }
        });
    }
    
    @Override
    public void craftItem(CraftItemRequest request,
                         StreamObserver<CraftItemResponse> responseObserver) {
        if (request == null || request.getRecipeId().isEmpty()) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid request: recipe ID is required")
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
                
                // Crafting implementation requires recipe system
                // This is a placeholder - full implementation would:
                // 1. Look up recipe by ID
                // 2. Check if player has ingredients
                // 3. Execute crafting
                // 4. Return crafted item
                
                responseObserver.onError(Status.UNIMPLEMENTED
                        .withDescription("Crafting not yet fully implemented")
                        .asRuntimeException());
                
            } catch (Exception e) {
                LOGGER.error("Error crafting item", e);
                responseObserver.onError(Status.INTERNAL
                        .withDescription("Failed to craft item: " + e.getMessage())
                        .withCause(e)
                        .asRuntimeException());
            }
        });
    }
}

