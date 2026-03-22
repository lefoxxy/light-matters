package com.lefoxxy.lightmatters.client;

import com.lefoxxy.lightmatters.LightMattersMod;
import com.lefoxxy.lightmatters.gameplay.DarknessProfile;
import com.lefoxxy.lightmatters.gameplay.DarknessSystem;
import com.lefoxxy.lightmatters.gameplay.DarknessStage;
import com.lefoxxy.lightmatters.item.FuelLanternItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = LightMattersMod.MODID, value = Dist.CLIENT)
public final class LightMattersClient {
    private static DarknessStage currentStage = DarknessStage.BRIGHT;
    private static DarknessProfile currentProfile;
    private static float panicPulse;
    private static float pressureFill;
    private static float exposureFill;
    private static int clientPitchBlackExposure;
    private static int panicHeartbeatCooldown;
    private static int fatigueBreathCooldown;

    private LightMattersClient() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        LightMattersClientConfig.load();
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

        if (player.isCreative() || player.isSpectator()) {
            currentProfile = null;
            currentStage = DarknessStage.BRIGHT;
            pressureFill = 0.0F;
            exposureFill = 0.0F;
            clientPitchBlackExposure = 0;
            return;
        }

        currentProfile = DarknessProfile.sample(player);
        currentStage = currentProfile.stage();
        if (currentStage.isSevere()) {
            clientPitchBlackExposure = Math.min(DarknessSystem.getPanicTriggerTicks(), clientPitchBlackExposure + 1);
        } else {
            clientPitchBlackExposure = Math.max(0, clientPitchBlackExposure - 2);
        }

        pressureFill = Mth.lerp(0.18F, pressureFill, getPressureFill(currentProfile));
        exposureFill = Mth.lerp(0.18F, exposureFill, Mth.clamp((float) clientPitchBlackExposure / (float) DarknessSystem.getPanicTriggerTicks(), 0.0F, 1.0F));
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || player.isCreative() || player.isSpectator()) {
            panicPulse = 0.0F;
            panicHeartbeatCooldown = 8;
            fatigueBreathCooldown = 20;
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
        if (player == null || player.isCreative() || player.isSpectator() || !player.hasEffect(LightMattersMod.PANIC)) {
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
        if (player == null || player.isCreative() || player.isSpectator()) {
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
        if (minecraft.options.hideGui || player == null || player.isCreative() || player.isSpectator()) {
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

        if (LightMattersClientConfig.isHudEnabled()) {
            renderDarknessMeter(event, minecraft, player, width, height);
        }
    }

    private static void renderDarknessMeter(RenderGuiEvent.Post event, Minecraft minecraft, LocalPlayer player, int width, int height) {
        if (currentProfile == null) {
            return;
        }

        boolean showMeter = currentStage != DarknessStage.BRIGHT || pressureFill > 0.02F || exposureFill > 0.02F
                || player.hasEffect(LightMattersMod.PANIC) || player.hasEffect(LightMattersMod.FATIGUE);
        if (!showMeter) {
            return;
        }

        float scale = LightMattersClientConfig.getHudScale();
        int meterWidth = 78;
        int meterHeight = 5;
        int panelWidth = 94;
        boolean showExposure = exposureFill > 0.02F || player.hasEffect(LightMattersMod.PANIC);
        int panelHeight = showExposure ? 32 : 20;
        int x = getHudX(width, panelWidth, scale);
        int y = getHudY(height, panelHeight, scale);
        int pressureColor = getPressureColor(currentStage);

        event.getGuiGraphics().pose().pushPose();
        event.getGuiGraphics().pose().translate(x, y, 0.0F);
        event.getGuiGraphics().pose().scale(scale, scale, 1.0F);

        event.getGuiGraphics().fill(0, 0, panelWidth, panelHeight, 0x7A05070A);
        event.getGuiGraphics().drawString(minecraft.font, Component.literal("Darkness"), 5, 4, 0xE9D7B1, false);
        event.getGuiGraphics().drawString(minecraft.font, Component.literal(getStageLabel(currentStage)), 58, 4, pressureColor, false);
        event.getGuiGraphics().fill(5, 12, 5 + meterWidth, 12 + meterHeight, 0xCC1A1E24);
        event.getGuiGraphics().fill(5, 12, 5 + Mth.floor(meterWidth * pressureFill), 12 + meterHeight, pressureColor);

        if (showExposure) {
            int exposureY = 19;
            event.getGuiGraphics().fill(5, exposureY, 5 + meterWidth, exposureY + 4, 0xCC1A1E24);
            event.getGuiGraphics().fill(5, exposureY, 5 + Mth.floor(meterWidth * exposureFill), exposureY + 4, 0xFFD17A49);
            event.getGuiGraphics().drawString(minecraft.font, Component.literal(getRecoveryLabel(player)), 5, 25, 0xD8C8AD, false);
        }

        event.getGuiGraphics().pose().popPose();
    }

    private static int getHudX(int screenWidth, int panelWidth, float scale) {
        int scaledWidth = Mth.ceil(panelWidth * scale);
        int offset = LightMattersClientConfig.getHudOffsetX();
        return switch (LightMattersClientConfig.getHudAnchor()) {
            case TOP_LEFT, BOTTOM_LEFT -> offset;
            case TOP_RIGHT, BOTTOM_RIGHT -> screenWidth - scaledWidth - offset;
        };
    }

    private static int getHudY(int screenHeight, int panelHeight, float scale) {
        int scaledHeight = Mth.ceil(panelHeight * scale);
        int offset = LightMattersClientConfig.getHudOffsetY();
        return switch (LightMattersClientConfig.getHudAnchor()) {
            case TOP_LEFT, TOP_RIGHT -> offset;
            case BOTTOM_LEFT, BOTTOM_RIGHT -> screenHeight - scaledHeight - offset;
        };
    }

    @SubscribeEvent
    public static void onOptionsScreenInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof OptionsScreen optionsScreen)) {
            return;
        }

        int x = optionsScreen.width - 154;
        int y = 8;
        event.addListener(Button.builder(Component.literal("Light Matters HUD"), button -> Minecraft.getInstance().setScreen(new LightMattersHudOptionsScreen(optionsScreen)))
                .bounds(x, y, 146, 20)
                .build());
    }

    private static float getPressureFill(DarknessProfile profile) {
        return Mth.clamp((15.0F - profile.effectiveLight()) / 15.0F, 0.0F, 1.0F);
    }

    private static int getPressureColor(DarknessStage stage) {
        return switch (stage) {
            case BRIGHT -> 0x8FB37A;
            case GLOOM -> 0xC9A65A;
            case DARK -> 0xC76C3F;
            case PITCH_BLACK -> 0xB53B2F;
        };
    }

    private static String getStageLabel(DarknessStage stage) {
        return switch (stage) {
            case BRIGHT -> "Stable";
            case GLOOM -> "Gloom";
            case DARK -> "Strained";
            case PITCH_BLACK -> "Breaking";
        };
    }

    private static String getRecoveryLabel(LocalPlayer player) {
        MobEffectInstance panic = player.getEffect(LightMattersMod.PANIC);
        if (panic != null) {
            return "Panic active";
        }

        MobEffectInstance fatigue = player.getEffect(LightMattersMod.FATIGUE);
        if (fatigue != null) {
            return "Fatigue active";
        }

        if (currentStage.isSevere()) {
            return "Exposure rising";
        }

        if (exposureFill > 0.05F) {
            return "Steady yourself";
        }

        return "Recovering";
    }
}
