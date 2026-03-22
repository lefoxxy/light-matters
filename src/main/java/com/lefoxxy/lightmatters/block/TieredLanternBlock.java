package com.lefoxxy.lightmatters.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public final class TieredLanternBlock extends LanternBlock {
    private final SimpleParticleType primaryParticle;
    private final SimpleParticleType accentParticle;

    public TieredLanternBlock(SimpleParticleType primaryParticle, SimpleParticleType accentParticle, BlockBehaviour.Properties properties) {
        super(properties);
        this.primaryParticle = primaryParticle;
        this.accentParticle = accentParticle;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + (state.getValue(HANGING) ? 0.72D : 0.48D);
        double z = pos.getZ() + 0.5D;

        if (random.nextFloat() < 0.65F) {
            level.addParticle(primaryParticle, x, y, z, 0.0D, 0.003D, 0.0D);
        }

        if (random.nextFloat() < 0.35F) {
            level.addParticle(accentParticle, x, y + 0.05D, z, 0.0D, 0.002D, 0.0D);
        }
    }
}
