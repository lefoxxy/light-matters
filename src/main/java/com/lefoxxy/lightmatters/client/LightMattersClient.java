package com.lefoxxy.lightmatters.client;

import com.lefoxxy.lightmatters.LightMattersMod;
import com.lefoxxy.lightmatters.gameplay.DarknessProfile;
import com.lefoxxy.lightmatters.gameplay.DarknessStage;
import com.lefoxxy.lightmatters.item.FuelLanternItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = LightMattersMod.MODID, value = Dist.CLIENT)
public final class LightMattersClient {
    private static DarknessStage currentStage = DarknessStage.BRIGHT;

    private LightMattersClient() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            registerLanternProperty(LightMattersMod.WOOD_LANTERN.get());
            registerLanternProperty(LightMattersMod.IRON_LANTERN.get());
            registerLanternProperty(LightMattersMod.GOLD_LANTERN.get());
            registerLanternProperty(LightMattersMod.DIAMOND_LANTERN.get());
            registerLanternProperty(LightMattersMod.NETHERITE_LANTERN.get());
            registerLanternProperty(LightMattersMod.CREATIVE_LANTERN.get());
        });
    }

    private static void registerLanternProperty(net.minecraft.world.item.Item item) {
        ItemProperties.register(
                item,
                ResourceLocation.fromNamespaceAndPath(LightMattersMod.MODID, "lit"),
                (stack, level, entity, seed) -> FuelLanternItem.isLit(stack) ? 1.0F : 0.0F);
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
