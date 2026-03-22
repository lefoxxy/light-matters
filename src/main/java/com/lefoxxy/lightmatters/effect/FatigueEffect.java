package com.lefoxxy.lightmatters.effect;

import com.lefoxxy.lightmatters.LightMattersMod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class FatigueEffect extends MobEffect {
    public FatigueEffect() {
        super(MobEffectCategory.HARMFUL, 0x7A6651);
        addAttributeModifier(
                Attributes.ATTACK_SPEED,
                ResourceLocation.fromNamespaceAndPath(LightMattersMod.MODID, "fatigue_attack_speed"),
                -0.12D,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
}
