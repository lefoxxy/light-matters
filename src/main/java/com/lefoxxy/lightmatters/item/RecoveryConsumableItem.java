package com.lefoxxy.lightmatters.item;

import java.util.List;

import com.lefoxxy.lightmatters.LightMattersMod;
import com.lefoxxy.lightmatters.gameplay.DarknessSystem;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public final class RecoveryConsumableItem extends Item {
    private final int exposureReliefTicks;
    private final int panicReliefTicks;
    private final int fatigueReliefTicks;
    private final String tooltipKey;

    public RecoveryConsumableItem(Properties properties, int exposureReliefTicks, int panicReliefTicks, int fatigueReliefTicks, String tooltipKey) {
        super(properties);
        this.exposureReliefTicks = exposureReliefTicks;
        this.panicReliefTicks = panicReliefTicks;
        this.fatigueReliefTicks = fatigueReliefTicks;
        this.tooltipKey = tooltipKey;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        player.startUsingItem(usedHand);
        return InteractionResultHolder.consume(player.getItemInHand(usedHand));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (livingEntity instanceof ServerPlayer player) {
            DarknessSystem.relievePitchBlackExposure(player, exposureReliefTicks);
            DarknessSystem.relievePanic(player, panicReliefTicks);
            DarknessSystem.relieveFatigue(player, fatigueReliefTicks);
            player.displayClientMessage(Component.translatable("item.lightmatters.recovery.relief"), true);
            level.playSound(null, player.blockPosition(), SoundEvents.HONEY_DRINK, SoundSource.PLAYERS, 0.65F, 0.95F + (player.getRandom().nextFloat() * 0.1F));
        }

        if (livingEntity instanceof Player player) {
            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            ItemStack remainder = new ItemStack(net.minecraft.world.item.Items.GLASS_BOTTLE);
            if (stack.isEmpty()) {
                return remainder;
            }

            if (!player.getInventory().add(remainder)) {
                player.drop(remainder, false);
            }
        }

        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable(tooltipKey).withStyle(ChatFormatting.GRAY));
    }
}
