package com.lefoxxy.lightmatters.gameplay;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.lefoxxy.lightmatters.LightMattersMod;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = LightMattersMod.MODID)
public final class DarknessSystem {
    private static final int EFFECT_REFRESH_TICKS = 60;
    private static final int SAMPLE_INTERVAL_TICKS = 20;
    private static final int PANIC_TRIGGER_TICKS = 160;
    private static final int FATIGUE_RECOVERY_TICKS = 120;
    private static final int PANIC_RECOVERY_TICKS = 140;
    private static final Map<UUID, Integer> PITCH_BLACK_EXPOSURE = new HashMap<>();
    private static final Map<UUID, Integer> FATIGUE_RECOVERY = new HashMap<>();
    private static final Map<UUID, Integer> PANIC_RECOVERY = new HashMap<>();

    private DarknessSystem() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide() || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if (player.tickCount % SAMPLE_INTERVAL_TICKS != 0) {
            return;
        }

        if (player.isCreative() || player.isSpectator()) {
            clearManagedState(serverPlayer);
            return;
        }

        DarknessProfile profile = DarknessProfile.sample(serverPlayer);
        applyStageEffects(serverPlayer, profile.stage());
        updateExposure(serverPlayer, profile.stage());
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        if (player.isCreative() || player.isSpectator()) {
            return;
        }

        DarknessProfile profile = DarknessProfile.sample(player);
        if (profile.stage() == DarknessStage.BRIGHT) {
            return;
        }

        event.setNewSpeed(event.getOriginalSpeed() * profile.stage().miningSpeedMultiplier());
    }

    private static void applyStageEffects(ServerPlayer player, DarknessStage stage) {
        if (stage.appliesDarknessEffect()) {
            applyManagedEffect(player, MobEffects.DARKNESS, stage.darknessAmplifier());
        } else {
            removeEffect(player, MobEffects.DARKNESS);
        }

        if (stage == DarknessStage.DARK) {
            FATIGUE_RECOVERY.put(player.getUUID(), FATIGUE_RECOVERY_TICKS);
            applyManagedEffect(player, LightMattersMod.FATIGUE, 0);
        } else if (stage == DarknessStage.PITCH_BLACK) {
            FATIGUE_RECOVERY.put(player.getUUID(), FATIGUE_RECOVERY_TICKS + 40);
            applyManagedEffect(player, LightMattersMod.FATIGUE, 1);
        } else {
            applyFatigueRecovery(player);
        }
    }

    private static void updateExposure(ServerPlayer player, DarknessStage stage) {
        UUID playerId = player.getUUID();
        if (!stage.isSevere()) {
            PITCH_BLACK_EXPOSURE.remove(playerId);
            applyPanicRecovery(player);
            return;
        }

        int exposure = PITCH_BLACK_EXPOSURE.merge(playerId, SAMPLE_INTERVAL_TICKS, Integer::sum);
        if (exposure >= PANIC_TRIGGER_TICKS) {
            PANIC_RECOVERY.put(playerId, PANIC_RECOVERY_TICKS);
            applyManagedEffect(player, LightMattersMod.PANIC, 0);
        }
    }

    private static void clearManagedState(ServerPlayer player) {
        PITCH_BLACK_EXPOSURE.remove(player.getUUID());
        FATIGUE_RECOVERY.remove(player.getUUID());
        PANIC_RECOVERY.remove(player.getUUID());
        clearManagedEffects(player);
    }

    public static int getPitchBlackExposureTicks(Player player) {
        return PITCH_BLACK_EXPOSURE.getOrDefault(player.getUUID(), 0);
    }

    private static void clearManagedEffects(ServerPlayer player) {
        removeEffect(player, MobEffects.DARKNESS);
        removeEffect(player, LightMattersMod.FATIGUE);
        removeEffect(player, LightMattersMod.PANIC);
    }

    private static void applyFatigueRecovery(ServerPlayer player) {
        UUID playerId = player.getUUID();
        Integer remaining = FATIGUE_RECOVERY.get(playerId);
        if (remaining == null || remaining <= 0) {
            FATIGUE_RECOVERY.remove(playerId);
            removeEffect(player, LightMattersMod.FATIGUE);
            return;
        }

        int next = Math.max(0, remaining - SAMPLE_INTERVAL_TICKS);
        FATIGUE_RECOVERY.put(playerId, next);
        int amplifier = remaining > 80 ? 1 : 0;
        applyManagedEffect(player, LightMattersMod.FATIGUE, amplifier);
    }

    private static void applyPanicRecovery(ServerPlayer player) {
        UUID playerId = player.getUUID();
        Integer remaining = PANIC_RECOVERY.get(playerId);
        if (remaining == null || remaining <= 0) {
            PANIC_RECOVERY.remove(playerId);
            removeEffect(player, LightMattersMod.PANIC);
            return;
        }

        int next = Math.max(0, remaining - SAMPLE_INTERVAL_TICKS);
        PANIC_RECOVERY.put(playerId, next);
        applyManagedEffect(player, LightMattersMod.PANIC, 0);
    }

    private static void removeEffect(ServerPlayer player, Holder<MobEffect> effect) {
        if (player.hasEffect(effect)) {
            player.removeEffect(effect);
        }
    }

    private static void applyManagedEffect(ServerPlayer player, Holder<MobEffect> effect, int amplifier) {
        MobEffectInstance instance = new MobEffectInstance(effect, EFFECT_REFRESH_TICKS, amplifier, false, false, true);
        player.addEffect(instance);
    }
}
