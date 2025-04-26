package de.dasbabypixel.oretrees.blocks;

import de.dasbabypixel.oretrees.OreTrees;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OreSaplingBlock extends SaplingBlock {

    public OreSaplingBlock(TreeType treeType) {
        super(new Grower(treeType), BlockBehaviour.Properties
                .of()
                .mapColor(MapColor.PLANT)
                .noCollission()
                .randomTicks()
                .instabreak()
                .sound(SoundType.GRASS)
                .pushReaction(PushReaction.DESTROY));
    }

    private static class Grower extends AbstractTreeGrower {
        private final ResourceKey<ConfiguredFeature<?, ?>> normal;
        private final ResourceKey<ConfiguredFeature<?, ?>> bees;
        private final ResourceKey<ConfiguredFeature<?, ?>> fancy;
        private final ResourceKey<ConfiguredFeature<?, ?>> fancyBees;

        public Grower(TreeType type) {
            this(key(type.id()), key(type.id() + "_bees"), key(type.id() + "_fancy"), key(type.id() + "_fancy_bees"));
        }

        private static ResourceKey<ConfiguredFeature<?, ?>> key(String path) {
            return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(OreTrees.MODID, path));
        }

        public Grower(ResourceKey<ConfiguredFeature<?, ?>> normal, ResourceKey<ConfiguredFeature<?, ?>> bees, ResourceKey<ConfiguredFeature<?, ?>> fancy, ResourceKey<ConfiguredFeature<?, ?>> fancyBees) {
            this.normal = normal;
            this.bees = bees;
            this.fancy = fancy;
            this.fancyBees = fancyBees;
        }

        @Override
        protected @Nullable ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(@NotNull RandomSource randomSource, boolean hasFlowers) {
            if (randomSource.nextInt(10) == 0) {
                return hasFlowers ? fancyBees : fancy;
            } else {
                return hasFlowers ? bees : normal;
            }
        }
    }
}
