package com.hostileradar;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Client only: draws the radar in the bottom-right corner of the screen. */
@Mod.EventBusSubscriber(modid = HostileRadar.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class RadarHud {
    private static final int SIZE = 72;
    private static final double RANGE = 24.0D;
    private static final int MARGIN = 8;

    private static boolean enabled = true;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        while (RadarKeys.TOGGLE.consumeClick()) {
            enabled = !enabled;
        }
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() != VanillaGuiOverlay.HOTBAR.type() || !enabled) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null || mc.options.hideGui) {
            return;
        }

        GuiGraphics g = event.getGuiGraphics();
        int x = mc.getWindow().getGuiScaledWidth() - SIZE - MARGIN;
        int y = mc.getWindow().getGuiScaledHeight() - SIZE - MARGIN;
        int cx = x + SIZE / 2;
        int cy = y + SIZE / 2;

        // Background, border and crosshair
        int line = 0xFF2ECC71;
        g.fill(x, y, x + SIZE, y + SIZE, 0xAA0B1A10);
        g.fill(x, y, x + SIZE, y + 1, line);
        g.fill(x, y + SIZE - 1, x + SIZE, y + SIZE, line);
        g.fill(x, y, x + 1, y + SIZE, line);
        g.fill(x + SIZE - 1, y, x + SIZE, y + SIZE, line);
        g.fill(cx, y + 1, cx + 1, y + SIZE - 1, 0x442ECC71);
        g.fill(x + 1, cy, x + SIZE - 1, cy + 1, 0x442ECC71);

        // The top of the radar is where the player is looking
        float yaw = player.getViewYRot(event.getPartialTick()) * ((float) Math.PI / 180.0F);
        double forwardX = -Math.sin(yaw);
        double forwardZ = Math.cos(yaw);
        double rightX = -Math.cos(yaw);
        double rightZ = -Math.sin(yaw);
        double scale = (SIZE / 2.0D - 3.0D) / RANGE;

        AABB box = player.getBoundingBox().inflate(RANGE);
        List<Mob> mobs = mc.level.getEntitiesOfClass(Mob.class, box, m -> m instanceof Enemy && m.isAlive());

        int count = 0;
        for (Mob mob : mobs) {
            double dx = mob.getX() - player.getX();
            double dz = mob.getZ() - player.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist > RANGE) {
                continue;
            }
            count++;
            int px = cx + (int) Math.round((dx * rightX + dz * rightZ) * scale);
            int py = cy - (int) Math.round((dx * forwardX + dz * forwardZ) * scale);
            int color = dist < 8.0D ? 0xFFFF3B30 : (dist < 16.0D ? 0xFFFF9500 : 0xFFFFD60A);
            g.fill(px - 1, py - 1, px + 2, py + 2, color);
        }

        // The player in the middle
        g.fill(cx - 1, cy - 1, cx + 2, cy + 2, 0xFFFFFFFF);
        g.drawString(mc.font, Component.translatable("hud.hostileradar.count", count), x, y - 10, 0xFFFFFFFF, true);
    }
}
