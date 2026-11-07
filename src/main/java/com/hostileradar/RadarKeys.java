package com.hostileradar;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

/** Client only: the key that turns the radar on and off (H by default). */
@Mod.EventBusSubscriber(modid = HostileRadar.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RadarKeys {
    public static final KeyMapping TOGGLE = new KeyMapping(
            "key.hostileradar.toggle", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H, "key.categories.hostileradar");

    @SubscribeEvent
    public static void onRegisterKeys(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE);
    }
}
