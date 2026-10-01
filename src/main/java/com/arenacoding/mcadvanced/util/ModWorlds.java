package com.arenacoding.mcadvanced.util;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.List;

import static com.arenacoding.mcadvanced.MinecraftAdvanced.MOD_ID;

/**
 * Реестр 12 миров + хаба. Индексы миров важны: они передаются
 * по сети (WorldSelectorScreen -> SelectWorldPayload).
 */
public final class ModWorlds {
    public record WorldInfo(
            String id, ResourceKey<Level> dimension, String gem,
            Component displayName, int portalColor) {
        public Identifier dimensionId() {
            return Identifier.fromNamespaceAndPath(MOD_ID, id);
        }
    }

    public static final List<WorldInfo> WORLDS = List.of(
            new WorldInfo("plains_kingdom", dim("plains_kingdom"), "adurite", Component.translatable("biome.mcadvanced.plains_kingdom"), 0x6a8fb5),
            new WorldInfo("desert_realm", dim("desert_realm"), "sunstone", Component.translatable("biome.mcadvanced.desert_realm"), 0xf0a13a),
            new WorldInfo("frozen_waste", dim("frozen_waste"), "glacite", Component.translatable("biome.mcadvanced.frozen_waste"), 0x7fd4e8),
            new WorldInfo("mountain_peaks", dim("mountain_peaks"), "titanite", Component.translatable("biome.mcadvanced.mountain_peaks"), 0x9a7fb8),
            new WorldInfo("savanna_expanse", dim("savanna_expanse"), "amber", Component.translatable("biome.mcadvanced.savanna_expanse"), 0xffb830),
            new WorldInfo("enchanted_forest", dim("enchanted_forest"), "moonstone", Component.translatable("biome.mcadvanced.enchanted_forest"), 0xc9d7f2),
            new WorldInfo("jungle_depths", dim("jungle_depths"), "jade", Component.translatable("biome.mcadvanced.jungle_depths"), 0x3fae6a),
            new WorldInfo("swamp_marsh", dim("swamp_marsh"), "mirestone", Component.translatable("biome.mcadvanced.swamp_marsh"), 0x8a9a5b),
            new WorldInfo("volcanic_wastes", dim("volcanic_wastes"), "infernite", Component.translatable("biome.mcadvanced.volcanic_wastes"), 0xe2543a),
            new WorldInfo("azure_isles", dim("azure_isles"), "aquamarine", Component.translatable("biome.mcadvanced.azure_isles"), 0x4fd0c7),
            new WorldInfo("void_expanse", dim("void_expanse"), "nullite", Component.translatable("biome.mcadvanced.void_expanse"), 0x5a4a8a),
            new WorldInfo("mystic_grove", dim("mystic_grove"), "shroomite", Component.translatable("biome.mcadvanced.mystic_grove"), 0xd95fd0)
    );

    public static final ResourceKey<Level> HUB = dim("hub");

    private static ResourceKey<Level> dim(String id) {
        return ResourceKey.create(Registries.DIMENSION, Identifier.fromNamespaceAndPath(MOD_ID, id));
    }

    public static WorldInfo byIndex(int index) {
        return WORLDS.get(Math.floorMod(index, WORLDS.size()));
    }

    public static int count() {
        return WORLDS.size();
    }

    /** Инициализация (вызывается из onInitialize) — держит всё нагруженным. */
    public static void init() {
        // кастомный чанк-генератор хаба — регистрируем до загрузки датапаков
        Registry.register(BuiltInRegistries.CHUNK_GENERATOR,
                Identifier.fromNamespaceAndPath(MOD_ID, "hub_room"),
                com.arenacoding.mcadvanced.world.HubRoomGenerator.CODEC);
    }

    private ModWorlds() {
    }
}
