package com.lefoxxy.lightmatters;

import com.lefoxxy.lightmatters.effect.FatigueEffect;
import com.lefoxxy.lightmatters.effect.PanicEffect;
import com.lefoxxy.lightmatters.block.TieredLanternBlock;
import com.lefoxxy.lightmatters.item.FuelLanternItem;
import com.lefoxxy.lightmatters.item.LanternTier;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.fml.common.Mod;

@Mod(LightMattersMod.MODID)
public final class LightMattersMod {
    public static final String MODID = "lightmatters";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, MODID);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, MODID);
    public static final DeferredHolder<MobEffect, MobEffect> FATIGUE = EFFECTS.register("fatigue", FatigueEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> PANIC = EFFECTS.register("panic", PanicEffect::new);
    public static final DeferredHolder<SoundEvent, SoundEvent> PANIC_HEARTBEAT_SOUND = SOUND_EVENTS.register(
            "panic_heartbeat",
            () -> SoundEvent.createVariableRangeEvent(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(MODID, "panic_heartbeat")));
    public static final DeferredHolder<SoundEvent, SoundEvent> FATIGUE_BREATH_SOUND = SOUND_EVENTS.register(
            "fatigue_breath",
            () -> SoundEvent.createVariableRangeEvent(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(MODID, "fatigue_breath")));
    public static final DeferredBlock<LanternBlock> IRON_LANTERN_BLOCK = BLOCKS.register("iron_lantern_block",
            () -> new TieredLanternBlock(ParticleTypes.FLAME, ParticleTypes.SMOKE, BlockBehaviour.Properties.ofFullCopy(Blocks.LANTERN).lightLevel(state -> 12)));
    public static final DeferredBlock<LanternBlock> GOLD_LANTERN_BLOCK = BLOCKS.register("gold_lantern_block",
            () -> new TieredLanternBlock(ParticleTypes.FLAME, ParticleTypes.WAX_ON, BlockBehaviour.Properties.ofFullCopy(Blocks.LANTERN).lightLevel(state -> 13)));
    public static final DeferredBlock<LanternBlock> DIAMOND_LANTERN_BLOCK = BLOCKS.register("diamond_lantern_block",
            () -> new TieredLanternBlock(ParticleTypes.END_ROD, ParticleTypes.WHITE_SMOKE, BlockBehaviour.Properties.ofFullCopy(Blocks.LANTERN).lightLevel(state -> 14)));
    public static final DeferredBlock<LanternBlock> NETHERITE_LANTERN_BLOCK = BLOCKS.register("netherite_lantern_block",
            () -> new TieredLanternBlock(ParticleTypes.SOUL_FIRE_FLAME, ParticleTypes.WHITE_ASH, BlockBehaviour.Properties.ofFullCopy(Blocks.LANTERN).lightLevel(state -> 15)));
    public static final DeferredBlock<LanternBlock> CREATIVE_LANTERN_BLOCK = BLOCKS.register("creative_lantern_block",
            () -> new TieredLanternBlock(ParticleTypes.END_ROD, ParticleTypes.ELECTRIC_SPARK, BlockBehaviour.Properties.ofFullCopy(Blocks.LANTERN).lightLevel(state -> 15)));
    public static final DeferredItem<Item> WOOD_LANTERN = ITEMS.register(LanternTier.WOOD.itemName(), () -> new FuelLanternItem(LanternTier.WOOD, Blocks.LANTERN));
    public static final DeferredItem<Item> IRON_LANTERN = ITEMS.register(LanternTier.IRON.itemName(), () -> new FuelLanternItem(LanternTier.IRON, IRON_LANTERN_BLOCK.get()));
    public static final DeferredItem<Item> GOLD_LANTERN = ITEMS.register(LanternTier.GOLD.itemName(), () -> new FuelLanternItem(LanternTier.GOLD, GOLD_LANTERN_BLOCK.get()));
    public static final DeferredItem<Item> DIAMOND_LANTERN = ITEMS.register(LanternTier.DIAMOND.itemName(), () -> new FuelLanternItem(LanternTier.DIAMOND, DIAMOND_LANTERN_BLOCK.get()));
    public static final DeferredItem<Item> NETHERITE_LANTERN = ITEMS.register(LanternTier.NETHERITE.itemName(), () -> new FuelLanternItem(LanternTier.NETHERITE, NETHERITE_LANTERN_BLOCK.get()));
    public static final DeferredItem<Item> CREATIVE_LANTERN = ITEMS.register(LanternTier.CREATIVE.itemName(), () -> new FuelLanternItem(LanternTier.CREATIVE, CREATIVE_LANTERN_BLOCK.get()));

    public LightMattersMod(IEventBus modEventBus) {
        EFFECTS.register(modEventBus);
        SOUND_EVENTS.register(modEventBus);
        BLOCKS.register(modEventBus);
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
            event.accept(CREATIVE_LANTERN.get());
        }

        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(IRON_LANTERN);
            event.accept(GOLD_LANTERN);
            event.accept(DIAMOND_LANTERN);
            event.accept(NETHERITE_LANTERN);
            event.accept(CREATIVE_LANTERN);
        }
    }
}
