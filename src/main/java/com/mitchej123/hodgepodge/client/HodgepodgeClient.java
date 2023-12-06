package com.mitchej123.hodgepodge.client;

import java.util.Collections;

import net.minecraftforge.common.MinecraftForge;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.Compat;
import com.mitchej123.hodgepodge.client.handlers.ClientKeyListener;
import com.mitchej123.hodgepodge.util.ManagedEnum;

import biomesoplenty.common.eventhandler.client.gui.WorldTypeMessageEventHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModMetadata;

public class HodgepodgeClient {

    public static final ManagedEnum<AnimationMode> animationsMode = new ManagedEnum<>(AnimationMode.VISIBLE_ONLY);
    public static final ManagedEnum<RenderDebugMode> renderDebugMode = new ManagedEnum<>(RenderDebugMode.REDUCED);

    public static void preInit() {
        if (Compat.isGT5Present()) {
            MinecraftForge.EVENT_BUS.register(PollutionTooltip.INSTANCE);
        }
    }

    public static void postInit() {

        if (Common.config.renderDebug) {
            renderDebugMode.set(Common.config.renderDebugMode);
        } else {
            renderDebugMode.set(RenderDebugMode.OFF);
        }

        if (Common.config.enableDefaultLanPort) {
            if (Common.config.defaultLanPort < 0 || Common.config.defaultLanPort > 65535) {
                Common.log.error(
                        String.format(
                                "Default LAN port number must be in range of 0-65535, but %s was given. Defaulting to 0.",
                                Common.config.defaultLanPort));
                Common.config.defaultLanPort = 0;
            }
        }

        if (Compat.isGT5Present()) {
            Common.config.postInitClient();
            MinecraftForge.EVENT_BUS.register(Common.config.standardBlocks);
            MinecraftForge.EVENT_BUS.register(Common.config.liquidBlocks);
            MinecraftForge.EVENT_BUS.register(Common.config.doublePlants);
            MinecraftForge.EVENT_BUS.register(Common.config.crossedSquares);
            MinecraftForge.EVENT_BUS.register(Common.config.blockVine);
        }

        FMLCommonHandler.instance().bus().register(ClientTicker.INSTANCE);

        if (Common.config.addSystemInfo) {
            MinecraftForge.EVENT_BUS.register(DebugScreenHandler.INSTANCE);
        }

        if (Common.config.speedupAnimations) {
            FMLCommonHandler.instance().bus().register(new ClientKeyListener());
        }

        if (Compat.isIC2CropPluginPresent()) {
            ModMetadata meta = Loader.instance().getIndexedModList().get("Ic2Nei").getMetadata();
            meta.authorList = Collections.singletonList("Speiger");
            meta.description = "This IC2 Addon allows you to simulate the CropBreeding.";
            meta.autogenerated = false;
        }

        if (Compat.isBiomesOPlentyPresent()) {
            // removes the popup that BOP shows on first world gen
            MinecraftForge.EVENT_BUS.unregister(WorldTypeMessageEventHandler.instance);
        }
    }

    public enum AnimationMode {
        NONE,
        VISIBLE_ONLY,
        ALL
    }

    public enum RenderDebugMode {
        OFF,
        REDUCED,
        FULL
    }
}
