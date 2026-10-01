# -*- coding: utf-8 -*-
"""
Minecraft Advanced — центральный манифест контента.
Один источник данных для генераторов текстур, ассетов, датапака и Java-кода.
"""

MOD_ID = "mcadvanced"
NAMESPACE = MOD_ID

# ---------------------------------------------------------------------------
# 12 миров: (dim_id, ru, en, gem, gem_color, sky, grass, water, temp, downfall,
#            precip, dim_type, noise, tree_features, ground_features, ore_color)
# gem_color / sky — hex без решётки
# ---------------------------------------------------------------------------
WORLDS = [
    dict(id="plains_kingdom",   ru="Королевство Равнин",   en="Plains Kingdom",
         gem="adurite",    gem_color="6a8fb5", sky="78a7ff", grass=None, water="3f76e4",
         temp=0.8, down=0.4, precip=True, dim_type="minecraft:overworld", noise="minecraft:overworld",
         trees=["minecraft:trees_plains"], ground=["minecraft:patch_grass_normal", "minecraft:flower_plain"],
         fog=None, has_ceiling=False, amplified=False, water_ore_base="stone"),
    dict(id="desert_realm",     ru="Пустынное Царство",    en="Desert Realm",
         gem="sunstone",   gem_color="f0a13a", sky="6eb1ff", grass=None, water="3f76e4",
         temp=2.0, down=0.0, precip=False, dim_type="minecraft:overworld", noise="minecraft:overworld",
         trees=["minecraft:patch_cactus_desert"], ground=["minecraft:patch_dry_grass_desert", "minecraft:patch_dead_bush", "minecraft:desert_well"],
         fog=None, has_ceiling=False, amplified=False, water_ore_base="sandstone"),
    dict(id="frozen_waste",     ru="Ледяная Пустошь",      en="Frozen Waste",
         gem="glacite",    gem_color="7fd4e8", sky="7fa1ff", grass="80b497", water="3938c9",
         temp=0.0, down=0.5, precip=True, dim_type="minecraft:overworld", noise="minecraft:overworld",
         trees=["minecraft:trees_snowy", "minecraft:spruce_on_snow"], ground=["minecraft:patch_grass_taiga", "minecraft:pile_snow"],
         fog=None, has_ceiling=False, amplified=False, water_ore_base="packed_ice"),
    dict(id="mountain_peaks",   ru="Горные Пики",          en="Mountain Peaks",
         gem="titanite",   gem_color="9a7fb8", sky="82a7ff", grass=None, water="3f76e4",
         temp=0.2, down=0.3, precip=True, dim_type="minecraft:overworld", noise="minecraft:overworld",
         trees=["minecraft:trees_grove"], ground=["minecraft:patch_grass_taiga"],
         fog=None, has_ceiling=False, amplified=True, water_ore_base="stone"),
    dict(id="savanna_expanse",   ru="Простор Саванны",      en="Savanna Expanse",
         gem="amber",      gem_color="ffb830", sky="78a7ff", grass="bfb755", water="3f76e4",
         temp=1.2, down=0.0, precip=False, dim_type="minecraft:overworld", noise="minecraft:overworld",
         trees=["minecraft:trees_savanna"], ground=["minecraft:patch_grass_savanna"],
         fog=None, has_ceiling=False, amplified=False, water_ore_base="terracotta"),
    dict(id="enchanted_forest", ru="Волшебный Лес",        en="Enchanted Forest",
         gem="moonstone",  gem_color="c9d7f2", sky="79a6ff", grass=None, water="3f76e4",
         temp=0.7, down=0.8, precip=True, dim_type="minecraft:overworld", noise="minecraft:overworld",
         trees=["minecraft:trees_birch"], ground=["minecraft:patch_grass_forest", "minecraft:flower_forest_flowers"],
         fog=None, has_ceiling=False, amplified=False, water_ore_base="stone"),
    dict(id="jungle_depths",    ru="Глубины Джунглей",     en="Jungle Depths",
         gem="jade",       gem_color="3fae6a", sky="77a8ff", grass=None, water="3f76e4",
         temp=0.95, down=0.9, precip=True, dim_type="minecraft:overworld", noise="minecraft:overworld",
         trees=["minecraft:trees_jungle"], ground=["minecraft:patch_grass_jungle", "minecraft:flower_warm"],
         fog=None, has_ceiling=False, amplified=False, water_ore_base="stone"),
    dict(id="swamp_marsh",      ru="Кислотная Топь",       en="Swamp Marsh",
         gem="mirestone",  gem_color="8a9a5b", sky="78a7ff", grass="6a7039", water="617b64",
         temp=0.8, down=0.9, precip=True, dim_type="minecraft:overworld", noise="minecraft:overworld",
         trees=["minecraft:trees_swamp"], ground=["minecraft:patch_grass_normal", "minecraft:flower_swamp"],
         fog="232317", has_ceiling=False, amplified=False, water_ore_base="mud"),
    dict(id="volcanic_wastes",  ru="Вулканические Пустоши", en="Volcanic Wastes",
         gem="infernite",  gem_color="e2543a", sky="1c0f14", grass=None, water="9a5b3f",
         temp=2.0, down=0.0, precip=False, dim_type="minecraft:the_nether", noise="minecraft:nether",
         trees=["minecraft:crimson_forest_vegetation", "minecraft:crimson_fungi"], ground=["minecraft:patch_fire", "minecraft:patch_crimson_roots"],
         fog="330808", has_ceiling=True, amplified=False, water_ore_base="blackstone"),
    dict(id="azure_isles",      ru="Лазурные Острова",     en="Azure Isles",
         gem="aquamarine", gem_color="4fd0c7", sky="8cf4ff", grass=None, water="3ff2e0",
         temp=0.7, down=0.4, precip=True, dim_type="minecraft:overworld", noise="minecraft:end",
         trees=["minecraft:trees_cherry"], ground=["minecraft:flower_meadow"],
         fog=None, has_ceiling=False, amplified=False, water_ore_base="stone"),
    dict(id="void_expanse",     ru="Бездна",               en="Void Expanse",
         gem="nullite",    gem_color="5a4a8a", sky="161616", grass=None, water="2a1f3d",
         temp=0.5, down=0.0, precip=False, dim_type="minecraft:the_end", noise="minecraft:end",
         trees=["minecraft:chorus_plant"], ground=[],
         fog="101010", has_ceiling=False, amplified=False, water_ore_base="end_stone"),
    dict(id="mystic_grove",     ru="Мистическая Роща",     en="Mystic Grove",
         gem="shroomite",  gem_color="d95fd0", sky="7ba4ff", grass=None, water="b07bd6",
         temp=0.8, down=0.8, precip=True, dim_type="minecraft:overworld", noise="minecraft:overworld",
         trees=["minecraft:trees_birch"], ground=["minecraft:patch_grass_forest", "minecraft:red_mushroom_normal", "minecraft:brown_mushroom_normal"],
         fog=None, has_ceiling=False, amplified=False, water_ore_base="stone"),
]

# ---------------------------------------------------------------------------
# Мобы. kind: humanoid | beast | beast_hostile | spider | slime | wisp | trader
# ---------------------------------------------------------------------------
MOBS = [
    # --- plains ---
    dict(id="bandit",        ru="Бандит",           en="Bandit",        world="plains_kingdom", kind="humanoid",
         hp=20, dmg=4.0, speed=0.32, w=0.6, h=1.95, hostile=True, skin="leather", accent="8b3a2a",
         sounds="zombie", loot=[("mcadvanced:adurite", 0.35, 1, 1), ("minecraft:emerald", 0.3, 1, 2)]),
    dict(id="bull",          ru="Бык",              en="Bull",          world="plains_kingdom", kind="beast",
         hp=16, dmg=0, speed=0.25, w=0.9, h=1.4, hostile=False, skin="brown", accent="3a2a1a",
         sounds="cow", loot=[("mcadvanced:game_meat", 1.0, 1, 3), ("minecraft:leather", 0.8, 1, 2)]),
    # --- desert ---
    dict(id="mummy",         ru="Мумия",            en="Mummy",         world="desert_realm", kind="humanoid",
         hp=26, dmg=5.0, speed=0.26, w=0.6, h=1.95, hostile=True, skin="linen", accent="c9b896",
         sounds="zombie", loot=[("mcadvanced:sunstone", 0.5, 1, 2), ("minecraft:gold_nugget", 0.6, 1, 3)]),
    dict(id="scorpion",      ru="Скорпион",         en="Scorpion",      world="desert_realm", kind="spider",
         hp=16, dmg=4.0, speed=0.3, w=1.3, h=0.9, hostile=True, skin="sand", accent="a8865a",
         sounds="spider", poison=True, loot=[("mcadvanced:sunstone", 0.4, 1, 1), ("minecraft:string", 0.6, 1, 2)]),
    # --- frozen ---
    dict(id="frost_reaver",  ru="Ледяной жнец",     en="Frost Reaver",  world="frozen_waste", kind="humanoid",
         hp=24, dmg=5.0, speed=0.3, w=0.6, h=1.95, hostile=True, skin="ice", accent="5ab8d4",
         sounds="zombie", loot=[("mcadvanced:glacite", 0.5, 1, 2), ("minecraft:blue_ice", 0.2, 1, 1)]),
    dict(id="frost_stag",    ru="Ледяной олень",    en="Frost Stag",    world="frozen_waste", kind="beast",
         hp=14, dmg=0, speed=0.3, w=0.9, h=1.4, hostile=False, skin="gray", accent="d8d8e8",
         sounds="cow", loot=[("mcadvanced:game_meat", 1.0, 1, 2), ("mcadvanced:glacite", 0.15, 1, 1)]),
    # --- mountains ---
    dict(id="rock_golem",    ru="Каменный голем",   en="Rock Golem",    world="mountain_peaks", kind="humanoid",
         hp=40, dmg=7.0, speed=0.22, w=1.1, h=2.7, hostile=True, skin="stone", accent="7a7a7a",
         sounds="irongolem", loot=[("mcadvanced:titanite", 0.6, 1, 2), ("minecraft:iron_nugget", 0.5, 1, 3)]),
    # --- savanna ---
    dict(id="lion",          ru="Лев",              en="Lion",          world="savanna_expanse", kind="beast_hostile",
         hp=22, dmg=6.0, speed=0.34, w=0.9, h=1.4, hostile=True, skin="gold", accent="c49a3a",
         sounds="cow", loot=[("mcadvanced:game_meat", 1.0, 1, 3), ("mcadvanced:amber", 0.3, 1, 1)]),
    dict(id="zebra",         ru="Зебра",            en="Zebra",         world="savanna_expanse", kind="beast",
         hp=16, dmg=0, speed=0.32, w=0.9, h=1.4, hostile=False, skin="striped", accent="e8e8e8",
         sounds="cow", loot=[("mcadvanced:game_meat", 1.0, 1, 2), ("minecraft:leather", 0.6, 1, 2)]),
    # --- forest ---
    dict(id="treant",        ru="Энт",              en="Treant",        world="enchanted_forest", kind="humanoid",
         hp=34, dmg=6.0, speed=0.2, w=1.0, h=2.4, hostile=True, skin="bark", accent="5a7a3a",
         sounds="zombie", loot=[("mcadvanced:moonstone", 0.5, 1, 2), ("minecraft:oak_log", 0.7, 1, 2)]),
    dict(id="forest_wisp",   ru="Лесной дух",       en="Forest Wisp",   world="enchanted_forest", kind="wisp",
         hp=10, dmg=0, speed=0.6, w=1.0, h=1.0, hostile=False, skin="glow", accent="b8f0c8",
         sounds="ghast", loot=[("mcadvanced:moonstone", 0.5, 1, 2), ("minecraft:glowstone_dust", 0.8, 1, 3)]),
    # --- jungle ---
    dict(id="vine_spider",   ru="Лиановый паук",    en="Vine Spider",   world="jungle_depths", kind="spider",
         hp=18, dmg=4.0, speed=0.32, w=1.3, h=0.9, hostile=True, skin="jungle", accent="3a8a3a",
         sounds="spider", poison=True, loot=[("mcadvanced:jade", 0.4, 1, 1), ("minecraft:string", 0.7, 1, 2)]),
    dict(id="jungle_brute",  ru="Дикарь джунглей",  en="Jungle Brute",  world="jungle_depths", kind="humanoid",
         hp=28, dmg=6.0, speed=0.3, w=0.7, h=1.95, hostile=True, skin="leaf", accent="4a7a2a",
         sounds="zombie", loot=[("mcadvanced:jade", 0.45, 1, 2), ("minecraft:cocoa_beans", 0.4, 1, 3)]),
    # --- swamp ---
    dict(id="bog_witch",     ru="Болотная ведьма",  en="Bog Witch",     world="swamp_marsh", kind="humanoid",
         hp=24, dmg=5.0, speed=0.28, w=0.6, h=1.95, hostile=True, skin="witch", accent="4a5a3a",
         sounds="witch", poison=True, ranged="potion", loot=[("mcadvanced:mirestone", 0.5, 1, 2), ("minecraft:glass_bottle", 0.5, 1, 2)]),
    # --- volcanic ---
    dict(id="magma_beast",   ru="Магмовый зверь",   en="Magma Beast",   world="volcanic_wastes", kind="humanoid",
         hp=32, dmg=7.0, speed=0.28, w=0.8, h=2.1, hostile=True, skin="magma", accent="e2543a",
         sounds="blaze", fire=True, loot=[("mcadvanced:infernite", 0.55, 1, 2), ("minecraft:magma_cream", 0.4, 1, 2)]),
    dict(id="ash_wraith",    ru="Пепельный призрак", en="Ash Wraith",   world="volcanic_wastes", kind="humanoid",
         hp=18, dmg=4.0, speed=0.36, w=0.6, h=1.95, hostile=True, skin="ash", accent="6a6a6a",
         sounds="blaze", loot=[("mcadvanced:infernite", 0.35, 1, 1), ("minecraft:coal", 0.6, 1, 3)]),
    # --- azure ---
    dict(id="pirate_ghost",  ru="Призрак пирата",   en="Pirate Ghost",  world="azure_isles", kind="humanoid",
         hp=20, dmg=4.0, speed=0.3, w=0.6, h=1.95, hostile=True, skin="ghost", accent="7ac8d4",
         sounds="skeleton", ranged="arrow", loot=[("mcadvanced:aquamarine", 0.45, 1, 2), ("minecraft:iron_nugget", 0.4, 1, 2)]),
    dict(id="shore_crawler", ru="Береговой ползун", en="Shore Crawler", world="azure_isles", kind="spider",
         hp=14, dmg=3.0, speed=0.3, w=1.2, h=0.8, hostile=True, skin="coral", accent="e07a5a",
         sounds="spider", loot=[("mcadvanced:aquamarine", 0.35, 1, 1), ("minecraft:prismarine_shard", 0.3, 1, 2)]),
    # --- void ---
    dict(id="void_wraith",   ru="Призрак Бездны",   en="Void Wraith",   world="void_expanse", kind="humanoid",
         hp=28, dmg=6.0, speed=0.34, w=0.6, h=2.2, hostile=True, skin="void", accent="3a2a5a",
         sounds="enderman", loot=[("mcadvanced:nullite", 0.5, 1, 2), ("minecraft:ender_pearl", 0.25, 1, 1)]),
    dict(id="null_crawler",  ru="Пустотный ползун", en="Null Crawler",  world="void_expanse", kind="spider",
         hp=18, dmg=4.0, speed=0.34, w=1.3, h=0.9, hostile=True, skin="void", accent="2a1a4a",
         sounds="spider", loot=[("mcadvanced:nullite", 0.4, 1, 1), ("minecraft:echo_shard", 0.1, 1, 1)]),
    # --- mystic ---
    dict(id="spore_slime",   ru="Споровый слайм",   en="Spore Slime",   world="mystic_grove", kind="slime",
         hp=16, dmg=3.0, speed=0.3, w=1.04, h=1.04, hostile=True, skin="mushroom", accent="d95fd0",
         sounds="slime", loot=[("mcadvanced:shroomite", 0.4, 1, 1), ("minecraft:slime_ball", 0.6, 1, 3)]),
    dict(id="mushroomling",  ru="Гриблинг",         en="Mushroomling",  world="mystic_grove", kind="trader",
         hp=20, dmg=0, speed=0.25, w=0.6, h=1.3, hostile=False, skin="mushroom", accent="d95fd0",
         sounds="villager", trader="mystic",
         loot=[("mcadvanced:shroomite", 0.4, 1, 2), ("minecraft:red_mushroom", 0.8, 1, 3)]),
]

# ---------------------------------------------------------------------------
# Боссы (по одному на мир)
# ---------------------------------------------------------------------------
BOSSES = [
    dict(id="bandit_king",   ru="Король бандитов",    en="Bandit King",     world="plains_kingdom",
         hp=150, dmg=9, speed=0.32, scale=1.25, bar="RED",      skin="leather",  accent="b8323a",
         artifact="kings_blade",   artifact_ru="Клинок Короля",     artifact_en="King's Blade",
         staff=False, sounds="zombie", effect=None),
    dict(id="pharaoh",       ru="Фараон",             en="Pharaoh",         world="desert_realm",
         hp=160, dmg=8, speed=0.3, scale=1.2, bar="YELLOW",    skin="gold",     accent="e8c73a",
         artifact="pharaoh_scepter", artifact_ru="Скипетр Фараона", artifact_en="Pharaoh's Scepter",
         staff=True, sounds="zombie", effect=None),
    dict(id="yeti",          ru="Йети",               en="Yeti",            world="frozen_waste",
         hp=180, dmg=11, speed=0.28, scale=1.5, bar="BLUE",      skin="yeti",     accent="e8f0f8",
         artifact="yeti_frostmaul", artifact_ru="Ледяная дубина Йети", artifact_en="Yeti Frostmaul",
         staff=False, sounds="zombie", effect="slowness"),
    dict(id="stone_titan",   ru="Каменный титан",     en="Stone Titan",     world="mountain_peaks",
         hp=220, dmg=13, speed=0.22, scale=1.8, bar="GREEN",     skin="stone",    accent="5a5a6a",
         artifact="titan_hammer",   artifact_ru="Молот Титана",     artifact_en="Titan Hammer",
         staff=False, sounds="irongolem", effect=None),
    dict(id="sun_predator",  ru="Солнечный хищник",   en="Sun Predator",    world="savanna_expanse",
         hp=150, dmg=10, speed=0.38, scale=1.3, bar="GOLD",      skin="gold",     accent="ff9838",
         artifact="sun_fang",       artifact_ru="Клык Солнца",      artifact_en="Sun Fang",
         staff=False, sounds="cow", effect="fire"),
    dict(id="elder_treant",  ru="Древний энт",        en="Elder Treant",    world="enchanted_forest",
         hp=200, dmg=10, speed=0.2, scale=1.6, bar="GREEN",     skin="bark",     accent="3a6a2a",
         artifact="treant_club",    artifact_ru="Дубина энта",      artifact_en="Treant Club",
         staff=False, sounds="zombie", effect=None),
    dict(id="vine_serpent",  ru="Змей-лиан",          en="Vine Serpent",    world="jungle_depths",
         hp=170, dmg=9, speed=0.32, scale=1.4, bar="GREEN",     skin="leaf",     accent="2a8a4a",
         artifact="serpent_staff",  artifact_ru="Посох Змея",       artifact_en="Serpent Staff",
         staff=True, sounds="spider", effect="poison"),
    dict(id="bog_horror",    ru="Болотный ужас",      en="Bog Horror",      world="swamp_marsh",
         hp=180, dmg=10, speed=0.26, scale=1.5, bar="PURPLE",    skin="witch",    accent="3a4a2a",
         artifact="witchblade",     artifact_ru="Ведьмин клинок",   artifact_en="Witchblade",
         staff=False, sounds="witch", effect="poison"),
    dict(id="inferno_lord",  ru="Повелитель Инферно", en="Inferno Lord",    world="volcanic_wastes",
         hp=230, dmg=13, speed=0.3, scale=1.7, bar="RED",       skin="magma",    accent="ff6a2a",
         artifact="inferno_blade",  artifact_ru="Клинок Инферно",  artifact_en="Inferno Blade",
         staff=False, sounds="blaze", effect="fire"),
    dict(id="storm_king",    ru="Король Шторма",      en="Storm King",      world="azure_isles",
         hp=190, dmg=10, speed=0.34, scale=1.45, bar="BLUE",     skin="ghost",    accent="38c8e8",
         artifact="storm_caller",   artifact_ru="Зов Шторма",       artifact_en="Storm Caller",
         staff=True, sounds="skeleton", effect=None),
    dict(id="void_sovereign", ru="Владыка Бездны",    en="Void Sovereign",  world="void_expanse",
         hp=260, dmg=14, speed=0.36, scale=1.75, bar="PURPLE",   skin="void",     accent="8a5aff",
         artifact="sovereign_edge", artifact_ru="Грань Владыки",    artifact_en="Sovereign Edge",
         staff=False, sounds="enderman", effect="wither"),
    dict(id="spore_mother",  ru="Мать Спор",          en="Spore Mother",    world="mystic_grove",
         hp=170, dmg=9, speed=0.24, scale=1.5, bar="PINK",      skin="mushroom", accent="e87ad8",
         artifact="spore_scepter",  artifact_ru="Скипетр Спор",    artifact_en="Spore Scepter",
         staff=True, sounds="slime", effect="poison"),
]

# ---------------------------------------------------------------------------
# Торговцы-NPC (хаб + миры): торговля кодом (MerchantOffer)
# trades: (cost_item, cost_count) -> (give_item, give_count), maxUses, xp
# ---------------------------------------------------------------------------
TRADERS = [
    dict(id="hunter_trader",    ru="Охотник Диких Миров", en="Wild Worlds Hunter",
         skin="leather", accent="7a5a3a", sounds="villager", spawn_in_hub=True,
         trades=[
             (("minecraft:emerald", 8),  ("mcadvanced:game_meat", 6), 12, 2),
             (("minecraft:emerald", 14), ("mcadvanced:cooked_game_meat", 8), 12, 3),
             (("mcadvanced:adurite", 3), ("minecraft:emerald", 1), 8, 4),
             (("mcadvanced:jade", 3),    ("minecraft:emerald", 1), 8, 4),
             (("minecraft:emerald", 20), ("mcadvanced:amber", 1), 6, 5),
             (("minecraft:emerald", 36), ("mcadvanced:hub_compass", 1), 3, 8),
         ]),
    dict(id="alchemist_trader", ru="Алхимик", en="Alchemist",
         skin="witch", accent="8a3a8a", sounds="villager", spawn_in_hub=True,
         trades=[
             (("minecraft:emerald", 5),  ("minecraft:splash_potion{Potion:\"minecraft:healing\"}", 1), 6, 4),
             (("minecraft:emerald", 6),  ("minecraft:splash_potion{Potion:\"minecraft:strong_poison\"}", 1), 6, 4),
             (("minecraft:emerald", 4),  ("minecraft:potion{Potion:\"minecraft:strong_swiftness\"}", 1), 8, 3),
             (("mcadvanced:frost_berry", 6), ("mcadvanced:mystic_stew", 2), 8, 4),
             (("mcadvanced:shroomite", 2),   ("minecraft:experience_bottle", 3), 8, 6),
         ]),
    dict(id="blacksmith_trader", ru="Кузнец Легенд", en="Legendary Blacksmith",
         skin="iron", accent="9a9a9a", sounds="villager", spawn_in_hub=True,
         trades=[
             (("mcadvanced:adurite", 12),   ("mcadvanced:adurite_sword", 1), 4, 12),
             (("mcadvanced:glacite", 12),   ("mcadvanced:glacite_pickaxe", 1), 4, 12),
             (("mcadvanced:infernite", 14), ("mcadvanced:infernite_axe", 1), 3, 15),
             (("mcadvanced:nullite", 16),   ("mcadvanced:nullite_sword", 1), 3, 18),
             (("mcadvanced:titanite", 18),  ("mcadvanced:crown_of_worlds", 1), 1, 30),
         ]),
]

# ---------------------------------------------------------------------------
# Инструментальные тиры: гемы -> полный набор (кирка/меч/топор/лопата/мотыга + броня)
# material: ванильный ToolMaterial/базовая защита (baseline = алмаз + чуть лучше)
# ---------------------------------------------------------------------------
TIER_SETS = [
    dict(gem="adurite",   ru="адурит",    en="adurite",   tool="DIAMOND", durability_bonus=0,
         defense=[3, 6, 5, 2],  sword_dmg=4.0, color="6a8fb5"),
    dict(gem="glacite",   ru="глацит",    en="glacite",   tool="DIAMOND", durability_bonus=100,
         defense=[3, 6, 5, 2],  sword_dmg=4.5, color="7fd4e8"),
    dict(gem="infernite", ru="инфернит",  en="infernite", tool="NETHERITE", durability_bonus=0,
         defense=[3, 6, 5, 2],  sword_dmg=5.0, color="e2543a"),
    dict(gem="nullite",   ru="нуллит",    en="nullite",   tool="NETHERITE", durability_bonus=200,
         defense=[4, 7, 5, 3],  sword_dmg=5.5, color="5a4a8a"),
]

# ---------------------------------------------------------------------------
# Еда
# ---------------------------------------------------------------------------
FOODS = [
    dict(id="game_meat",         ru="Сырая дичь",       en="Raw Game Meat",    nutrition=3, sat=0.3, cook=True),
    dict(id="cooked_game_meat",  ru="Жареная дичь",     en="Cooked Game Meat", nutrition=8, sat=0.8),
    dict(id="frost_berry",       ru="Морозная ягода",   en="Frost Berry",      nutrition=4, sat=0.6),
    dict(id="mystic_stew",       ru="Мистическое рагу", en="Mystic Stew",      nutrition=10, sat=0.9, always=True, bowl=True),
]

# ---------------------------------------------------------------------------
# ХАБ
# ---------------------------------------------------------------------------
HUB = dict(
    dim="hub", ru="Главная Комната", en="Grand Hub",
    biome="hub", stone="hub_stone", bricks="hub_stone_bricks",
    pillar="hub_pillar", lantern="hub_lantern", altar="boss_altar",
    color="c8a84a",
)

if __name__ == "__main__":
    print(f"worlds={len(WORLDS)} mobs={len(MOBS)} bosses={len(BOSSES)} traders={len(TRADERS)}")
