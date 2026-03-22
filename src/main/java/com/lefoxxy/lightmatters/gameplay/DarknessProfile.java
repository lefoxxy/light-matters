package com.lefoxxy.lightmatters.gameplay;

import com.lefoxxy.lightmatters.LightMattersMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

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
        int rawBlockLight = level.getBrightness(LightLayer.BLOCK, eyePos);
        int blockLightPenalty = getBlockLightPenalty(level, eyePos);
        int blockLight = Math.max(0, rawBlockLight - blockLightPenalty);
        int rawSkyLight = Math.max(0, level.getBrightness(LightLayer.SKY, eyePos) - level.getSkyDarken());
        int outdoorPenalty = getOutdoorPenalty(level);
        int skyLight = Math.max(0, rawSkyLight - outdoorPenalty);
        int personalLight = com.lefoxxy.lightmatters.item.FuelLanternItem.getHeldLanternLight(player);
        int effectiveLight = Math.max(Math.max(blockLight, skyLight), personalLight);
        DarknessStage stage = DarknessStage.fromEffectiveLight(effectiveLight);
        LightSourceTier ambientTier = LightSourceTier.fromAmbientLight(effectiveLight);

        return new DarknessProfile(stage, effectiveLight, blockLight, rawSkyLight, skyLight, outdoorPenalty, personalLight, level.canSeeSky(eyePos), ambientTier);
    }

    private static int getBlockLightPenalty(Level level, BlockPos center) {
        boolean customLanternNearby = false;
        boolean vanillaTorchNearby = false;

        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-4, -2, -4), center.offset(4, 2, 4))) {
            BlockState state = level.getBlockState(pos);

            if (state.is(LightMattersMod.IRON_LANTERN_BLOCK.get())
                    || state.is(LightMattersMod.GOLD_LANTERN_BLOCK.get())
                    || state.is(LightMattersMod.DIAMOND_LANTERN_BLOCK.get())
                    || state.is(LightMattersMod.NETHERITE_LANTERN_BLOCK.get())) {
                customLanternNearby = true;
                break;
            }

            if (state.is(Blocks.TORCH) || state.is(Blocks.WALL_TORCH) || state.is(Blocks.SOUL_TORCH) || state.is(Blocks.SOUL_WALL_TORCH)) {
                vanillaTorchNearby = true;
            }
        }

        if (customLanternNearby) {
            return 0;
        }

        return vanillaTorchNearby ? 3 : 0;
    }

    private static int getOutdoorPenalty(Level level) {
        if (!level.dimensionType().natural() || !level.dimensionType().hasSkyLight()) {
            return 0;
        }

        long dayTicks = Math.floorMod(level.getDayTime(), 24000L);
        float penaltyWeight = getNightWeight(dayTicks);

        if (penaltyWeight <= 0.0F) {
            return 0;
        }

        float weatherBonus = level.isThundering() ? 2.0F : level.isRaining() ? 1.0F : 0.0F;
        return Math.round((penaltyWeight * 6.0F) + weatherBonus);
    }

    private static float getNightWeight(long dayTicks) {
        if (dayTicks < 12000L) {
            return 0.0F;
        }

        if (dayTicks < 13500L) {
            return (dayTicks - 12000L) / 1500.0F;
        }

        if (dayTicks < 22500L) {
            return 1.0F;
        }

        if (dayTicks < 24000L) {
            return 1.0F - ((dayTicks - 22500L) / 1500.0F);
        }

        return 0.0F;
    }
}
