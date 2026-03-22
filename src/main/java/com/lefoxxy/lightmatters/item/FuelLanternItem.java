package com.lefoxxy.lightmatters.item;

import java.util.List;

import com.lefoxxy.lightmatters.LightMattersMod;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.Level;
import net.minecraft.world.InteractionResult;

public final class FuelLanternItem extends BlockItem {
    private static final String FUEL_KEY = "FuelTicks";
    private static final String LIT_KEY = "Lit";
    private final LanternTier tier;

    public FuelLanternItem(LanternTier tier, Block block) {
        super(block, new Properties().stacksTo(1));
        this.tier = tier;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return super.useOn(context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack lantern = player.getItemInHand(usedHand);
        ItemStack otherHand = player.getItemInHand(usedHand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);

        if (!tier.infiniteFuel() && isFuelItem(otherHand) && getFuel(lantern) < getMaxFuel()) {
            if (!level.isClientSide()) {
                addFuel(lantern, getFuelValue(otherHand));
                consumeFuelItem(player, otherHand);
                player.displayClientMessage(Component.translatable("item.lightmatters.lantern.refueled", formatSeconds(getFuel(lantern) / 20)), true);
            }
            return InteractionResultHolder.sidedSuccess(lantern, level.isClientSide());
        }

        if (!tier.infiniteFuel() && getFuel(lantern) <= 0) {
            if (!level.isClientSide()) {
                player.displayClientMessage(Component.translatable("item.lightmatters.lantern.empty"), true);
            }
            return InteractionResultHolder.fail(lantern);
        }

        if (!level.isClientSide()) {
            boolean lit = !isLit(lantern);
            setLit(lantern, lit);
            player.displayClientMessage(Component.translatable(lit ? "item.lightmatters.lantern.lit" : "item.lightmatters.lantern.unlit"), true);
        }
        return InteractionResultHolder.sidedSuccess(lantern, level.isClientSide());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide() || tier.infiniteFuel() || !(entity instanceof Player player) || !isLit(stack) || !isHeld(player, slotId, isSelected)) {
            return;
        }

        int remainingFuel = getFuel(stack);
        if (remainingFuel <= 0) {
            extinguish(stack, player);
            return;
        }

        setFuel(stack, remainingFuel - 1);
        if (remainingFuel - 1 <= 0) {
            extinguish(stack, player);
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return !tier.infiniteFuel() && getFuel(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * ((float)getFuel(stack) / (float)getMaxFuel()));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        if (!isLit(stack)) {
            return 0x6B7280;
        }

        return switch (tier) {
            case WOOD -> 0xD6893B;
            case IRON -> 0xC8CDD3;
            case GOLD -> 0xF2C94C;
            case DIAMOND -> 0x65D6E9;
            case NETHERITE -> 0x5D5666;
        };
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        int fuelTicks = getFuel(stack);
        tooltipComponents.add(Component.translatable("item.lightmatters.lantern.tier", Component.literal(tier.displayName()).withStyle(tier.nameColor())));
        tooltipComponents.add(Component.translatable("item.lightmatters.lantern.brightness", tier.personalLight()));
        tooltipComponents.add(tier.infiniteFuel()
                ? Component.translatable("item.lightmatters.lantern.efficiency_infinite")
                : Component.translatable("item.lightmatters.lantern.efficiency", formatSeconds(tier.fuelSecondsPerCoal())));
        tooltipComponents.add(Component.translatable("item.lightmatters.lantern.status", isLit(stack)
                ? Component.translatable("item.lightmatters.lantern.state.lit").withStyle(ChatFormatting.GOLD)
                : Component.translatable("item.lightmatters.lantern.state.unlit").withStyle(ChatFormatting.GRAY)));
        tooltipComponents.add(tier.infiniteFuel()
                ? Component.translatable("item.lightmatters.lantern.fuel_infinite")
                : Component.translatable("item.lightmatters.lantern.fuel", formatSeconds(fuelTicks / 20)));
        tooltipComponents.add(Component.translatable("item.lightmatters.lantern.controls"));
        if (!tier.infiniteFuel()) {
            tooltipComponents.add(Component.translatable("item.lightmatters.lantern.refuel_hint"));
            tooltipComponents.add(Component.translatable("item.lightmatters.lantern.fuels"));
        }
    }

    public static int getHeldLanternLight(Player player) {
        return Math.max(getStackLight(player.getMainHandItem()), getStackLight(player.getOffhandItem()));
    }

    public static int getStackLight(ItemStack stack) {
        if (stack.getItem() instanceof FuelLanternItem lanternItem && isLit(stack) && (lanternItem.tier.infiniteFuel() || getFuel(stack) > 0)) {
            return lanternItem.tier.personalLight();
        }

        return 0;
    }

    private static boolean isHeld(Player player, int slotId, boolean isSelected) {
        return isSelected || slotId == Inventory.SLOT_OFFHAND;
    }

    private static boolean isFuelItem(ItemStack stack) {
        return stack.is(Items.COAL)
                || stack.is(Items.CHARCOAL)
                || stack.is(LightMattersMod.RESIN_CLUMP.get())
                || stack.is(LightMattersMod.LAMP_OIL.get());
    }

    private static void extinguish(ItemStack stack, Player player) {
        setFuel(stack, 0);
        setLit(stack, false);
        player.displayClientMessage(Component.translatable("item.lightmatters.lantern.went_dark"), true);
    }

    public static void burnExternalFuel(ItemStack stack, Player player, int fuelCost) {
        if (!(stack.getItem() instanceof FuelLanternItem lanternItem) || lanternItem.tier.infiniteFuel() || !isLit(stack)) {
            return;
        }

        int remainingFuel = getFuel(stack);
        if (remainingFuel <= 0) {
            extinguish(stack, player);
            return;
        }

        setFuel(stack, remainingFuel - fuelCost);
        if (remainingFuel - fuelCost <= 0) {
            extinguish(stack, player);
        }
    }

    private static int getFuel(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return tag.getInt(FUEL_KEY);
    }

    private static void setFuel(ItemStack stack, int fuelTicks) {
        if (!(stack.getItem() instanceof FuelLanternItem lanternItem)) {
            return;
        }

        if (lanternItem.tier.infiniteFuel()) {
            return;
        }

        int clampedFuel = Mth.clamp(fuelTicks, 0, lanternItem.getMaxFuel());
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            if (clampedFuel > 0) {
                tag.putInt(FUEL_KEY, clampedFuel);
            } else {
                tag.remove(FUEL_KEY);
            }
        });
    }

    public static boolean isLit(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean(LIT_KEY);
    }

    private static void setLit(ItemStack stack, boolean lit) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            if (lit) {
                tag.putBoolean(LIT_KEY, true);
            } else {
                tag.remove(LIT_KEY);
            }
        });
    }

    private static void addFuel(ItemStack stack, int amount) {
        setFuel(stack, getFuel(stack) + amount);
    }

    private int getFuelValue(ItemStack fuelStack) {
        if (fuelStack.is(Items.CHARCOAL)) {
            return tier.fuelPerCoalTicks() / 2;
        }

        if (fuelStack.is(LightMattersMod.RESIN_CLUMP.get())) {
            return Math.round(tier.fuelPerCoalTicks() * 1.3F);
        }

        if (fuelStack.is(LightMattersMod.LAMP_OIL.get())) {
            return Math.round(tier.fuelPerCoalTicks() * 2.0F);
        }

        return tier.fuelPerCoalTicks();
    }

    private int getMaxFuel() {
        return Math.max(1, tier.maxFuelTicks());
    }

    private static void consumeFuelItem(Player player, ItemStack fuelStack) {
        if (player.getAbilities().instabuild) {
            return;
        }

        boolean lampOil = fuelStack.is(LightMattersMod.LAMP_OIL.get());
        fuelStack.shrink(1);
        if (lampOil) {
            ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
            if (!player.getInventory().add(bottle)) {
                player.drop(bottle, false);
            }
        }
    }

    private static Component formatSeconds(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return Component.literal(minutes + "m " + seconds + "s");
    }
}
