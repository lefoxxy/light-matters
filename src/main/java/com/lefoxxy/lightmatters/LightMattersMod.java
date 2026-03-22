package com.lefoxxy.lightmatters;

import com.lefoxxy.lightmatters.effect.FatigueEffect;
import com.lefoxxy.lightmatters.effect.PanicEffect;
import com.lefoxxy.lightmatters.item.FuelLanternItem;
import com.lefoxxy.lightmatters.item.LanternTier;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import net.minecraft.core.registries.Registries;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
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
    public static final DeferredHolder<MobEffect, MobEffect> FATIGUE = EFFECTS.register("fatigue", FatigueEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> PANIC = EFFECTS.register("panic", PanicEffect::new);
    public static final DeferredBlock<TorchBlock> WOOD_TORCH_BLOCK = BLOCKS.register("wood_torch",
            () -> new TorchBlock(ParticleTypes.FLAME, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .noCollission()
                    .instabreak()
                    .lightLevel(state -> 10)
                    .sound(SoundType.WOOD)
                    .pushReaction(net.minecraft.world.level.material.PushReaction.DESTROY)));
    public static final DeferredBlock<WallTorchBlock> WOOD_WALL_TORCH_BLOCK = BLOCKS.register("wood_wall_torch",
            () -> new WallTorchBlock(ParticleTypes.FLAME, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .noCollission()
                    .instabreak()
                    .lightLevel(state -> 10)
                    .sound(SoundType.WOOD)
                    .dropsLike(WOOD_TORCH_BLOCK.get())
                    .pushReaction(net.minecraft.world.level.material.PushReaction.DESTROY)));
    public static final DeferredBlock<LanternBlock> IRON_LANTERN_BLOCK = BLOCKS.register("iron_lantern_block",
            () -> new LanternBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LANTERN).lightLevel(state -> 12)));
    public static final DeferredBlock<LanternBlock> GOLD_LANTERN_BLOCK = BLOCKS.register("gold_lantern_block",
            () -> new LanternBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LANTERN).lightLevel(state -> 13)));
    public static final DeferredBlock<LanternBlock> DIAMOND_LANTERN_BLOCK = BLOCKS.register("diamond_lantern_block",
            () -> new LanternBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LANTERN).lightLevel(state -> 14)));
    public static final DeferredBlock<LanternBlock> NETHERITE_LANTERN_BLOCK = BLOCKS.register("netherite_lantern_block",
            () -> new LanternBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LANTERN).lightLevel(state -> 15)));
    public static final DeferredBlock<LanternBlock> CREATIVE_LANTERN_BLOCK = BLOCKS.register("creative_lantern_block",
            () -> new LanternBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LANTERN).lightLevel(state -> 15)));
    public static final DeferredItem<Item> WOOD_TORCH = ITEMS.register("wood_torch",
            () -> new StandingAndWallBlockItem(WOOD_TORCH_BLOCK.get(), WOOD_WALL_TORCH_BLOCK.get(), new Item.Properties(), Direction.DOWN));
    public static final DeferredItem<BlockItem> IRON_LANTERN_PLACEABLE = ITEMS.registerSimpleBlockItem("iron_lantern_block", IRON_LANTERN_BLOCK);
    public static final DeferredItem<BlockItem> GOLD_LANTERN_PLACEABLE = ITEMS.registerSimpleBlockItem("gold_lantern_block", GOLD_LANTERN_BLOCK);
    public static final DeferredItem<BlockItem> DIAMOND_LANTERN_PLACEABLE = ITEMS.registerSimpleBlockItem("diamond_lantern_block", DIAMOND_LANTERN_BLOCK);
    public static final DeferredItem<BlockItem> NETHERITE_LANTERN_PLACEABLE = ITEMS.registerSimpleBlockItem("netherite_lantern_block", NETHERITE_LANTERN_BLOCK);
    public static final DeferredItem<BlockItem> CREATIVE_LANTERN_PLACEABLE = ITEMS.registerSimpleBlockItem("creative_lantern_block", CREATIVE_LANTERN_BLOCK);
    public static final DeferredItem<Item> WOOD_LANTERN = ITEMS.register(LanternTier.WOOD.itemName(), () -> new FuelLanternItem(LanternTier.WOOD));
    public static final DeferredItem<Item> IRON_LANTERN = ITEMS.register(LanternTier.IRON.itemName(), () -> new FuelLanternItem(LanternTier.IRON));
    public static final DeferredItem<Item> GOLD_LANTERN = ITEMS.register(LanternTier.GOLD.itemName(), () -> new FuelLanternItem(LanternTier.GOLD));
    public static final DeferredItem<Item> DIAMOND_LANTERN = ITEMS.register(LanternTier.DIAMOND.itemName(), () -> new FuelLanternItem(LanternTier.DIAMOND));
    public static final DeferredItem<Item> NETHERITE_LANTERN = ITEMS.register(LanternTier.NETHERITE.itemName(), () -> new FuelLanternItem(LanternTier.NETHERITE));
    public static final DeferredItem<Item> CREATIVE_LANTERN = ITEMS.register(LanternTier.CREATIVE.itemName(), () -> new FuelLanternItem(LanternTier.CREATIVE));

    public LightMattersMod(IEventBus modEventBus) {
        EFFECTS.register(modEventBus);
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
            event.accept(WOOD_TORCH);
            event.accept(IRON_LANTERN_PLACEABLE);
            event.accept(GOLD_LANTERN_PLACEABLE);
            event.accept(DIAMOND_LANTERN_PLACEABLE);
            event.accept(NETHERITE_LANTERN_PLACEABLE);
            event.accept(CREATIVE_LANTERN_PLACEABLE);
        }
    }
}
