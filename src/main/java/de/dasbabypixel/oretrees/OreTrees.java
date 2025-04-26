package de.dasbabypixel.oretrees;

import de.dasbabypixel.oretrees.blocks.OreSaplingBlock;
import de.dasbabypixel.oretrees.pack.OreTreesResourcePack;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// The value here should match an entry in the META-INF/mods.toml file
@SuppressWarnings("removal")
@Mod(OreTrees.MODID)
public class OreTrees
{
    static {
        Config.syncConfig();
    }

    public static final String MODID = "oretrees";
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    private static final List<RegistryObject<? extends Item>> items = new ArrayList<>();

    static {
        RegistryObject<? extends Item> firstTreeItem = null;
        for (var treeType : Config.treeTypes) {
            var saplingBlock = BLOCKS.register(treeType.id() + "_sapling", () -> new OreSaplingBlock(treeType));
            var saplingItem = ITEMS.register(treeType.id() + "_sapling", () -> new BlockItem(saplingBlock.get(), new Item.Properties()));
            if (firstTreeItem == null) {
                firstTreeItem = saplingItem;
            }

            var logBlock = BLOCKS.register(treeType.id() + "_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)));
            var logItem = ITEMS.register(treeType.id() + "_log", () -> new BlockItem(logBlock.get(), new Item.Properties()));

            var essenceBlock = BLOCKS.register(treeType.id() + "_essence", () -> new HalfTransparentBlock(BlockBehaviour.Properties
                    .of()
                    .mapColor(MapColor.STONE)
                    .sound(SoundType.STONE)
                    .explosionResistance(10.0F)
                    .strength(4F)
                    .noOcclusion()));
            var essenceItem = ITEMS.register(treeType.id() + "_essence", () -> new BlockItem(essenceBlock.get(), new Item.Properties()));
            var leavesBlock = BLOCKS.register(treeType.id() + "_leaves", () -> Blocks.leaves(SoundType.GRASS));
            var leavesItem = ITEMS.register(treeType.id() + "_leaves", () -> new BlockItem(leavesBlock.get(), new Item.Properties()));
            var fruitItem = ITEMS.register(treeType.id() + "_fruit", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(1)
                    .saturationMod(1)
                    .build())));
            var poppedFruitItem = ITEMS.register("popped_" + treeType.id() + "_fruit", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(2)
                    .saturationMod(2)
                    .build())));
            var barkItem = ITEMS.register(treeType.id() + "_bark", () -> new Item(new Item.Properties()));

            items.add(saplingItem);
            items.add(leavesItem);
            items.add(fruitItem);
            items.add(poppedFruitItem);
            items.add(barkItem);
            items.add(logItem);
            items.add(essenceItem);
        }
        if (firstTreeItem != null) {
            var finalFirstTreeItem = firstTreeItem;
            //noinspection NoTranslation
            CREATIVE_MODE_TABS.register("oretrees", () -> CreativeModeTab
                    .builder()
                    .title(Component.translatable("oretrees.creativetab.title"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(finalFirstTreeItem.get()::getDefaultInstance)
                    .displayItems((parameters, output) -> {
                        for (var item : items) {
                            output.accept(item.get());
                        }
                    })
                    .build());
        }
    }

    public OreTrees(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::pack);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void pack(AddPackFindersEvent event) {
        event.addRepositorySource(OreTreesResourcePack.INSTANCE);
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            for (var treeType : Config.treeTypes) {
                var sapling = Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(ResourceLocation.fromNamespaceAndPath(OreTrees.MODID, treeType.id() + "_sapling")));
                var log = Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(ResourceLocation.fromNamespaceAndPath(OreTrees.MODID, treeType.id() + "_log")));
                ItemBlockRenderTypes.setRenderLayer(sapling, RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(log, RenderType.cutoutMipped());
            }
        }

        @SubscribeEvent
        public static void colors(RegisterColorHandlersEvent.Block event) {
            for (var treeType : Config.treeTypes) {
                var sapling = Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(ResourceLocation.fromNamespaceAndPath(OreTrees.MODID, treeType.id() + "_sapling")));
                var leaves = Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(ResourceLocation.fromNamespaceAndPath(OreTrees.MODID, treeType.id() + "_leaves")));
                var log = Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(ResourceLocation.fromNamespaceAndPath(OreTrees.MODID, treeType.id() + "_log")));
                var essence = Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(ResourceLocation.fromNamespaceAndPath(OreTrees.MODID, treeType.id() + "_essence")));
                var color = (BlockColor) (p_92567_, p_92568_, p_92569_, p_92570_) -> treeType
                        .color()
                        .getRGB() & 0xFFFFFF;
                event.register(color, sapling, leaves, log, essence);
            }
        }

        @SubscribeEvent
        public static void colors(RegisterColorHandlersEvent.Item event) {
            for (var treeType : Config.treeTypes) {
                var sapling = Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath(OreTrees.MODID, treeType.id() + "_sapling")));
                var leaves = Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath(OreTrees.MODID, treeType.id() + "_leaves")));
                var fruit = Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath(OreTrees.MODID, treeType.id() + "_fruit")));
                var poppedFruit = Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath(OreTrees.MODID, "popped_" + treeType.id() + "_fruit")));
                var bark = Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath(OreTrees.MODID, treeType.id() + "_bark")));
                var log = Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath(OreTrees.MODID, treeType.id() + "_log")));
                var essence = Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath(OreTrees.MODID, treeType.id() + "_essence")));
                var color = (ItemColor) (itemStack, tint) -> treeType.color().getRGB() & 0xFFFFFF;
                var itemOverlayColor = (ItemColor) (itemStack, i) -> {
                    if (i == 1) return color.getColor(itemStack, i);
                    return -1;
                };
                event.register(color, sapling, leaves, poppedFruit, log, essence);
                event.register(itemOverlayColor, bark, fruit);
            }
        }
    }
}
