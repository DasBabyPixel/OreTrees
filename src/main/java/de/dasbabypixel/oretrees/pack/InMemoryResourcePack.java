package de.dasbabypixel.oretrees.pack;

import com.mojang.logging.LogUtils;
import net.minecraft.FileUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class InMemoryResourcePack extends AbstractPackResources {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<PackType, Map<String, Supplier<byte[]>>> data;

    protected InMemoryResourcePack(Map<String, Supplier<byte[]>> assets, Map<String, Supplier<byte[]>> data) {
        super("oretrees", false);
        var map = new EnumMap<PackType, Map<String, Supplier<byte[]>>>(PackType.class);
        map.put(PackType.CLIENT_RESOURCES, Map.copyOf(assets));
        map.put(PackType.SERVER_DATA, Map.copyOf(data));
        this.data = Map.copyOf(map);
    }

    @Override
    public @Nullable IoSupplier<InputStream> getRootResource(String @NotNull ... names) {
        return null;
    }

    @Override
    public @Nullable IoSupplier<InputStream> getResource(@NotNull PackType packType, @NotNull ResourceLocation resourceLocation) {
        var data = this.data.get(packType);
        var sup = data.get(resourceLocation.toString());
        if (sup == null) return null;
        return () -> new ByteArrayInputStream(sup.get());
    }

    @Override
    public void listResources(@NotNull PackType packType, @NotNull String namespaceIn, @NotNull String pathIn, @NotNull ResourceOutput resourceOutput) {
        var data = this.data.get(packType);
        FileUtil.decomposePath(pathIn).get().ifLeft(strings -> {

            var compiled = ResourceLocation.fromNamespaceAndPath(namespaceIn, pathIn).toString();

            data.entrySet().stream().filter(e -> e.getKey().startsWith(compiled)).forEach(e -> {
                resourceOutput.accept(ResourceLocation.parse(e.getKey()), () -> new ByteArrayInputStream(e
                        .getValue()
                        .get()));
            });

        }).ifRight(listPartialResult -> LOGGER.error("Invalid path {}: {}", pathIn, listPartialResult.message()));
    }

    @Override
    public @NotNull Set<String> getNamespaces(@NotNull PackType packType) {
        var data = this.data.get(packType);
        return data
                .keySet()
                .stream()
                .map(ResourceLocation::parse)
                .map(ResourceLocation::getNamespace)
                .collect(Collectors.toSet());
    }

    @Override
    public void close() {

    }
}
