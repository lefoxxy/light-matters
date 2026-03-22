package com.lefoxxy.lightmatters.client;

import com.lefoxxy.lightmatters.LightMattersMod;
import com.lefoxxy.lightmatters.gameplay.DarknessProfile;
import com.lefoxxy.lightmatters.gameplay.DarknessStage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = LightMattersMod.MODID, value = Dist.CLIENT)
public final class LightMattersClient {
    private static DarknessStage currentStage = DarknessStage.BRIGHT;

    private LightMattersClient() {
    }

    @SubscribeEvent
    public static void onClientPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) {
            return;
        }

        currentStage = DarknessProfile.sample(player).stage();
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || currentStage == DarknessStage.BRIGHT) {
            return;
        }

        int width = minecraft.getWindow().getGuiScaledWidth();
        int height = minecraft.getWindow().getGuiScaledHeight();
        int alpha = Mth.floor(currentStage.overlayAlpha() * 255.0F);
        int color = alpha << 24;

        event.getGuiGraphics().fill(0, 0, width, height, color);
    }
}
