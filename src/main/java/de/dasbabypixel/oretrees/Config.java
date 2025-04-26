package de.dasbabypixel.oretrees;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.mojang.logging.LogUtils;
import de.dasbabypixel.oretrees.blocks.TreeType;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@SuppressWarnings("unchecked")
@Mod.EventBusSubscriber(modid = OreTrees.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final Logger LOGGER = LogUtils.getLogger();

    public static Set<TreeType> treeTypes;

    private static void saveDummyConfig(Path path) throws IOException {
        var config = CommentedConfig.inMemory();
        config.setComment("trees", """
                Format:
                [[trees]]
                id = "sparkling"
                color = [r,g,b]
                """);

        var example = CommentedConfig.inMemory();
        example.setComment("id", "The id of the tree. This can be anything, and is used to identify the tree. This is also used for name generation in en_us");
        example.add("id", "sparkling");
        example.add("color", List.of(255, 0, 0));
        config.set("trees", List.of(example));

        var text = TomlFormat.instance().createWriter().writeToString(config);
        Files.writeString(path, text);
    }

    private static void validateConfig(CommentedConfig rootConfig) {
        if (!rootConfig.contains("trees")) {
            LOGGER.error("Config does not contain any trees");
            return;
        }
        var trees = (List<com.electronwill.nightconfig.core.Config>) rootConfig.get("trees");
        if (trees.stream().map(config -> {
            if (!config.contains("id")) {
                LOGGER.error("Missing id");
                return false;
            }
            if (!config.contains("color")) {
                LOGGER.error("Missing color");
                return false;
            }
            var colorObj = config.get("color");
            if (!(colorObj instanceof List<?> colorList)) {
                LOGGER.error("Color not of type List");
                return false;
            }
            if (colorList.size() != 3) {
                LOGGER.error("Color not of List size 3");
                return false;
            }
            for (var o : colorList) {
                try {
                    Integer.parseInt(o.toString());
                } catch (Throwable t) {
                    LOGGER.error("Failed to parse color", t);
                    return false;
                }
            }
            return true;
        }).anyMatch(s -> !s)) {
            throw new Error("Invalid config");
        }
    }

    public static void syncConfig() {
        var path = Path.of("config/oretrees-common.toml");
        try {
            if (!Files.exists(path)) {
                saveDummyConfig(path);
            }
            var text = Files.readString(path);
            var rootConfig = TomlFormat.instance().createParser().parse(text);
            validateConfig(rootConfig);

            var trees = (List<com.electronwill.nightconfig.core.Config>) rootConfig.get("trees");
            if (trees != null) {
                treeTypes = trees.stream().map(config -> {
                    var id = config.get("id").toString();
                    var colorObj = config.get("color");
                    var color = Color.WHITE;
                    if (colorObj instanceof List<?> l) {
                        var r = Integer.parseInt(l.get(0).toString());
                        var g = Integer.parseInt(l.get(1).toString());
                        var b = Integer.parseInt(l.get(2).toString());
                        color = new Color(r, g, b);
                    }
                    return new TreeType(id, color);
                }).collect(Collectors.toSet());
            } else {
                treeTypes = Set.of();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
