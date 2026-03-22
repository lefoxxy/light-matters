package com.lefoxxy.lightmatters;

import com.lefoxxy.lightmatters.item.FuelLanternItem;
import com.lefoxxy.lightmatters.item.LanternTier;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.fml.common.Mod;

@Mod(LightMattersMod.MODID)
public final class LightMattersMod {
    public static final String MODID = "lightmatters";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredItem<Item> WOOD_LANTERN = ITEMS.register(LanternTier.WOOD.itemName(), () -> new FuelLanternItem(LanternTier.WOOD));
    public static final DeferredItem<Item> IRON_LANTERN = ITEMS.register(LanternTier.IRON.itemName(), () -> new FuelLanternItem(LanternTier.IRON));
    public static final DeferredItem<Item> GOLD_LANTERN = ITEMS.register(LanternTier.GOLD.itemName(), () -> new FuelLanternItem(LanternTier.GOLD));
    public static final DeferredItem<Item> DIAMOND_LANTERN = ITEMS.register(LanternTier.DIAMOND.itemName(), () -> new FuelLanternItem(LanternTier.DIAMOND));
    public static final DeferredItem<Item> NETHERITE_LANTERN = ITEMS.register(LanternTier.NETHERITE.itemName(), () -> new FuelLanternItem(LanternTier.NETHERITE));

    public LightMattersMod(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        modEventBus.addListener(this::addCreativeTabItems);
        LOGGER.info("Light Matters is loaded. Darkness now matters.");
    }

    private void addCreativeTabItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(WOOD_LANTERN.get());
            event.accept(IRON_LANTERN.get());
            event.accept(GOLD_LANTERN.get());
            event.accept(DIAMOND_LANTERN.get());
            event.accept(NETHERITE_LANTERN.get());
        }
    }
}
