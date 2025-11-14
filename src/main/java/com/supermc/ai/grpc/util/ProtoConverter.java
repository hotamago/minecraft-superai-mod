package com.supermc.ai.grpc.util;

import com.supermc.ai.grpc.proto.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for converting between Minecraft types and Protocol Buffer types.
 *
 * This class provides static methods to convert Minecraft game objects
 * to their corresponding protobuf message representations.
 *
 * @author SuperAI Team
 * @version 1.0.0
 * @since 1.0.0
 */
public final class ProtoConverter {
    
    private ProtoConverter() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Converts a Minecraft Vec3 to a Position protobuf message.
     *
     * @param vec3 the Minecraft Vec3
     * @return the Position protobuf message
     */
    public static Position toPosition(Vec3 vec3) {
        return Position.newBuilder()
                .setX(vec3.x)
                .setY(vec3.y)
                .setZ(vec3.z)
                .build();
    }
    
    /**
     * Converts a Minecraft BlockPos to a Position protobuf message.
     *
     * @param pos the Minecraft BlockPos
     * @return the Position protobuf message
     */
    public static Position toPosition(BlockPos pos) {
        return Position.newBuilder()
                .setX(pos.getX())
                .setY(pos.getY())
                .setZ(pos.getZ())
                .build();
    }
    
    /**
     * Converts a Position protobuf message to a Minecraft BlockPos.
     *
     * @param position the Position protobuf message
     * @return the Minecraft BlockPos
     */
    public static BlockPos toBlockPos(Position position) {
        return new BlockPos(
                (int) Math.floor(position.getX()),
                (int) Math.floor(position.getY()),
                (int) Math.floor(position.getZ())
        );
    }
    
    /**
     * Converts a Position protobuf message to a Minecraft Vec3.
     *
     * @param position the Position protobuf message
     * @return the Minecraft Vec3
     */
    public static Vec3 toVec3(Position position) {
        return new Vec3(position.getX(), position.getY(), position.getZ());
    }
    
    /**
     * Converts player rotation to a Rotation protobuf message.
     *
     * @param yaw the yaw angle in degrees
     * @param pitch the pitch angle in degrees
     * @return the Rotation protobuf message
     */
    public static Rotation toRotation(float yaw, float pitch) {
        return Rotation.newBuilder()
                .setYaw(yaw)
                .setPitch(pitch)
                .build();
    }
    
    /**
     * Converts a BlockState to BlockType enum.
     *
     * @param blockState the Minecraft BlockState
     * @return the BlockType enum value
     */
    public static BlockType toBlockType(BlockState blockState) {
        Block block = blockState.getBlock();
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
        
        if (blockId == null) {
            return BlockType.UNKNOWN;
        }
        
        String path = blockId.getPath();
        
        // Map common block types
        if (path.contains("air")) {
            return BlockType.AIR;
        } else if (path.contains("stone")) {
            return BlockType.STONE;
        } else if (path.contains("grass")) {
            return BlockType.GRASS;
        } else if (path.contains("dirt")) {
            return BlockType.DIRT;
        } else if (path.contains("wood") || path.contains("log")) {
            return BlockType.WOOD;
        } else if (path.contains("leaves")) {
            return BlockType.LEAVES;
        } else if (path.contains("water")) {
            return BlockType.WATER;
        } else if (path.contains("lava")) {
            return BlockType.LAVA;
        } else if (path.contains("sand")) {
            return BlockType.SAND;
        } else if (path.contains("gravel")) {
            return BlockType.GRAVEL;
        } else if (path.contains("ore")) {
            return BlockType.ORE;
        } else if (path.contains("chest")) {
            return BlockType.CHEST;
        } else if (path.contains("furnace")) {
            return BlockType.FURNACE;
        } else if (path.contains("crafting_table")) {
            return BlockType.CRAFTING_TABLE;
        } else if (path.contains("torch")) {
            return BlockType.TORCH;
        } else if (path.contains("door")) {
            return BlockType.DOOR;
        } else if (path.contains("trapdoor")) {
            return BlockType.TRAPDOOR;
        } else if (path.contains("fence")) {
            return BlockType.FENCE;
        } else if (path.contains("stairs")) {
            return BlockType.STAIRS;
        } else if (path.contains("slab")) {
            return BlockType.SLAB;
        } else if (path.contains("wall")) {
            return BlockType.WALL;
        } else if (path.contains("glass")) {
            return BlockType.GLASS;
        } else if (path.contains("wool")) {
            return BlockType.WOOL;
        } else if (path.contains("bed")) {
            return BlockType.BED;
        } else if (path.contains("rail")) {
            return BlockType.RAIL;
        } else if (path.contains("ladder")) {
            return BlockType.LADDER;
        } else if (path.contains("sign")) {
            return BlockType.SIGN;
        } else if (path.contains("tnt")) {
            return BlockType.TNT;
        } else if (path.contains("obsidian")) {
            return BlockType.OBSIDIAN;
        } else if (path.contains("portal")) {
            return BlockType.PORTAL;
        } else if (path.contains("cake")) {
            return BlockType.CAKE;
        } else if (path.contains("spawner")) {
            return BlockType.SPAWNER;
        } else if (path.contains("fire")) {
            return BlockType.FIRE;
        } else if (path.contains("bookshelf")) {
            return BlockType.BOOKSHELF;
        } else if (path.contains("plant") || path.contains("crop") || path.contains("sapling")) {
            if (path.contains("crop")) {
                return BlockType.CROP;
            } else if (path.contains("sapling")) {
                return BlockType.SAPLING;
            } else {
                return BlockType.PLANT;
            }
        }
        
        return BlockType.UNKNOWN;
    }
    
    /**
     * Converts a BlockState to BlockInfo protobuf message.
     *
     * @param blockState the Minecraft BlockState
     * @param pos the block position
     * @return the BlockInfo protobuf message
     */
    public static BlockInfo toBlockInfo(BlockState blockState, BlockPos pos) {
        Block block = blockState.getBlock();
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
        
        BlockInfo.Builder builder = BlockInfo.newBuilder()
                .setPosition(toPosition(pos))
                .setType(toBlockType(blockState))
                .setBlockId(blockId != null ? blockId.toString() : "unknown")
                .setBlockName(block.getName().getString())
                .setIsSolid(blockState.canOcclude())
                .setIsLiquid(blockState.getFluidState().isEmpty() == false)
                .setHardness(blockState.getDestroySpeed(null, null));
        
        // Add block properties
        Map<String, String> properties = new HashMap<>();
        blockState.getProperties().forEach(property -> {
            properties.put(property.getName(), blockState.getValue(property).toString());
        });
        builder.putAllProperties(properties);
        
        return builder.build();
    }
    
    /**
     * Converts an Entity to EntityInfo protobuf message.
     *
     * @param entity the Minecraft Entity
     * @return the EntityInfo protobuf message
     */
    public static EntityInfo toEntityInfo(Entity entity) {
        ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        
        EntityInfo.Builder builder = EntityInfo.newBuilder()
                .setEntityId(entityId != null ? entityId.toString() : "unknown")
                .setEntityType(entity.getType().getDescription().getString())
                .setPosition(toPosition(entity.position()))
                .setRotation(toRotation(entity.getYRot(), entity.getXRot()))
                .setIsAlive(entity.isAlive());
        
        // Add health if entity has health
        if (entity instanceof net.minecraft.world.entity.LivingEntity livingEntity) {
            builder.setHealth(livingEntity.getHealth())
                   .setMaxHealth(livingEntity.getMaxHealth());
        } else {
            builder.setHealth(1.0).setMaxHealth(1.0);
        }
        
        // Add entity attributes
        Map<String, String> attributes = new HashMap<>();
        attributes.put("onGround", String.valueOf(entity.onGround()));
        attributes.put("noGravity", String.valueOf(entity.isNoGravity()));
        builder.putAllAttributes(attributes);
        
        return builder.build();
    }
    
    /**
     * Converts an ItemStack to ItemInfo protobuf message.
     *
     * @param itemStack the Minecraft ItemStack
     * @return the ItemInfo protobuf message
     */
    public static ItemInfo toItemInfo(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return ItemInfo.newBuilder()
                    .setItemId("minecraft:air")
                    .setItemName("Air")
                    .setType(ItemType.ITEM_TYPE_UNSPECIFIED)
                    .setCount(0)
                    .build();
        }
        
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
        
        ItemInfo.Builder builder = ItemInfo.newBuilder()
                .setItemId(itemId != null ? itemId.toString() : "unknown")
                .setItemName(itemStack.getDisplayName().getString())
                .setCount(itemStack.getCount())
                .setMaxStackSize(itemStack.getMaxStackSize())
                .setIsEnchantable(itemStack.isEnchantable());
        
        // Determine item type
        ItemType itemType = determineItemType(itemStack);
        builder.setType(itemType);
        
        // Add damage info if applicable
        if (itemStack.isDamageableItem()) {
            builder.setDamage(itemStack.getDamageValue())
                   .setMaxDamage(itemStack.getMaxDamage());
        }
        
        // Add item properties
        Map<String, String> properties = new HashMap<>();
        if (!itemStack.getComponents().isEmpty()) {
            properties.put("hasNBT", "true");
        }
        builder.putAllProperties(properties);
        
        return builder.build();
    }
    
    /**
     * Determines the ItemType for an ItemStack.
     *
     * @param itemStack the ItemStack
     * @return the ItemType enum value
     */
    private static ItemType determineItemType(ItemStack itemStack) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
        if (itemId == null) {
            return ItemType.MISC;
        }
        
        String path = itemId.getPath();
        
        // Check for tools
        if (path.contains("sword") || path.contains("axe") || path.contains("pickaxe") ||
            path.contains("shovel") || path.contains("hoe") || path.contains("shears")) {
            return ItemType.TOOL;
        }
        
        // Check for weapons
        if (path.contains("bow") || path.contains("crossbow") || path.contains("trident")) {
            return ItemType.WEAPON;
        }
        
        // Check for armor
        if (path.contains("helmet") || path.contains("chestplate") ||
            path.contains("leggings") || path.contains("boots")) {
            return ItemType.ARMOR;
        }
        
        // Check for food - simplified check based on item registry name for now
        if (path.contains("apple") || path.contains("bread") || path.contains("beef") ||
            path.contains("chicken") || path.contains("pork") || path.contains("fish") ||
            path.contains("carrot") || path.contains("potato")) {
            return ItemType.FOOD;
        }
        
        // Check for potion
        if (path.contains("potion") || path.contains("splash") || path.contains("lingering")) {
            return ItemType.POTION;
        }
        
        // Check for book
        if (path.contains("book") || path.contains("enchanted_book")) {
            return ItemType.BOOK;
        }
        
        // Check if it's a block
        if (BuiltInRegistries.BLOCK.containsKey(itemId)) {
            return ItemType.BLOCK;
        }
        
        return ItemType.MISC;
    }
}

