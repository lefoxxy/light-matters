package com.lefoxxy.lightmatters.effect;

import com.lefoxxy.lightmatters.LightMattersMod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class PanicEffect extends MobEffect {
    public PanicEffect() {
        super(MobEffectCategory.HARMFUL, 0xC97B4B);
        addAttributeModifier(
                Attributes.MOVEMENT_SPEED,
                ResourceLocation.fromNamespaceAndPath(LightMattersMod.MODID, "panic_movement_speed"),
                -0.08D,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        addAttributeModifier(
                Attributes.ATTACK_DAMAGE,
                ResourceLocation.fromNamespaceAndPath(LightMattersMod.MODID, "panic_attack_damage"),
                -0.15D,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
}
