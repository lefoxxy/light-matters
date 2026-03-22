package com.lefoxxy.lightmatters.gameplay;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.Level;

public record DarknessProfile(
        DarknessStage stage,
        int effectiveLight,
        int blockLight,
        int rawSkyLight,
        int skyLight,
        int outdoorPenalty,
        int personalLight,
        boolean canSeeSky,
        LightSourceTier ambientTier) {

    public static DarknessProfile sample(Player player) {
        Level level = player.level();
        BlockPos eyePos = BlockPos.containing(player.getX(), player.getEyeY(), player.getZ());
        int blockLight = level.getBrightness(LightLayer.BLOCK, eyePos);
        int rawSkyLight = Math.max(0, level.getBrightness(LightLayer.SKY, eyePos) - level.getSkyDarken());
        int outdoorPenalty = getOutdoorPenalty(level);
        int skyLight = Math.max(0, rawSkyLight - outdoorPenalty);
        int personalLight = com.lefoxxy.lightmatters.item.FuelLanternItem.getHeldLanternLight(player);
        int effectiveLight = Math.max(Math.max(blockLight, skyLight), personalLight);
        DarknessStage stage = DarknessStage.fromEffectiveLight(effectiveLight);
        LightSourceTier ambientTier = LightSourceTier.fromAmbientLight(effectiveLight);

        return new DarknessProfile(stage, effectiveLight, blockLight, rawSkyLight, skyLight, outdoorPenalty, personalLight, level.canSeeSky(eyePos), ambientTier);
    }

    private static int getOutdoorPenalty(Level level) {
        if (!level.dimensionType().natural()) {
            return 0;
        }

        float timeOfDay = level.getTimeOfDay(1.0F);
        float duskWeight = timeOfDay >= 0.5F && timeOfDay < 0.75F ? (timeOfDay - 0.5F) / 0.25F : 0.0F;
        float nightWeight = timeOfDay >= 0.75F && timeOfDay <= 1.0F ? 1.0F - ((timeOfDay - 0.75F) / 0.25F) : 0.0F;
        float penaltyWeight = Math.max(duskWeight, nightWeight);

        return Math.round(penaltyWeight * 6.0F);
    }
}
