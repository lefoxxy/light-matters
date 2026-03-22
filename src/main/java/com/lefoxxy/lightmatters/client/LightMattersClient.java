package com.lefoxxy.lightmatters.client;

import com.lefoxxy.lightmatters.LightMattersMod;
import com.lefoxxy.lightmatters.gameplay.DarknessProfile;
import com.lefoxxy.lightmatters.gameplay.DarknessStage;
import com.lefoxxy.lightmatters.item.FuelLanternItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = LightMattersMod.MODID, value = Dist.CLIENT)
public final class LightMattersClient {
    private static DarknessStage currentStage = DarknessStage.BRIGHT;
    private static float panicPulse;
    private static int panicHeartbeatCooldown;
    private static int fatigueBreathCooldown;

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
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            panicPulse = 0.0F;
            return;
        }

        boolean panicActive = player.hasEffect(LightMattersMod.PANIC);
        boolean fatigueActive = player.hasEffect(LightMattersMod.FATIGUE);

        if (panicActive) {
            panicPulse += 0.18F;
            if (panicHeartbeatCooldown-- <= 0) {
                player.level().playLocalSound(player.getX(), player.getY(), player.getZ(), LightMattersMod.PANIC_HEARTBEAT_SOUND.get(), SoundSource.PLAYERS, 0.65F, 1.05F, false);
                panicHeartbeatCooldown = 28;
            }

            if (player.tickCount % 6 == 0) {
                player.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, player.getX(), player.getEyeY() - 0.25D, player.getZ(), 0.0D, 0.01D, 0.0D);
            }
        } else {
            panicHeartbeatCooldown = 8;
        }

        if (fatigueActive) {
            if (fatigueBreathCooldown-- <= 0) {
                player.level().playLocalSound(player.getX(), player.getY(), player.getZ(), LightMattersMod.FATIGUE_BREATH_SOUND.get(), SoundSource.PLAYERS, 0.4F, 0.85F, false);
                fatigueBreathCooldown = 70;
            }

            if (player.tickCount % 10 == 0) {
                player.level().addParticle(ParticleTypes.ASH, player.getX(), player.getEyeY() - 0.2D, player.getZ(), 0.0D, 0.005D, 0.0D);
            }
        } else {
            fatigueBreathCooldown = 20;
        }
    }

    @SubscribeEvent
    public static void onCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || !player.hasEffect(LightMattersMod.PANIC)) {
            return;
        }

        float phase = (player.tickCount + (float)event.getPartialTick()) * 0.35F;
        event.setYaw(event.getYaw() + Mth.sin(phase) * 0.45F);
        event.setPitch(event.getPitch() + Mth.cos(phase * 0.8F) * 0.35F);
        event.setRoll(event.getRoll() + Mth.sin(phase * 0.5F) * 0.6F);
    }

    @SubscribeEvent
    public static void onComputeFov(ViewportEvent.ComputeFov event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }

        if (player.hasEffect(LightMattersMod.FATIGUE)) {
            event.setFOV(event.getFOV() * 0.97D);
        }

        if (player.hasEffect(LightMattersMod.PANIC)) {
            event.setFOV(event.getFOV() * 0.985D);
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (minecraft.options.hideGui || player == null) {
            return;
        }

        int width = minecraft.getWindow().getGuiScaledWidth();
        int height = minecraft.getWindow().getGuiScaledHeight();
        int alpha = Mth.floor(currentStage.overlayAlpha() * 255.0F);
        int color = alpha << 24;

        if (currentStage != DarknessStage.BRIGHT) {
            event.getGuiGraphics().fill(0, 0, width, height, color);
        }

        if (player.hasEffect(LightMattersMod.PANIC)) {
            float pulse = (Mth.sin(panicPulse) + 1.0F) * 0.5F;
            int panicAlpha = Mth.floor((0.08F + (pulse * 0.07F)) * 255.0F);
            int panicColor = (panicAlpha << 24) | 0x6A1808;
            event.getGuiGraphics().fill(0, 0, width, height, panicColor);
        }
    }
}
