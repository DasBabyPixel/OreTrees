package de.dasbabypixel.oretrees.blocks;

import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class TreeTypeProperty extends Property<TreeType> {
    protected TreeTypeProperty(String p_61692_, Class<TreeType> p_61693_) {
        super(p_61692_, p_61693_);
    }

    @Override
    public @NotNull Collection<TreeType> getPossibleValues() {
        return List.of();
    }

    @Override
    public @NotNull String getName(@NotNull TreeType p_61696_) {
        return "";
    }

    @Override
    public @NotNull Optional<TreeType> getValue(@NotNull String p_61701_) {
        return Optional.empty();
    }
}
