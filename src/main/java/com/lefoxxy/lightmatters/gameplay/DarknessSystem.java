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
    private static final Map<UUID, Integer> PITCH_BLACK_EXPOSURE = new HashMap<>();

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
        if (stage == DarknessStage.BRIGHT) {
            clearManagedEffects(player);
            return;
        }

        if (stage.appliesDarknessEffect()) {
            applyHiddenEffect(player, MobEffects.DARKNESS, stage.darknessAmplifier());
        }

        if (stage == DarknessStage.DARK) {
            applyHiddenEffect(player, MobEffects.DIG_SLOWDOWN, 0);
        } else if (stage == DarknessStage.PITCH_BLACK) {
            applyHiddenEffect(player, MobEffects.DIG_SLOWDOWN, 1);
        }

        if (stage.appliesWeakness()) {
            applyHiddenEffect(player, MobEffects.WEAKNESS, stage.weaknessAmplifier());
        }
    }

    private static void updateExposure(ServerPlayer player, DarknessStage stage) {
        UUID playerId = player.getUUID();
        if (!stage.isSevere()) {
            PITCH_BLACK_EXPOSURE.remove(playerId);
            removeEffect(player, MobEffects.MOVEMENT_SLOWDOWN);
            removeEffect(player, MobEffects.CONFUSION);
            return;
        }

        int exposure = PITCH_BLACK_EXPOSURE.merge(playerId, SAMPLE_INTERVAL_TICKS, Integer::sum);
        if (exposure >= PANIC_TRIGGER_TICKS) {
            applyHiddenEffect(player, MobEffects.MOVEMENT_SLOWDOWN, 0);
            applyHiddenEffect(player, MobEffects.CONFUSION, 0);
        }
    }

    private static void clearManagedState(ServerPlayer player) {
        PITCH_BLACK_EXPOSURE.remove(player.getUUID());
        clearManagedEffects(player);
    }

    public static int getPitchBlackExposureTicks(Player player) {
        return PITCH_BLACK_EXPOSURE.getOrDefault(player.getUUID(), 0);
    }

    private static void clearManagedEffects(ServerPlayer player) {
        removeEffect(player, MobEffects.DARKNESS);
        removeEffect(player, MobEffects.DIG_SLOWDOWN);
        removeEffect(player, MobEffects.WEAKNESS);
        removeEffect(player, MobEffects.MOVEMENT_SLOWDOWN);
        removeEffect(player, MobEffects.CONFUSION);
    }

    private static void removeEffect(ServerPlayer player, Holder<MobEffect> effect) {
        if (player.hasEffect(effect)) {
            player.removeEffect(effect);
        }
    }

    private static void applyHiddenEffect(ServerPlayer player, Holder<MobEffect> effect, int amplifier) {
        MobEffectInstance instance = new MobEffectInstance(effect, EFFECT_REFRESH_TICKS, amplifier, true, false, false);
        player.addEffect(instance);
    }
}
