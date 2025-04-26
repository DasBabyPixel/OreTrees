package de.dasbabypixel.oretrees.blocks;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public record TreeType(String id, Color color) implements Comparable<TreeType> {
    @Override
    public int compareTo(@NotNull TreeType o) {
        return 0;
    }
}
