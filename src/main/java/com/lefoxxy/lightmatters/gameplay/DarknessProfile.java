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
        int personalLight,
        boolean canSeeSky,
        LightSourceTier ambientTier) {

    public static DarknessProfile sample(Player player) {
        Level level = player.level();
        BlockPos eyePos = BlockPos.containing(player.getX(), player.getEyeY(), player.getZ());
        int blockLight = level.getBrightness(LightLayer.BLOCK, eyePos);
        int rawSkyLight = Math.max(0, level.getBrightness(LightLayer.SKY, eyePos) - level.getSkyDarken());
        int skyPenalty = 6 + (level.isRainingAt(eyePos) ? 2 : 0);
        int skyLight = Math.max(0, rawSkyLight - skyPenalty);
        int personalLight = com.lefoxxy.lightmatters.item.FuelLanternItem.getHeldLanternLight(player);
        int effectiveLight = Math.max(Math.max(blockLight, skyLight), personalLight);
        DarknessStage stage = DarknessStage.fromEffectiveLight(effectiveLight);
        LightSourceTier ambientTier = LightSourceTier.fromAmbientLight(effectiveLight);

        return new DarknessProfile(stage, effectiveLight, blockLight, rawSkyLight, skyLight, personalLight, level.canSeeSky(eyePos), ambientTier);
    }
}
