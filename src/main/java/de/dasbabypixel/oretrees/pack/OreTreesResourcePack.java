package de.dasbabypixel.oretrees.pack;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.dasbabypixel.oretrees.Config;
import de.dasbabypixel.oretrees.OreTrees;
import de.dasbabypixel.oretrees.blocks.TreeType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.flag.FeatureFlagSet;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class OreTreesResourcePack implements RepositorySource {
    public static final OreTreesResourcePack INSTANCE = new OreTreesResourcePack();
    private final String modid = OreTrees.MODID;
    private InMemoryResourcePack pack = null;

    private synchronized InMemoryResourcePack pack() {
        if (pack != null) return pack;
        var assets = new HashMap<String, Supplier<byte[]>>();
        var data = new HashMap<String, Supplier<byte[]>>();
        var assetsLocation = new HashMap<ResourceLocation, byte[]>();
        var dataLocation = new HashMap<ResourceLocation, byte[]>();

        basicConfiguration(assetsLocation, dataLocation);

        for (var treeType : Config.treeTypes) {
            addSapling(assetsLocation, dataLocation, treeType);
            addLeaves(assetsLocation, dataLocation, treeType);
            addFruits(assetsLocation, dataLocation, treeType);
            addBark(assetsLocation, dataLocation, treeType);
            addLog(assetsLocation, dataLocation, treeType);
            addEssence(assetsLocation, dataLocation, treeType);
            makeBonsai(assetsLocation, dataLocation, treeType);
            makeBotany(assetsLocation, dataLocation, treeType);
        }

        addLanguage(assetsLocation);

        for (var resourceLocationEntry : assetsLocation.entrySet()) {
            var bytes = resourceLocationEntry.getValue();
            assets.put(resourceLocationEntry.getKey().toString(), bytes::clone);
        }
        for (var resourceLocationEntry : dataLocation.entrySet()) {
            var bytes = resourceLocationEntry.getValue();
            data.put(resourceLocationEntry.getKey().toString(), bytes::clone);
        }

        pack = new InMemoryResourcePack(assets, data);
        return pack;
    }

    private void basicConfiguration(Map<ResourceLocation, byte[]> assets, Map<ResourceLocation, byte[]> data) {
        assets.put(location("models/block/sapling.json"), """
                {
                    "ambientocclusion": false,
                    "textures": {
                        "particle": "#sapling"
                    },
                    "elements": [
                        {   "from": [ 0.8, 0, 8 ],
                            "to": [ 15.2, 16, 8 ],
                            "rotation": { "origin": [ 8, 8, 8 ], "axis": "y", "angle": 45, "rescale": true },
                            "shade": false,
                            "faces": {
                                "north": { "uv": [ 0, 0, 16, 16 ], "texture": "#sapling", "tintindex": 0 },
                                "south": { "uv": [ 0, 0, 16, 16 ], "texture": "#sapling", "tintindex": 0 }
                            }
                        },
                        {   "from": [ 8, 0, 0.8 ],
                            "to": [ 8, 16, 15.2 ],
                            "rotation": { "origin": [ 8, 8, 8 ], "axis": "y", "angle": 45, "rescale": true },
                            "shade": false,
                            "faces": {
                                "west": { "uv": [ 0, 0, 16, 16 ], "texture": "#sapling", "tintindex": 0 },
                                "east": { "uv": [ 0, 0, 16, 16 ], "texture": "#sapling", "tintindex": 0 }
                            }
                        }
                    ]
                }
                
                """.getBytes(StandardCharsets.UTF_8));
        assets.put(location("models/block/ore_log.json"), """
                {
                  "parent": "block/cube_column",
                  "elements": [
                    {
                      "from": [ 0, 0, 0 ],
                      "to": [ 16, 16, 16 ],
                      "faces": {
                          "down":  { "texture": "#end",   "cullface": "down" },
                          "up":    { "texture": "#end",   "cullface": "up" },
                          "north": { "texture": "#side",   "cullface": "north" },
                          "south": { "texture": "#side",   "cullface": "south" },
                          "west":  { "texture": "#side",   "cullface": "west" },
                          "east":  { "texture": "#side",   "cullface": "east" }
                      }
                    },
                    {
                      "from": [ 0, 0, 0 ],
                      "to": [ 16, 16, 16 ],
                      "faces": {
                          "down":  { "texture": "#overlay", "tintindex": 0, "cullface": "down" },
                          "up":    { "texture": "#overlay", "tintindex": 0, "cullface": "up" },
                          "north": { "texture": "#overlay", "tintindex": 0, "cullface": "north" },
                          "south": { "texture": "#overlay", "tintindex": 0, "cullface": "south" },
                          "west":  { "texture": "#overlay", "tintindex": 0, "cullface": "west" },
                          "east":  { "texture": "#overlay", "tintindex": 0, "cullface": "east" }
                      }
                    }
                  ]
                }
                """.getBytes(StandardCharsets.UTF_8));
        assets.put(location("models/block/ore_log_horizontal.json"), """
                {
                  "parent": "block/cube_column",
                  "elements": [
                    {
                      "from": [ 0, 0, 0 ],
                      "to": [ 16, 16, 16 ],
                      "faces": {
                          "down":  { "texture": "#end",   "cullface": "down" },
                          "up":    { "texture": "#end", "rotation": 180,  "cullface": "up" },
                          "north": { "texture": "#side",   "cullface": "north" },
                          "south": { "texture": "#side",   "cullface": "south" },
                          "west":  { "texture": "#side",   "cullface": "west" },
                          "east":  { "texture": "#side",   "cullface": "east" }
                      }
                    },
                    {
                      "from": [ 0, 0, 0 ],
                      "to": [ 16, 16, 16 ],
                      "faces": {
                          "down":  { "texture": "#overlay", "tintindex": 0, "cullface": "down" },
                          "up":    { "texture": "#overlay", "tintindex": 0, "cullface": "up" },
                          "north": { "texture": "#overlay", "tintindex": 0, "cullface": "north" },
                          "south": { "texture": "#overlay", "tintindex": 0, "cullface": "south" },
                          "west":  { "texture": "#overlay", "tintindex": 0, "cullface": "west" },
                          "east":  { "texture": "#overlay", "tintindex": 0, "cullface": "east" }
                      }
                    }
                  ]
                }
                """.getBytes(StandardCharsets.UTF_8));

        data.put(ResourceLocation.withDefaultNamespace("tags/items/leaves.json"), createTags(type -> List.of(location(type.id() + "_leaves"))));
        data.put(ResourceLocation.withDefaultNamespace("tags/blocks/leaves.json"), createTags(type -> List.of(location(type.id() + "_leaves"))));
        data.put(ResourceLocation.withDefaultNamespace("tags/items/saplings.json"), createTags(type -> List.of(location(type.id() + "_sapling"))));
        data.put(ResourceLocation.withDefaultNamespace("tags/blocks/saplings.json"), createTags(type -> List.of(location(type.id() + "_sapling"))));
        data.put(ResourceLocation.withDefaultNamespace("tags/blocks/oak_logs.json"), createTags(type -> List.of(location(type.id() + "_log"))));
        data.put(ResourceLocation.withDefaultNamespace("tags/items/oak_logs.json"), createTags(type -> List.of(location(type.id() + "_log"))));
        data.put(location("tags/blocks/essence.json"), createTags(type -> List.of(location(type.id() + "_essence"))));
        data.put(location("tags/items/essence.json"), createTags(type -> List.of(location(type.id() + "_essence"))));
        data.put(location("tags/items/fruits.json"), createTags(type -> List.of(location(type.id() + "_fruit"))));
        data.put(location("tags/items/popped_fruits.json"), createTags(type -> List.of(location("popped_" + type.id() + "_fruit"))));
        data.put(location("tags/items/barks.json"), createTags(type -> List.of(location(type.id() + "_bark"))));
        data.put(location("tags/block/saplings.json"), createTags(type -> List.of(location(type.id() + "_sapling"))));
        data.put(location("tags/items/saplings.json"), createTags(type -> List.of(location(type.id() + "_sapling"))));
        data.put(location("tags/block/leaves.json"), createTags(type -> List.of(location(type.id() + "_leaves"))));
        data.put(location("tags/items/leaves.json"), createTags(type -> List.of(location(type.id() + "_leaves"))));
    }

    private byte[] createTags(Function<TreeType, List<ResourceLocation>> locationFunction) {
        var tags = new JsonObject();
        var tagsValues = new JsonArray();
        for (var treeType : Config.treeTypes) {
            var values = locationFunction.apply(treeType);
            for (var value : values) {
                tagsValues.add(value.toString());
            }
        }
        tags.add("values", tagsValues);
        return tags.toString().getBytes(StandardCharsets.UTF_8);
    }

    private void addLanguage(Map<ResourceLocation, byte[]> data) {
        var json = new JsonObject();

        for (var treeType : Config.treeTypes) {
            var id = treeType.id();
            var name = Character.toUpperCase(id.charAt(0)) + id.substring(1);
            json.addProperty("block.oretrees." + id + "_sapling", name + " Sapling");
            json.addProperty("block.oretrees." + id + "_leaves", name + " Leaves");
            json.addProperty("block.oretrees." + id + "_log", name + " Log");
            json.addProperty("block.oretrees." + id + "_essence", name + " Essence Block");
            json.addProperty("item.oretrees." + id + "_fruit", name + " Fruit");
            json.addProperty("item.oretrees.popped_" + id + "_fruit", "Popped " + name + " Fruit");
            json.addProperty("item.oretrees." + id + "_bark", name + " Bark");
            json.addProperty("oretrees.creativetab.title", "Ore Trees");
        }

        data.put(location("lang/en_us.json"), json.toString().getBytes(StandardCharsets.UTF_8));
    }

    private void addLog(Map<ResourceLocation, byte[]> assets, Map<ResourceLocation, byte[]> data, TreeType treeType) {
        var id = treeType.id();
        var name = id + "_log";
        assets.put(location("blockstates/" + name + ".json"), """
                {
                  "variants": {
                    "axis=x": {
                      "model": "%1$s:block/%2$s_log_horizontal",
                      "x": 90,
                      "y": 90
                    },
                    "axis=y": {
                      "model": "%1$s:block/%2$s_log"
                    },
                    "axis=z": {
                      "model": "%1$s:block/%2$s_log_horizontal",
                      "x": 90
                    }
                  }
                }
                """.formatted(modid, id).getBytes(StandardCharsets.UTF_8));
        assets.put(location("models/block/" + name + ".json"), """
                {
                  "parent": "%1$s:block/ore_log",
                  "textures": {
                    "end": "minecraft:block/oak_log_top",
                    "side": "minecraft:block/oak_log",
                    "overlay": "%1$s:block/ore_overlay"
                  }
                }
                """.formatted(modid).getBytes(StandardCharsets.UTF_8));
        assets.put(location("models/block/" + name + "_horizontal.json"), """
                {
                  "parent": "%1$s:block/ore_log_horizontal",
                  "textures": {
                    "end": "minecraft:block/oak_log_top",
                    "side": "minecraft:block/oak_log",
                    "overlay": "%1$s:block/ore_overlay"
                  }
                }
                """.formatted(modid).getBytes(StandardCharsets.UTF_8));
        assets.put(location("models/item/" + name + ".json"), """
                {
                  "parent": "%1$s:block/%2$s_log"
                }
                """.formatted(modid, id).getBytes(StandardCharsets.UTF_8));

        data.put(location("loot_tables/blocks/" + name + "_normal.json"), """
                {
                  "type": "minecraft:block",
                  "pools": [
                    {
                      "rolls": 1,
                      "entries": [
                        {
                          "type": "minecraft:item",
                          "name": "minecraft:oak_log"
                        }
                      ]
                    },
                    {
                      "rolls": 1,
                      "entries": [
                        {
                          "type": "minecraft:item",
                          "name": "%1$s:%2$s_bark"
                        }
                      ]
                    }
                  ]
                }
                """.formatted(modid, id).getBytes(StandardCharsets.UTF_8));
        data.put(location("loot_tables/blocks/" + name + ".json"), """
                {
                   "type": "minecraft:block",
                   "pools": [
                     {
                       "rolls": 1,
                       "entries": [
                         {
                           "type": "minecraft:alternatives",
                           "children": [
                             {
                               "type": "minecraft:item",
                               "name": "%1$s:%2$s_log",
                               "conditions": [
                                 {
                                   "condition": "minecraft:match_tool",
                                   "predicate": {
                                     "enchantments": [
                                       {
                                         "enchantment": "minecraft:silk_touch",
                                         "levels": {
                                           "min": 1
                                         }
                                       }
                                     ]
                                   }
                                 }
                               ]
                             },
                             {
                               "type": "minecraft:loot_table",
                               "name": "%1$s:blocks/%2$s_log_normal"
                             }
                           ]
                         }
                       ]
                     }
                   ],
                   "random_sequence": "%1$s:blocks/%2$s_log"
                 }
                """.formatted(modid, id).getBytes(StandardCharsets.UTF_8));
    }

    private void addFruits(Map<ResourceLocation, byte[]> assets, Map<ResourceLocation, byte[]> data, TreeType treeType) {
        var id = treeType.id();
        assets.put(location("models/item/" + id + "_fruit.json"), """
                {
                  "parent": "item/generated",
                  "textures": {
                    "layer0": "%s",
                    "layer1": "%s"
                  }
                }
                """.formatted(location("item/fruit"), location("item/fruit_overlay")).getBytes(StandardCharsets.UTF_8));
        assets.put(location("models/item/popped_" + id + "_fruit.json"), """
                {
                  "parent": "item/generated",
                  "textures": {
                    "layer0": "%s"
                  }
                }
                """.formatted(location("item/popped_fruit")).getBytes(StandardCharsets.UTF_8));

        data.put(location("recipes/popped_" + id + "_fruit.json"), """
                {
                  "type": "minecraft:smelting",
                  "category": "food",
                  "cookingtime": 100,
                  "experience": 0.05,
                  "ingredient": {
                    "item": "%1$s:%2$s_fruit"
                  },
                  "result": "%1$s:popped_%2$s_fruit"
                }
                """.formatted(modid, id).getBytes(StandardCharsets.UTF_8));
        data.put(location("recipes/popped_" + id + "_fruit_from_smoking.json"), """
                {
                  "type": "minecraft:smoking",
                  "category": "food",
                  "cookingtime": 50,
                  "experience": 0.05,
                  "ingredient": {
                    "item": "%1$s:%2$s_fruit"
                  },
                  "result": "%1$s:popped_%2$s_fruit"
                }
                """.formatted(modid, id).getBytes(StandardCharsets.UTF_8));
        data.put(location("recipes/popped_" + id + "_fruit_from_campfire.json"), """
                {
                  "type": "minecraft:campfire_cooking",
                  "category": "food",
                  "cookingtime": 300,
                  "experience": 0.05,
                  "ingredient": {
                    "item": "%1$s:%2$s_fruit"
                  },
                  "result": "%1$s:popped_%2$s_fruit"
                }
                """.formatted(modid, id).getBytes(StandardCharsets.UTF_8));
    }

    private void makeBonsai(Map<ResourceLocation, byte[]> assets, Map<ResourceLocation, byte[]> data, TreeType treeType) {
        var id = treeType.id();
        assets.put(ResourceLocation.parse("bonsaitrees3:models/tree/" + modid + "/" + id + ".json"), """
                {
                  "type": "forge:conditional",
                  "recipes": [
                    {
                      "conditions": [
                        {
                          "type": "forge:mod_loaded",
                          "modid": "bonsaitrees3"
                        }
                      ],
                      "recipe": {
                        "type": "bonsaitrees3:sapling/%1$s/%2$s",
                        "version": 3,
                        "ref": {
                          "a": {
                            "block": "%1$s:%2$s_log"
                          },
                          "b": {
                            "block": "%1$s:%2$s_leaves"
                          }
                        },
                        "shape": [
                          [
                            "     ",
                            "     ",
                            "bbbb ",
                            " bbb ",
                            "     ",
                            "     "
                          ],
                          [
                            "  b  ",
                            "  bb ",
                            "bbbbb",
                            "bbbbb",
                            "     ",
                            "     "
                          ],
                          [
                            " bbb ",
                            " bab ",
                            "bbabb",
                            "bbabb",
                            "  a  ",
                            "  a  "
                          ],
                          [
                            "  b  ",
                            " bbb ",
                            "bbbbb",
                            "bbbbb",
                            "     ",
                            "     "
                          ],
                          [
                            "     ",
                            "     ",
                            "bbbbb",
                            " bbbb",
                            "     ",
                            "     "
                          ]
                        ]
                      }
                    }
                  ]
                }
                """.formatted(modid, id).getBytes(StandardCharsets.UTF_8));
        data.put(ResourceLocation.parse("bonsaitrees3:recipes/sapling/oretrees/" + id + ".json"), """
                {
                  "type": "bonsaitrees3:sapling",
                  "compatibleSoilTags": [
                    "dirt",
                    "grass"
                  ],
                  "drops": [
                    {
                      "chance": 0.02,
                      "result": {
                        "item": "%1$s:%2$s_sapling"
                      },
                      "rolls": 1
                    },
                    {
                      "chance": 0.75,
                      "result": {
                        "item": "minecraft:oak_log"
                      },
                      "rolls": 1
                    },
                    {
                      "chance": 0.75,
                      "result": {
                        "item": "%1$s:%2$s_bark"
                      },
                      "rolls": 1
                    },
                    {
                      "chance": 0.2,
                      "result": {
                        "item": "minecraft:stick"
                      },
                      "rolls": 3
                    },
                    {
                      "chance": 0.2,
                      "requiresSilkTouch": true,
                      "result": {
                        "item": "%1$s:%2$s_leaves"
                      },
                      "rolls": 2
                    },
                    {
                      "chance": 0.65,
                      "requiresBees": true,
                      "result": {
                        "item": "%1$s:%2$s_fruit"
                      },
                      "rolls": 1
                    }
                  ],
                  "mod": "%1$s",
                  "sapling": [
                    {
                      "item": "%1$s:%2$s_sapling"
                    }
                  ]
                }
                """.formatted(modid, id).getBytes(StandardCharsets.UTF_8));
    }

    private void makeBotany(Map<ResourceLocation, byte[]> assets, Map<ResourceLocation, byte[]> data, TreeType treeType) {
        var id = treeType.id();
        data.put(ResourceLocation.parse("botanypots:recipes/oretrees/crop/" + id + ".json"), """
                {
                  "type": "forge:conditional",
                  "recipes": [
                    {
                      "conditions": [
                        {
                          "type": "forge:mod_loaded",
                          "modid": "botanypots"
                        }
                      ],
                      "recipe": {
                        "type": "botanypots:crop",
                        "seed": {
                          "item": "%1$s:%2$s_sapling"
                        },
                        "categories": [
                          "dirt"
                        ],
                        "growthTicks": 1200,
                        "display": {
                          "block": "%1$s:%2$s_sapling"
                        },
                        "drops": [
                          {
                            "chance": 0.02,
                            "output": {
                              "item": "%1$s:%2$s_sapling"
                            }
                          },
                          {
                            "chance": 0.75,
                            "output": {
                              "item": "minecraft:oak_log"
                            }
                          },
                          {
                            "chance": 0.75,
                            "output": {
                              "item": "%1$s:%2$s_bark"
                            }
                          },
                          {
                            "chance": 0.2,
                            "output": {
                              "item": "minecraft:stick"
                            },
                            "minRolls": 3,
                            "maxRolls": 3
                          },
                          {
                            "chance": 0.2,
                            "output": {
                              "item": "%1$s:%2$s_leaves"
                            },
                            "minRolls": 2,
                            "maxRolls": 2
                          },
                          {
                            "chance": 0.65,
                            "output": {
                              "item": "%1$s:%2$s_fruit"
                            }
                          }
                        ]
                      }
                    }
                  ]
                }
                """.formatted(modid, id).getBytes(StandardCharsets.UTF_8));
    }

    private void addBark(Map<ResourceLocation, byte[]> assets, Map<ResourceLocation, byte[]> data, TreeType treeType) {
        var id = treeType.id();
        var name = id + "_bark";
        assets.put(location("models/item/" + name + ".json"), """
                {
                	"parent": "item/generated",
                	"textures": {
                		"layer0": "%1$s",
                		"layer1": "%2$s"
                	}
                }
                """.formatted(location("item/bark"), location("item/bark_overlay")).getBytes(StandardCharsets.UTF_8));
    }

    private void addEssence(Map<ResourceLocation, byte[]> assets, Map<ResourceLocation, byte[]> data, TreeType treeType) {
        var id = treeType.id();
        var name = id + "_essence";
        assets.put(location("blockstates/" + name + ".json"), """
                {
                  "variants": {
                    "": {
                      "model": "%s"
                    }
                  }
                }
                """.formatted(location("block/" + name)).getBytes(StandardCharsets.UTF_8));
        assets.put(location("models/block/" + name + ".json"), """
                {
                  "parent": "minecraft:block/leaves",
                  "textures": {
                    "all": "%s"
                  }
                }
                """.formatted(location("block/essence")).getBytes(StandardCharsets.UTF_8));
        assets.put(location("models/item/" + name + ".json"), """
                {
                  "parent": "%s"
                }
                """.formatted(location("block/" + name)).getBytes(StandardCharsets.UTF_8));

        data.put(location("recipes/" + name + ".json"), """
                {
                        "type": "minecraft:crafting_shaped",
                        "category": "building",
                        "group": "essence",
                        "show_notification": true,
                        "pattern": [
                          "#.#",
                          ".#.",
                          "#.#"
                        ],
                        "key": {
                          "#": {
                            "item": "%1$s:%2$s_bark"
                          },
                          ".": {
                            "item": "%1$s:%2$s_fruit"
                          }
                        },
                        "result": {
                          "item": "%1$s:%2$s_essence"
                        }
                      }
                    }
                  ]
                }
                """.formatted(modid, id).getBytes(StandardCharsets.UTF_8));
    }

    private void addLeaves(Map<ResourceLocation, byte[]> assets, Map<ResourceLocation, byte[]> data, TreeType treeType) {
        var id = treeType.id();
        var name = id + "_leaves";
        var textureLocation = ResourceLocation.fromNamespaceAndPath(modid, "block/oak_leaves");
        assets.put(location("blockstates/" + name + ".json"), """
                {
                  "variants": {
                    "": {
                      "model": "%s"
                    }
                  }
                }
                """.formatted(location("block/" + name)).getBytes(StandardCharsets.UTF_8));
        assets.put(location("models/block/" + name + ".json"), """
                {
                  "parent": "minecraft:block/leaves",
                  "textures": {
                    "all": "%s"
                  }
                }
                """.formatted(textureLocation).getBytes(StandardCharsets.UTF_8));
        assets.put(location("models/item/" + name + ".json"), """
                {
                  "parent": "%s"
                }
                """.formatted(location("block/" + name)).getBytes(StandardCharsets.UTF_8));

        data.put(location("loot_tables/blocks/" + name + ".json"), """
                {
                  "type": "minecraft:block",
                  "pools": [
                    {
                      "bonus_rolls": 0.0,
                      "entries": [
                        {
                          "type": "minecraft:alternatives",
                          "children": [
                            {
                              "type": "minecraft:item",
                              "conditions": [
                                {
                                  "condition": "minecraft:any_of",
                                  "terms": [
                                    {
                                      "condition": "minecraft:match_tool",
                                      "predicate": {
                                        "items": [
                                          "minecraft:shears"
                                        ]
                                      }
                                    },
                                    {
                                      "condition": "minecraft:match_tool",
                                      "predicate": {
                                        "enchantments": [
                                          {
                                            "enchantment": "minecraft:silk_touch",
                                            "levels": {
                                              "min": 1
                                            }
                                          }
                                        ]
                                      }
                                    }
                                  ]
                                }
                              ],
                              "name": "%2$s"
                            },
                            {
                              "type": "minecraft:item",
                              "conditions": [
                                {
                                  "condition": "minecraft:survives_explosion"
                                },
                                {
                                  "chances": [
                                    0.05,
                                    0.0625,
                                    0.083333336,
                                    0.1
                                  ],
                                  "condition": "minecraft:table_bonus",
                                  "enchantment": "minecraft:fortune"
                                }
                              ],
                              "name": "%3$s"
                            }
                          ]
                        }
                      ],
                      "rolls": 1.0
                    },
                    {
                      "bonus_rolls": 0.0,
                      "conditions": [
                        {
                          "condition": "minecraft:inverted",
                          "term": {
                            "condition": "minecraft:any_of",
                            "terms": [
                              {
                                "condition": "minecraft:match_tool",
                                "predicate": {
                                  "items": [
                                    "minecraft:shears"
                                  ]
                                }
                              },
                              {
                                "condition": "minecraft:match_tool",
                                "predicate": {
                                  "enchantments": [
                                    {
                                      "enchantment": "minecraft:silk_touch",
                                      "levels": {
                                        "min": 1
                                      }
                                    }
                                  ]
                                }
                              }
                            ]
                          }
                        }
                      ],
                      "entries": [
                        {
                          "type": "minecraft:item",
                          "conditions": [
                            {
                              "chances": [
                                0.02,
                                0.022222223,
                                0.025,
                                0.033333335,
                                0.1
                              ],
                              "condition": "minecraft:table_bonus",
                              "enchantment": "minecraft:fortune"
                            }
                          ],
                          "functions": [
                            {
                              "add": false,
                              "count": {
                                "type": "minecraft:uniform",
                                "max": 2.0,
                                "min": 1.0
                              },
                              "function": "minecraft:set_count"
                            },
                            {
                              "function": "minecraft:explosion_decay"
                            }
                          ],
                          "name": "minecraft:stick"
                        }
                      ],
                      "rolls": 1.0
                    },
                    {
                      "bonus_rolls": 0.0,
                      "conditions": [
                        {
                          "condition": "minecraft:inverted",
                          "term": {
                            "condition": "minecraft:any_of",
                            "terms": [
                              {
                                "condition": "minecraft:match_tool",
                                "predicate": {
                                  "items": [
                                    "minecraft:shears"
                                  ]
                                }
                              },
                              {
                                "condition": "minecraft:match_tool",
                                "predicate": {
                                  "enchantments": [
                                    {
                                      "enchantment": "minecraft:silk_touch",
                                      "levels": {
                                        "min": 1
                                      }
                                    }
                                  ]
                                }
                              }
                            ]
                          }
                        }
                      ],
                      "entries": [
                        {
                          "type": "minecraft:item",
                          "conditions": [
                            {
                              "condition": "minecraft:survives_explosion"
                            },
                            {
                              "chances": [
                                0.005,
                                0.0055555557,
                                0.00625,
                                0.008333334,
                                0.025
                              ],
                              "condition": "minecraft:table_bonus",
                              "enchantment": "minecraft:fortune"
                            }
                          ],
                          "name": "%4$s"
                        }
                      ],
                      "rolls": 1.0
                    }
                  ],
                  "random_sequence": "%1$s"
                }
                """
                .formatted(location("blocks/" + name), location(name), location(id + "_sapling"), location(id + "_fruit"))
                .getBytes(StandardCharsets.UTF_8));
    }

    private void addSapling(Map<ResourceLocation, byte[]> assets, HashMap<ResourceLocation, byte[]> data, TreeType treeType) {
        var id = treeType.id();
        var name = id + "_sapling";
        var textureLocation = ResourceLocation.fromNamespaceAndPath(modid, "block/oak_sapling");
        assets.put(location("blockstates/" + name + ".json"), """
                {
                  "variants": {
                    "": {
                      "model": "%s"
                    }
                  }
                }
                """.formatted(location("block/" + name)).getBytes(StandardCharsets.UTF_8));
        assets.put(location("models/block/" + name + ".json"), """
                {
                  "parent": "oretrees:block/sapling",
                  "textures": {
                    "sapling": "%s"
                  }
                }
                """.formatted(textureLocation).getBytes(StandardCharsets.UTF_8));
        assets.put(location("models/item/" + name + ".json"), """
                {
                  "parent": "minecraft:item/generated",
                  "textures": {
                    "layer0": "%s"
                  }
                }
                """.formatted(textureLocation).getBytes(StandardCharsets.UTF_8));

        data.put(location("worldgen/configured_feature/" + id + ".json"), """
                {
                  "type": "minecraft:tree",
                  "config": {
                    "decorators": [],
                    "dirt_provider": {
                      "type": "minecraft:simple_state_provider",
                      "state": {
                        "Name": "minecraft:dirt"
                      }
                    },
                    "foliage_placer": {
                      "type": "minecraft:blob_foliage_placer",
                      "height": 3,
                      "offset": 0,
                      "radius": 2
                    },
                    "foliage_provider": {
                      "type": "minecraft:simple_state_provider",
                      "state": {
                        "Name": "%1$s",
                        "Properties": {
                          "distance": "7",
                          "persistent": "false",
                          "waterlogged": "false"
                        }
                      }
                    },
                    "force_dirt": false,
                    "ignore_vines": true,
                    "minimum_size": {
                      "type": "minecraft:two_layers_feature_size",
                      "limit": 1,
                      "lower_size": 0,
                      "upper_size": 1
                    },
                    "trunk_placer": {
                      "type": "minecraft:straight_trunk_placer",
                      "base_height": 4,
                      "height_rand_a": 2,
                      "height_rand_b": 0
                    },
                    "trunk_provider": {
                      "type": "minecraft:simple_state_provider",
                      "state": {
                        "Name": "%2$s",
                        "Properties": {
                          "axis": "y"
                        }
                      }
                    }
                  }
                }
                """.formatted(location(id + "_leaves"), location(id + "_log")).getBytes(StandardCharsets.UTF_8));
        data.put(location("worldgen/configured_feature/" + id + "_bees.json"), """
                {
                  "type": "minecraft:tree",
                  "config": {
                    "decorators": [
                      {
                        "type": "minecraft:beehive",
                        "probability": 0.05
                      }
                    ],
                    "dirt_provider": {
                      "type": "minecraft:simple_state_provider",
                      "state": {
                        "Name": "minecraft:dirt"
                      }
                    },
                    "foliage_placer": {
                      "type": "minecraft:blob_foliage_placer",
                      "height": 3,
                      "offset": 0,
                      "radius": 2
                    },
                    "foliage_provider": {
                      "type": "minecraft:simple_state_provider",
                      "state": {
                        "Name": "%1$s",
                        "Properties": {
                          "distance": "7",
                          "persistent": "false",
                          "waterlogged": "false"
                        }
                      }
                    },
                    "force_dirt": false,
                    "ignore_vines": true,
                    "minimum_size": {
                      "type": "minecraft:two_layers_feature_size",
                      "limit": 1,
                      "lower_size": 0,
                      "upper_size": 1
                    },
                    "trunk_placer": {
                      "type": "minecraft:straight_trunk_placer",
                      "base_height": 4,
                      "height_rand_a": 2,
                      "height_rand_b": 0
                    },
                    "trunk_provider": {
                      "type": "minecraft:simple_state_provider",
                      "state": {
                        "Name": "%2$s",
                        "Properties": {
                          "axis": "y"
                        }
                      }
                    }
                  }
                }
                """.formatted(location(id + "_leaves"), location(id + "_log")).getBytes(StandardCharsets.UTF_8));
        data.put(location("worldgen/configured_feature/" + id + "_fancy.json"), """
                {
                  "type": "minecraft:tree",
                  "config": {
                    "decorators": [],
                    "dirt_provider": {
                      "type": "minecraft:simple_state_provider",
                      "state": {
                        "Name": "minecraft:dirt"
                      }
                    },
                    "foliage_placer": {
                      "type": "minecraft:fancy_foliage_placer",
                      "height": 4,
                      "offset": 4,
                      "radius": 2
                    },
                    "foliage_provider": {
                      "type": "minecraft:simple_state_provider",
                      "state": {
                        "Name": "%1$s",
                        "Properties": {
                          "distance": "7",
                          "persistent": "false",
                          "waterlogged": "false"
                        }
                      }
                    },
                    "force_dirt": false,
                    "ignore_vines": true,
                    "minimum_size": {
                      "type": "minecraft:two_layers_feature_size",
                      "limit": 0,
                      "lower_size": 0,
                      "min_clipped_height": 4,
                      "upper_size": 0
                    },
                    "trunk_placer": {
                      "type": "minecraft:fancy_trunk_placer",
                      "base_height": 3,
                      "height_rand_a": 11,
                      "height_rand_b": 0
                    },
                    "trunk_provider": {
                      "type": "minecraft:simple_state_provider",
                      "state": {
                        "Name": "%2$s",
                        "Properties": {
                          "axis": "y"
                        }
                      }
                    }
                  }
                }
                """.formatted(location(id + "_leaves"), location(id + "_log")).getBytes(StandardCharsets.UTF_8));
        data.put(location("worldgen/configured_feature/" + id + "_fancy_bees.json"), """
                {
                  "type": "minecraft:tree",
                  "config": {
                    "decorators": [
                      {
                        "type": "minecraft:beehive",
                        "probability": 0.05
                      }
                    ],
                    "dirt_provider": {
                      "type": "minecraft:simple_state_provider",
                      "state": {
                        "Name": "minecraft:dirt"
                      }
                    },
                    "foliage_placer": {
                      "type": "minecraft:fancy_foliage_placer",
                      "height": 4,
                      "offset": 4,
                      "radius": 2
                    },
                    "foliage_provider": {
                      "type": "minecraft:simple_state_provider",
                      "state": {
                        "Name": "%1$s",
                        "Properties": {
                          "distance": "7",
                          "persistent": "false",
                          "waterlogged": "false"
                        }
                      }
                    },
                    "force_dirt": false,
                    "ignore_vines": true,
                    "minimum_size": {
                      "type": "minecraft:two_layers_feature_size",
                      "limit": 0,
                      "lower_size": 0,
                      "min_clipped_height": 4,
                      "upper_size": 0
                    },
                    "trunk_placer": {
                      "type": "minecraft:fancy_trunk_placer",
                      "base_height": 3,
                      "height_rand_a": 11,
                      "height_rand_b": 0
                    },
                    "trunk_provider": {
                      "type": "minecraft:simple_state_provider",
                      "state": {
                        "Name": "%2$s",
                        "Properties": {
                          "axis": "y"
                        }
                      }
                    }
                  }
                }
                """.formatted(location(id + "_leaves"), location(id + "_log")).getBytes(StandardCharsets.UTF_8));
    }

    private ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(modid, path);
    }

    @Override
    public void loadPacks(@NotNull Consumer<Pack> packConsumer) {
        var id = OreTrees.MODID + "-generated";
        var title = Component.literal("OreTrees Generated Resources");
        var required = true;
        var packInfo = new Pack.Info(Component.empty(), 0, 15, FeatureFlagSet.of(), true);

        packConsumer.accept(Pack.create(id, title, required, name -> pack(), packInfo, PackType.CLIENT_RESOURCES, Pack.Position.TOP, true, PackSource.BUILT_IN));
        packConsumer.accept(Pack.create(id, title, required, name -> pack(), packInfo, PackType.SERVER_DATA, Pack.Position.TOP, true, PackSource.BUILT_IN));
    }
}
