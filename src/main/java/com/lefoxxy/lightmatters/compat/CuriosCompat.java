package com.lefoxxy.lightmatters.compat;

import com.lefoxxy.lightmatters.item.FuelLanternItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.items.IItemHandler;

public final class CuriosCompat {
    public static final EntityCapability<IItemHandler, Void> CURIOS_INVENTORY =
            EntityCapability.createVoid(ResourceLocation.fromNamespaceAndPath("curios", "item_handler"), IItemHandler.class);

    private CuriosCompat() {
    }

    public static int getEquippedLanternLight(Player player) {
        IItemHandler curiosInventory = getCuriosInventory(player);
        if (curiosInventory == null) {
            return 0;
        }

        int brightest = 0;
        for (int slot = 0; slot < curiosInventory.getSlots(); slot++) {
            brightest = Math.max(brightest, FuelLanternItem.getStackLight(curiosInventory.getStackInSlot(slot)));
        }
        return brightest;
    }

    public static void tickEquippedLanterns(Player player, int fuelCost) {
        IItemHandler curiosInventory = getCuriosInventory(player);
        if (curiosInventory == null) {
            return;
        }

        for (int slot = 0; slot < curiosInventory.getSlots(); slot++) {
            ItemStack stack = curiosInventory.getStackInSlot(slot);
            if (stack.getItem() instanceof FuelLanternItem) {
                FuelLanternItem.burnExternalFuel(stack, player, fuelCost);
            }
        }
    }

    private static IItemHandler getCuriosInventory(LivingEntity entity) {
        return entity.getCapability(CURIOS_INVENTORY);
    }
}
