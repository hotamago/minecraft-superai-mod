package com.supermc.ai;

import com.supermc.ai.common.SuperAIConstants;
import com.supermc.ai.common.SuperAIExceptions;
import com.supermc.ai.config.SuperAIConfig;
import com.supermc.ai.grpc.GrpcServerManager;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * Main mod class for SuperAI Minecraft Bot system.
 *
 * This mod provides a gRPC API that allows external AI agents to interact
 * with the Minecraft world intelligently, enabling AI-powered player behavior
 * rather than simple NPC entities.
 *
 * @author SuperAI Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Mod(SuperAIMod.MODID)
public final class SuperAIMod {
    /**
     * The unique mod identifier. Must match the mod_id in gradle.properties.
     */
    public static final String MODID = "superai";

    /**
     * Logger instance for this mod.
     */
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Constructs the SuperAI mod instance and initializes all subsystems.
     *
     * @param context The mod loading context provided by Forge
     */
    public SuperAIMod(FMLJavaModLoadingContext context) {
        // Note: Event registration will be handled by @Mod.EventBusSubscriber annotations
        // on the inner classes for client and server setup

        // Initialize core systems
        try {
            initializeSystems(context);
            LOGGER.info("{} v{} initialized successfully", SuperAIConstants.MOD_NAME, SuperAIConstants.MOD_VERSION);
        } catch (Exception e) {
            LOGGER.error("Failed to initialize SuperAI mod", e);
            throw new RuntimeException("SuperAI mod initialization failed", e);
        }
    }

    /**
     * Initializes all core systems of the SuperAI mod.
     *
     * @param context The mod loading context
     * @throws SuperAIExceptions.ConfigurationException if configuration validation fails
     */
    private void initializeSystems(FMLJavaModLoadingContext context) throws SuperAIExceptions.ConfigurationException {
        // Initialize configuration system first
        SuperAIConfig.initialize(context);

        // Validate configuration
        if (!SuperAIConfig.validateConfiguration()) {
            throw new SuperAIExceptions.ConfigurationException("Invalid configuration detected");
        }

        LOGGER.info("Configuration system initialized and validated");

        // Note: gRPC server will be started in client setup event
        // Other systems are initialized on-demand through gRPC services
    }

    /**
     * Common setup handler for initialization tasks that run on both client and server.
     *
     * @param event The common setup event
     */
    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("SuperAI common setup completed");
    }

    /**
     * Client-side event handlers and initialization.
     */
    @Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        /**
         * Client setup handler for client-specific initialization.
         *
         * @param event The client setup event
         */
        public static void onClientSetup(net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                try {
                    // Start gRPC server on client side
                    GrpcServerManager.getInstance().start();
                    LOGGER.info("SuperAI client setup completed - gRPC server started");
                } catch (SuperAIExceptions.GrpcServiceException e) {
                    LOGGER.error("Failed to start gRPC server", e);
                }
            });
        }
    }

    /**
     * Server-side event handlers and initialization.
     */
    @Mod.EventBusSubscriber(modid = MODID, value = Dist.DEDICATED_SERVER, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ServerModEvents {
        /**
         * Server setup handler for server-specific initialization.
         *
         * @param event The dedicated server setup event
         */
        public static void onServerSetup(FMLDedicatedServerSetupEvent event) {
            LOGGER.info("SuperAI dedicated server setup completed");
        }
    }
}
