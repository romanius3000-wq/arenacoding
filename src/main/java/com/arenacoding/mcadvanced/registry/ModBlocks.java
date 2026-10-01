package com.arenacoding.mcadvanced.registry;

import com.arenacoding.mcadvanced.MinecraftAdvanced;
import com.arenacoding.mcadvanced.block.BossAltarBlock;
import com.arenacoding.mcadvanced.block.WorldPortalBlock;
import com.arenacoding.mcadvanced.util.ModWorlds;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.arenacoding.mcadvanced.MinecraftAdvanced.MOD_ID;

/**
 * Все новые блоки: 12 руд, 12 блоков самоцветов, 12 порталов,
 * блоки хаба и алтарь боссов.
 */
public final class ModBlocks {
    private static final Map<String, Block> ORES = new LinkedHashMap<>();
    private static final Map<String, Block> GEM_BLOCKS = new LinkedHashMap<>();
    private static final Map<String, Block> PORTALS = new LinkedHashMap<>();

    public static Block HUB_STONE;
    public static Block HUB_STONE_BRICKS;
    public static Block HUB_PILLAR;
    public static Block HUB_LANTERN;
    public static Block BOSS_ALTAR;

    public static void init() {
        for (ModWorlds.WorldInfo world : ModWorlds.WORLDS) {
            ORES.put(world.gem(), register(world.gem() + "_ore",
                    new Block(props(world.gem() + "_ore")
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE))));
            GEM_BLOCKS.put(world.gem(), register(world.gem() + "_block",
                    new Block(props(world.gem() + "_block")
                            .strength(5.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.METAL))));
            PORTALS.put(world.id(), register("portal_" + world.id(),
                    new WorldPortalBlock(props("portal_" + world.id())
                            .strength(-1.0f, 3600000.0f)
                            .lightLevel(state -> 13), world)));
        }
        HUB_STONE = register("hub_stone",
                new Block(props("hub_stone").strength(1.8f, 8.0f)));
        HUB_STONE_BRICKS = register("hub_stone_bricks",
                new Block(props("hub_stone_bricks").strength(2.0f, 8.0f)));
        HUB_PILLAR = register("hub_pillar",
                new Block(props("hub_pillar").strength(2.0f, 8.0f)));
        HUB_LANTERN = register("hub_lantern",
                new Block(props("hub_lantern").strength(0.3f)
                        .sound(SoundType.LANTERN).lightLevel(state -> 15)));
        BOSS_ALTAR = register("boss_altar",
                new BossAltarBlock(props("boss_altar").strength(3.0f, 9.0f)
                        .lightLevel(state -> 7)));
        MinecraftAdvanced.LOGGER.info("Зарегистрировано блоков: {}", ORES.size() * 3 + 5);
    }

    private static Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(MOD_ID, name);
    }

    private static ResourceKey<Block> key(String name) {
        return ResourceKey.create(Registries.BLOCK, id(name));
    }

    private static BlockBehaviour.Properties props(String name) {
        return BlockBehaviour.Properties.of().setId(key(name));
    }

    private static Block register(String name, Block block) {
        return Registry.register(BuiltInRegistries.BLOCK, id(name), block);
    }

    public static Block ore(String gem) {
        return ORES.get(gem);
    }

    public static Block gemBlock(String gem) {
        return GEM_BLOCKS.get(gem);
    }

    public static Block portalBlock(ModWorlds.WorldInfo world) {
        return PORTALS.get(world.id());
    }

    public static Block portalBlock(String worldId) {
        return PORTALS.get(worldId);
    }

    private ModBlocks() {
    }
}
