package com.arenacoding.mcadvanced.registry;

import com.arenacoding.mcadvanced.MinecraftAdvanced;
import com.arenacoding.mcadvanced.item.ArtifactStaffItem;
import com.arenacoding.mcadvanced.item.BossSummonItem;
import com.arenacoding.mcadvanced.item.HubCompassItem;
import com.arenacoding.mcadvanced.item.SoulItem;
import com.arenacoding.mcadvanced.item.WorldSelectorItem;
import com.arenacoding.mcadvanced.util.ModWorlds;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.arenacoding.mcadvanced.MinecraftAdvanced.MOD_ID;

/**
 * Все предметы: гемы, души, инструменты, броня, артефакты боссов,
 * предметы призыва, еда, утилита и яйца спавна.
 */
public final class ModItems {
    private static final Map<String, Item> ALL = new LinkedHashMap<>();

    // категории для креативных вкладок
    static final List<Item> TAB_BLOCKS = new ArrayList<>();
    static final List<Item> TAB_NATURAL = new ArrayList<>();
    static final List<Item> TAB_TOOLS = new ArrayList<>();
    static final List<Item> TAB_COMBAT = new ArrayList<>();
    static final List<Item> TAB_FOOD = new ArrayList<>();
    static final List<Item> TAB_INGREDIENTS = new ArrayList<>();
    static final List<Item> TAB_EGGS = new ArrayList<>();

    public static Item HUB_COMPASS;
    public static Item WORLD_SELECTOR;
    public static Item CROWN_OF_WORLDS;

    public static void init() {
        // ---- блоки -> предметы ----
        for (ModWorlds.WorldInfo world : ModWorlds.WORLDS) {
            blockItem(world.gem() + "_ore", ModBlocks.ore(world.gem()), TAB_NATURAL);
            blockItem(world.gem() + "_block", ModBlocks.gemBlock(world.gem()), TAB_BLOCKS);
            blockItem("portal_" + world.id(), ModBlocks.portalBlock(world), TAB_BLOCKS);
        }
        blockItem("hub_stone", ModBlocks.HUB_STONE, TAB_BLOCKS);
        blockItem("hub_stone_bricks", ModBlocks.HUB_STONE_BRICKS, TAB_BLOCKS);
        blockItem("hub_pillar", ModBlocks.HUB_PILLAR, TAB_BLOCKS);
        blockItem("hub_lantern", ModBlocks.HUB_LANTERN, TAB_BLOCKS);
        blockItem("boss_altar", ModBlocks.BOSS_ALTAR, TAB_BLOCKS);

        // ---- гемы и души ----
        for (ModWorlds.WorldInfo world : ModWorlds.WORLDS) {
            item(world.gem(), p -> new Item(p), TAB_INGREDIENTS);
            item(world.gem() + "_soul",
                    p -> new SoulItem(p.stacksTo(16).rarity(Rarity.RARE)), TAB_INGREDIENTS);
        }

        // ---- инструменты и броня (4 тира) ----
        registerTier("adurite", ToolMaterial.DIAMOND, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1561, 4.0f,
                new int[]{3, 6, 5, 2}, 2.0f, 0.05f);
        registerTier("glacite", ToolMaterial.DIAMOND, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1661, 4.5f,
                new int[]{3, 6, 5, 2}, 2.0f, 0.05f);
        registerTier("infernite", ToolMaterial.NETHERITE, BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 1800, 5.0f,
                new int[]{3, 7, 5, 2}, 2.5f, 0.1f);
        registerTier("nullite", ToolMaterial.NETHERITE, BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2031, 5.5f,
                new int[]{4, 7, 6, 3}, 3.0f, 0.15f);

        // ---- еда ----
        item("game_meat", p -> new Item(p.food(new FoodProperties(3, 0.3f, false))), TAB_FOOD);
        item("cooked_game_meat", p -> new Item(p.food(new FoodProperties(8, 0.8f, false))), TAB_FOOD);
        item("frost_berry", p -> new Item(p.food(new FoodProperties(4, 0.6f, false))), TAB_FOOD);
        item("mystic_stew",
                p -> new Item(p.food(new FoodProperties(10, 0.9f, true)).rarity(Rarity.UNCOMMON)), TAB_FOOD);

        // ---- утилита ----
        HUB_COMPASS = item("hub_compass",
                p -> new HubCompassItem(p.stacksTo(1).rarity(Rarity.UNCOMMON)), TAB_TOOLS);
        WORLD_SELECTOR = item("world_selector",
                p -> new WorldSelectorItem(p.stacksTo(1).rarity(Rarity.UNCOMMON)), TAB_TOOLS);
        CROWN_OF_WORLDS = item("crown_of_worlds",
                p -> new Item(p.humanoidArmor(crownMaterial(), ArmorType.HELMET)
                        .rarity(Rarity.EPIC).fireResistant()), TAB_COMBAT);

        // ---- артефакты боссов ----
        bossArtifact("kings_blade", 9.0f);
        staffArtifact("pharaoh_scepter");
        bossArtifact("yeti_frostmaul", 10.0f);
        bossArtifact("titan_hammer", 11.0f);
        bossArtifact("sun_fang", 8.0f);
        bossArtifact("treant_club", 10.0f);
        staffArtifact("serpent_staff");
        bossArtifact("witchblade", 9.0f);
        bossArtifact("inferno_blade", 12.0f);
        staffArtifact("storm_caller");
        bossArtifact("sovereign_edge", 13.0f);
        staffArtifact("spore_scepter");

        // ---- предметы призыва боссов ----
        for (String bossId : BOSSES) {
            summonItem(bossId);
        }

        // ---- яйца спавна ----
        for (Map.Entry<String, EntityType<?>> entry : allEntities()) {
            item(entry.getKey() + "_spawn_egg",
                    p -> new SpawnEggItem(p.spawnEgg(entry.getValue())), TAB_EGGS);
        }

        MinecraftAdvanced.LOGGER.info("Зарегистрировано предметов: {}", ALL.size());
    }

    private static final String[] BOSSES = {
            "bandit_king", "pharaoh", "yeti", "stone_titan", "sun_predator",
            "elder_treant", "vine_serpent", "bog_horror", "inferno_lord",
            "storm_king", "void_sovereign", "spore_mother"
    };

    // ------------------------------------------------------------------

    private static void registerTier(String gem, ToolMaterial baseMaterial,
                                     TagKey<Block> incorrectTag,
                                     int durability, float swordDamage,
                                     int[] defense, float toughness, float kbRes) {
        ToolMaterial material = new ToolMaterial(incorrectTag, durability,
                baseMaterial.speed(), baseMaterial.attackDamageBonus(),
                baseMaterial.enchantmentValue(), repairTag(gem));

        item(gem + "_sword", p -> new Item(p.sword(material, swordDamage, -2.4f)), TAB_COMBAT);
        item(gem + "_pickaxe", p -> new Item(p.pickaxe(material, 1.5f, -2.8f)), TAB_TOOLS);
        item(gem + "_axe", p -> new Item(p.axe(material, 6.0f, -3.0f)), TAB_TOOLS);
        item(gem + "_shovel", p -> new Item(p.shovel(material, 1.5f, -3.0f)), TAB_TOOLS);
        item(gem + "_hoe", p -> new Item(p.hoe(material, -3.0f, 0.0f)), TAB_TOOLS);

        ArmorMaterial armor = new ArmorMaterial(37,
                Map.of(ArmorType.BOOTS, defense[3], ArmorType.LEGGINGS, defense[2],
                        ArmorType.CHESTPLATE, defense[1], ArmorType.HELMET, defense[0],
                        ArmorType.BODY, defense[1]),
                18, SoundEvents.ARMOR_EQUIP_DIAMOND, toughness, kbRes,
                repairTag(gem),
                ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath(MOD_ID, gem)));
        item(gem + "_helmet", p -> new Item(p.humanoidArmor(armor, ArmorType.HELMET)), TAB_COMBAT);
        item(gem + "_chestplate", p -> new Item(p.humanoidArmor(armor, ArmorType.CHESTPLATE)), TAB_COMBAT);
        item(gem + "_leggings", p -> new Item(p.humanoidArmor(armor, ArmorType.LEGGINGS)), TAB_COMBAT);
        item(gem + "_boots", p -> new Item(p.humanoidArmor(armor, ArmorType.BOOTS)), TAB_COMBAT);
    }

    private static ArmorMaterial crownMaterial() {
        return new ArmorMaterial(37,
                Map.of(ArmorType.HELMET, 4, ArmorType.CHESTPLATE, 0, ArmorType.LEGGINGS, 0,
                        ArmorType.BOOTS, 0, ArmorType.BODY, 0),
                30, SoundEvents.ARMOR_EQUIP_GOLD, 3.0f, 0.2f,
                TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, "crown_repair")),
                ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath(MOD_ID, "crown_of_worlds")));
    }

    private static TagKey<Item> repairTag(String gem) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, gem + "_repair"));
    }

    private static void bossArtifact(String id, float damage) {
        item(id, p -> new Item(p.sword(ToolMaterial.NETHERITE, damage, -2.4f)
                .rarity(Rarity.EPIC).fireResistant()), TAB_COMBAT);
    }

    private static void staffArtifact(String id) {
        item(id, p -> new ArtifactStaffItem(p.durability(2048)
                .rarity(Rarity.EPIC).fireResistant()), TAB_COMBAT);
    }

    @SuppressWarnings("unchecked")
    private static void summonItem(String bossId) {
        Supplier<EntityType<? extends Mob>> supplier =
                () -> (EntityType<? extends Mob>) ModEntities.byId(bossId);
        item("summon_" + bossId,
                p -> new BossSummonItem(p.stacksTo(4).rarity(Rarity.RARE), supplier), TAB_TOOLS);
    }

    // ------------------------------------------------------------------

    private static Item.Properties props(String name) {
        return new Item.Properties().setId(ResourceKey.create(Registries.ITEM,
                Identifier.fromNamespaceAndPath(MOD_ID, name)));
    }

    private static <I extends Item> I item(String name, Function<Item.Properties, I> factory, List<Item> tab) {
        I item = factory.apply(props(name));
        Registry.register(BuiltInRegistries.ITEM,
                Identifier.fromNamespaceAndPath(MOD_ID, name), item);
        ALL.put(name, item);
        if (tab != null) {
            tab.add(item);
        }
        return item;
    }

    private static void blockItem(String name, Block block, List<Item> tab) {
        item(name, p -> new BlockItem(block, p), tab);
    }

    public static Item item(String name) {
        return ALL.get(name);
    }

    /** Список всех зарегистрированных сущностей мода (id -> тип). */
    private static List<Map.Entry<String, EntityType<?>>> allEntities() {
        List<Map.Entry<String, EntityType<?>>> list = new ArrayList<>();
        for (String id : ENTITY_IDS) {
            EntityType<?> type = ModEntities.byId(id);
            if (type != null) {
                list.add(Map.entry(id, type));
            }
        }
        return list;
    }

    private static final String[] ENTITY_IDS = {
            // мобы
            "bandit", "bull", "mummy", "scorpion", "frost_reaver", "frost_stag",
            "rock_golem", "lion", "zebra", "treant", "forest_wisp", "vine_spider",
            "jungle_brute", "bog_witch", "magma_beast", "ash_wraith", "pirate_ghost",
            "shore_crawler", "void_wraith", "null_crawler", "spore_slime", "mushroomling",
            // боссы
            "bandit_king", "pharaoh", "yeti", "stone_titan", "sun_predator",
            "elder_treant", "vine_serpent", "bog_horror", "inferno_lord",
            "storm_king", "void_sovereign", "spore_mother",
            // торговцы
            "hunter_trader", "alchemist_trader", "blacksmith_trader"
    };

    private ModItems() {
    }
}
