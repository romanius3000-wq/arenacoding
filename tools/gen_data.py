# -*- coding: utf-8 -*-
"""
Генератор серверных данных: dimensions, biomes, worldgen (руды),
loot tables, recipes, tags, новые торги жителей.
Выход: src/main/resources/data/...
"""
import os, sys, json

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
from manifest import WORLDS, MOBS, BOSSES, TRADERS, TIER_SETS, FOODS, HUB, MOD_ID

DATA = os.path.join(HERE, "..", "src", "main", "resources", "data")

def w(path, obj, ns=MOD_ID):
    p = os.path.join(DATA, ns, path)
    os.makedirs(os.path.dirname(p), exist_ok=True)
    with open(p, "w", encoding="utf-8") as f:
        json.dump(obj, f, ensure_ascii=False, indent=2, sort_keys=True)

# --------------------------------------------------------------------------
# ИЗМЕРЕНИЯ
# --------------------------------------------------------------------------
def gen_dimensions():
    for wl in WORLDS:
        w(f"dimension/{wl['id']}.json", {
            "type": wl["dim_type"],
            "generator": {
                "type": "minecraft:noise",
                "settings": "minecraft:amplified" if wl.get("amplified") else wl["noise"],
                "biome_source": {"type": "minecraft:fixed", "biome": f"{MOD_ID}:{wl['id']}"},
            },
        })
    # Хаб — собственный генератор комнат
    w("dimension/hub.json", {
        "type": "minecraft:overworld",
        "generator": {"type": f"{MOD_ID}:hub_room", "biome": f"{MOD_ID}:hub"},
    })

# --------------------------------------------------------------------------
# БИОМЫ
# --------------------------------------------------------------------------
OVERWORLD_ORES = [
    "minecraft:ore_dirt", "minecraft:ore_gravel", "minecraft:ore_granite_upper", "minecraft:ore_granite_lower",
    "minecraft:ore_diorite_upper", "minecraft:ore_diorite_lower", "minecraft:ore_andesite_upper",
    "minecraft:ore_andesite_lower", "minecraft:ore_tuff", "minecraft:ore_coal_upper", "minecraft:ore_coal_lower",
    "minecraft:ore_iron_upper", "minecraft:ore_iron_middle", "minecraft:ore_iron_small", "minecraft:ore_gold",
    "minecraft:ore_gold_lower", "minecraft:ore_redstone", "minecraft:ore_redstone_lower", "minecraft:ore_diamond",
    "minecraft:ore_diamond_medium", "minecraft:ore_diamond_large", "minecraft:ore_diamond_buried",
    "minecraft:ore_lapis", "minecraft:ore_lapis_buried", "minecraft:ore_copper",
]
NETHER_ORES = [
    "minecraft:ore_magma", "minecraft:ore_gravel_nether", "minecraft:ore_blackstone",
    "minecraft:ore_gold_nether", "minecraft:ore_quartz_nether",
]

def mob_spawner(mid, weight, mn, mx):
    # ванильные мобы уже с неймспейсом, наши — без
    etype = mid if ":" in mid else f"{MOD_ID}:{mid}"
    return {"type": etype, "weight": weight, "minCount": mn, "maxCount": mx}

def gen_biomes():
    for wl in WORLDS:
        monsters, creatures = [], []
        for m in MOBS:
            if m["world"] != wl["id"]:
                continue
            if m["kind"] in ("humanoid", "spider", "slime", "beast_hostile"):
                monsters.append(mob_spawner(m["id"], 95, 1, 3))
            elif m["kind"] in ("beast", "wisp", "trader"):
                creatures.append(mob_spawner(m["id"], 8, 1, 3))
        # немного ванильных монстров для разнообразия
        if not wl.get("has_ceiling") and wl["noise"] != "minecraft:end":
            monsters.append(mob_spawner("minecraft:zombie", 40, 1, 2))
            monsters.append(mob_spawner("minecraft:skeleton", 40, 1, 2))
        monsters.append(mob_spawner("minecraft:enderman", 5, 1, 1))

        is_nether = wl["noise"] == "minecraft:nether"
        is_end = wl["noise"] == "minecraft:end"
        ore_list = (NETHER_ORES if is_nether else OVERWORLD_ORES) + [f"{MOD_ID}:ore_{wl['gem']}"]

        features = [[], [], [], [], [], [], [], [], [], [], []]
        if is_nether:
            features[7] = ore_list + ["minecraft:spring_open", "minecraft:spring_closed"]
            features[9] = ["minecraft:spring_lava"] + wl["ground"]
            features[10] = wl["trees"]
        elif is_end:
            features[6] = ore_list
            features[9] = wl["trees"] + wl["ground"]
        else:
            features[2] = ["minecraft:amethyst_geode"]
            features[3] = ["minecraft:monster_room", "minecraft:monster_room_deep"]
            features[6] = ore_list + ["minecraft:underwater_magma"]
            features[8] = ["minecraft:spring_water", "minecraft:spring_lava"]
            features[9] = ["minecraft:glow_lichen"] + wl["ground"] + wl["trees"] + \
                          ["minecraft:patch_sugar_cane", "minecraft:patch_pumpkin"]
            features[10] = ["minecraft:freeze_top_layer"]

        effects = {"water_color": f"#{wl['water']}"}
        if wl["grass"]:
            effects["grass_color"] = f"#{wl['grass']}"
        attributes = {"minecraft:visual/sky_color": f"#{wl['sky']}"}
        if wl["fog"]:
            attributes["minecraft:visual/water_fog_color"] = f"#{wl['fog']}"

        biome = {
            "temperature": wl["temp"],
            "downfall": wl["down"],
            "has_precipitation": wl["precip"],
            "effects": effects,
            "attributes": attributes,
            "carvers": ["minecraft:nether_cave"] if is_nether else
                       ([] if is_end else ["minecraft:cave", "minecraft:cave_extra_underground", "minecraft:canyon"]),
            "features": features,
            "spawners": {
                "monster": monsters, "creature": creatures, "ambient": [],
                "axolotls": [], "misc": [], "underground_water_creature": [],
                "water_ambient": [], "water_creature": [],
            },
            "spawn_costs": {},
        }
        w(f"worldgen/biome/{wl['id']}.json", biome)
    # биом хаба: пустой, мирный
    w("worldgen/biome/hub.json", {
        "temperature": 0.7, "downfall": 0.0, "has_precipitation": False,
        "effects": {"water_color": "#3f76e4"},
        "attributes": {"minecraft:visual/sky_color": "#1a1030"},
        "carvers": [],
        "features": [[], [], [], [], [], [], [], [], [], [], []],
        "spawners": {"monster": [], "creature": [], "ambient": [], "axolotls": [], "misc": [],
                     "underground_water_creature": [], "water_ambient": [], "water_creature": []},
        "spawn_costs": {},
    })

# --------------------------------------------------------------------------
# РУДЫ (configured + placed features)
# --------------------------------------------------------------------------
def ore_targets(wl):
    is_nether = wl["noise"] == "minecraft:nether"
    is_end = wl["noise"] == "minecraft:end"
    if is_end:
        return [{"state": {"Name": f"{MOD_ID}:{wl['gem']}_ore"},
                 "target": {"predicate_type": "minecraft:block_match", "block": "minecraft:end_stone"}}]
    if is_nether:
        return [{"state": {"Name": f"{MOD_ID}:{wl['gem']}_ore"},
                 "target": {"predicate_type": "minecraft:tag_match", "tag": "minecraft:base_stone_nether"}}]
    return [
        {"state": {"Name": f"{MOD_ID}:{wl['gem']}_ore"},
         "target": {"predicate_type": "minecraft:tag_match", "tag": "minecraft:stone_ore_replaceables"}},
        {"state": {"Name": f"{MOD_ID}:{wl['gem']}_ore"},
         "target": {"predicate_type": "minecraft:tag_match", "tag": "minecraft:deepslate_ore_replaceables"}},
    ]

def gen_ores():
    for wl in WORLDS:
        w(f"worldgen/configured_feature/ore_{wl['gem']}.json", {
            "type": "minecraft:ore",
            "config": {"size": 7, "discard_chance_on_air_exposure": 0.0, "targets": ore_targets(wl)},
        })
        w(f"worldgen/placed_feature/ore_{wl['gem']}.json", {
            "feature": f"{MOD_ID}:ore_{wl['gem']}",
            "placement": [
                {"type": "minecraft:count", "count": 9},
                {"type": "minecraft:in_square"},
                {"type": "minecraft:height_range", "height": {
                    "type": "minecraft:uniform",
                    "min_inclusive": {"above_bottom": 0},
                    "max_inclusive": {"absolute": 110}}},
                {"type": "minecraft:biome"},
            ],
        })

# --------------------------------------------------------------------------
# ЛУТ
# --------------------------------------------------------------------------
def ore_loot(gem):
    return {
        "type": "minecraft:block",
        "pools": [{
            "rolls": 1.0,
            "entries": [{
                "type": "minecraft:alternatives",
                "children": [
                    {"type": "minecraft:item",
                     "conditions": [{"condition": "minecraft:match_tool", "predicate": {"predicates": {"minecraft:enchantments": [{"enchantments": "minecraft:silk_touch", "levels": {"min": 1}}]}}}],
                     "name": f"{MOD_ID}:{gem}_ore"},
                    {"type": "minecraft:item",
                     "functions": [{"enchantment": "minecraft:fortune", "formula": "minecraft:ore_drops", "function": "minecraft:apply_bonus"},
                                   {"function": "minecraft:explosion_decay"}],
                     "name": f"{MOD_ID}:{gem}"},
                ],
            }],
        }],
        "random_sequence": f"{MOD_ID}:blocks/{gem}_ore",
    }

def self_drop(block_id):
    return {
        "type": "minecraft:block",
        "pools": [{"rolls": 1.0, "entries": [
            {"type": "minecraft:item",
             "conditions": [{"condition": "minecraft:survives_explosion"}],
             "name": f"{MOD_ID}:{block_id}"}]}],
        "random_sequence": f"{MOD_ID}:blocks/{block_id}",
    }

def entity_loot(drops):
    pools = []
    for (item, chance, mn, mx) in drops:
        pool = {"rolls": 1.0, "entries": [{
            "type": "minecraft:item",
            "name": item,
            "functions": [{"function": "minecraft:set_count", "count": {"min": mn, "max": mx}}],
            "conditions": [{"condition": "minecraft:random_chance", "chance": chance}],
        }]}
        pools.append(pool)
    return {"type": "minecraft:entity", "pools": pools}

def gen_loot():
    for wl in WORLDS:
        w(f"loot_table/blocks/{wl['gem']}_ore.json", ore_loot(wl["gem"]))
        w(f"loot_table/blocks/{wl['gem']}_block.json", self_drop(f"{wl['gem']}_block"))
        w(f"loot_table/blocks/portal_{wl['id']}.json", self_drop(f"portal_{wl['id']}"))
    for b in (HUB["stone"], HUB["bricks"], HUB["pillar"], HUB["lantern"], HUB["altar"]):
        w(f"loot_table/blocks/{b}.json", self_drop(b))
    for m in MOBS:
        w(f"loot_table/entities/{m['id']}.json", entity_loot(m["loot"]))
    for b in BOSSES:
        wl = next(x for x in WORLDS if x["id"] == b["world"])
        drops = [
            (f"{MOD_ID}:{b['artifact']}", 1.0, 1, 1),
            (f"{MOD_ID}:{wl['gem']}_soul", 1.0, 1, 1),
            (f"{MOD_ID}:{wl['gem']}", 1.0, 2, 5),
            ("minecraft:emerald", 0.5, 1, 3),
        ]
        w(f"loot_table/entities/{b['id']}.json", entity_loot(drops))
    for t in TRADERS:
        w(f"loot_table/entities/{t['id']}.json", entity_loot([("minecraft:emerald", 0.3, 1, 2)]))

# --------------------------------------------------------------------------
# РЕЦЕПТЫ
# --------------------------------------------------------------------------
def shaped(name, pattern, key, category="misc"):
    w(f"recipe/{name}.json", {"type": "minecraft:crafting_shaped", "category": category,
                              "pattern": pattern, "key": key,
                              "result": {"id": f"{MOD_ID}:{name}", "count": 1}})

def gen_recipes():
    # инструменты
    for t in TIER_SETS:
        G, S = f"{MOD_ID}:{t['gem']}", "minecraft:stick"
        shaped(f"{t['gem']}_sword", [" G ", " G ", " S "], {"G": G, "S": S}, "equipment")
        shaped(f"{t['gem']}_pickaxe", ["GGG", " S ", " S "], {"G": G, "S": S}, "equipment")
        shaped(f"{t['gem']}_axe", ["GG ", "GS ", " S "], {"G": G, "S": S}, "equipment")
        shaped(f"{t['gem']}_shovel", [" G ", " S ", " S "], {"G": G, "S": S}, "equipment")
        shaped(f"{t['gem']}_hoe", ["GG ", " S ", " S "], {"G": G, "S": S}, "equipment")
        # броня
        shaped(f"{t['gem']}_helmet", ["GGG", "G G"], {"G": G}, "equipment")
        shaped(f"{t['gem']}_chestplate", ["G G", "GGG", "GGG"], {"G": G}, "equipment")
        shaped(f"{t['gem']}_leggings", ["GGG", "G G", "G G"], {"G": G}, "equipment")
        shaped(f"{t['gem']}_boots", ["G G", "G G"], {"G": G}, "equipment")
        # блок <-> гемы
        shaped(f"{t['gem']}_block", ["GGG", "GGG", "GGG"], {"G": G}, "building")
        w(f"recipe/{t['gem']}_from_block.json", {
            "type": "minecraft:crafting_shapeless", "category": "misc",
            "ingredients": [f"{MOD_ID}:{t['gem']}_block"],
            "result": {"id": f"{MOD_ID}:{t['gem']}", "count": 9}})
    # корона миров: 8 душ вокруг незеритового слитка
    souls = [f"{MOD_ID}:{x['gem']}_soul" for x in WORLDS[:8]]
    w("recipe/crown_of_worlds.json", {
        "type": "minecraft:crafting_shaped", "category": "equipment",
        "pattern": ["SNS", "SNS", "SNS"],
        "key": {"S": souls[0], "N": "minecraft:netherite_ingot"},
        "result": {"id": f"{MOD_ID}:crown_of_worlds", "count": 1}})
    # саммоны боссов: 4 гема + 4 палки + кость
    for wl in WORLDS:
        boss = next(b for b in BOSSES if b["world"] == wl["id"])
        w(f"recipe/summon_{boss['id']}.json", {
            "type": "minecraft:crafting_shaped", "category": "misc",
            "pattern": ["GSG", "SBS", "GSG"],
            "key": {"G": f"{MOD_ID}:{wl['gem']}", "S": "minecraft:stick", "B": "minecraft:bone"},
            "result": {"id": f"{MOD_ID}:summon_{boss['id']}", "count": 1}})
    # утилита
    w("recipe/hub_compass.json", {
        "type": "minecraft:crafting_shapeless", "category": "misc",
        "ingredients": ["minecraft:compass", "minecraft:diamond", "minecraft:gold_ingot", "minecraft:gold_ingot"],
        "result": {"id": f"{MOD_ID}:hub_compass", "count": 1}})
    w("recipe/world_selector.json", {
        "type": "minecraft:crafting_shapeless", "category": "misc",
        "ingredients": ["minecraft:paper", "minecraft:paper", "minecraft:emerald", "minecraft:amethyst_shard"],
        "result": {"id": f"{MOD_ID}:world_selector", "count": 1}})
    w(f"recipe/{HUB['altar']}.json", {
        "type": "minecraft:crafting_shaped", "category": "building",
        "pattern": ["SSS", "SDS", "SSS"],
        "key": {"S": "minecraft:stone_bricks", "D": "minecraft:diamond"},
        "result": {"id": f"{MOD_ID}:boss_altar", "count": 1}})
    # еда
    w("recipe/mystic_stew.json", {
        "type": "minecraft:crafting_shapeless", "category": "misc",
        "ingredients": ["minecraft:bowl", "minecraft:red_mushroom", "minecraft:brown_mushroom", f"{MOD_ID}:frost_berry"],
        "result": {"id": f"{MOD_ID}:mystic_stew", "count": 1}})
    for typ, time in (("smelting", 200), ("smoking", 100), ("campfire_cooking", 600)):
        w(f"recipe/cooked_game_meat_{typ}.json", {
            "type": f"minecraft:{typ}", "category": "food", "cookingtime": time,
            "ingredient": f"{MOD_ID}:game_meat",
            "result": {"id": f"{MOD_ID}:cooked_game_meat"}})

# --------------------------------------------------------------------------
# ТЕГИ
# --------------------------------------------------------------------------
def gen_tags():
    # mineable/pickaxe — все наши блоки (теги мержатся между паками)
    blocks = []
    for wl in WORLDS:
        blocks += [f"{MOD_ID}:{wl['gem']}_ore", f"{MOD_ID}:{wl['gem']}_block"]
    blocks += [f"{MOD_ID}:{b}" for b in (HUB["stone"], HUB["bricks"], HUB["pillar"], HUB["lantern"], HUB["altar"])]
    w("tags/block/mineable/pickaxe.json", {"values": blocks}, ns="minecraft")
    # нужны железные+ инструменты для руд
    w("tags/block/needs_iron_tool.json", {
        "values": [f"{MOD_ID}:{wl['gem']}_ore" for wl in WORLDS]}, ns="minecraft")
    # теги починки инструментов/брони
    for wl in WORLDS:
        w(f"tags/item/{wl['gem']}_repair.json", {"values": [f"{MOD_ID}:{wl['gem']}"]})
    w("tags/item/crown_repair.json", {
        "values": [f"{MOD_ID}:{wl['gem']}_soul" for wl in WORLDS]})

# --------------------------------------------------------------------------
# НОВЫЕ ТОРГИ ЖИТЕЛЕЙ (через теги, мержатся с ванилью)
# --------------------------------------------------------------------------
def trade(cost_item, cost_count, give_item, give_count=1, max_uses=12, xp=5):
    return {
        "wants": {"id": cost_item, "count": float(cost_count)},
        "gives": {"id": give_item, "count": float(give_count)},
        "max_uses": float(max_uses),
        "xp": float(xp),
        "reputation_discount": 0.05,
    }

def gen_villager_trades():
    # (profession, level, cost, count, give, count, uses, xp)
    new_trades = [
        ("farmer", 1, "minecraft:emerald", 1, f"{MOD_ID}:frost_berry", 4, 16, 2),
        ("farmer", 3, "minecraft:emerald", 3, f"{MOD_ID}:mystic_stew", 1, 8, 10),
        ("farmer", 4, f"{MOD_ID}:game_meat", 12, "minecraft:emerald", 1, 12, 20),
        ("armorer", 2, "minecraft:emerald", 9, f"{MOD_ID}:adurite_chestplate", 1, 4, 10),
        ("armorer", 4, f"{MOD_ID}:nullite", 4, "minecraft:emerald", 1, 6, 25),
        ("weaponsmith", 3, "minecraft:emerald", 18, f"{MOD_ID}:glacite_sword", 1, 4, 15),
        ("weaponsmith", 4, "minecraft:emerald", 24, f"{MOD_ID}:nullite_sword", 1, 3, 25),
        ("toolsmith", 3, "minecraft:emerald", 14, f"{MOD_ID}:glacite_pickaxe", 1, 4, 15),
        ("toolsmith", 4, "minecraft:emerald", 20, f"{MOD_ID}:infernite_axe", 1, 3, 25),
        ("cleric", 3, f"{MOD_ID}:moonstone", 2, "minecraft:ender_pearl", 1, 6, 15),
        ("cleric", 4, "minecraft:emerald", 16, f"{MOD_ID}:shroomite", 2, 6, 25),
        ("librarian", 4, "minecraft:emerald", 20, f"{MOD_ID}:world_selector", 1, 3, 25),
        ("butcher", 2, f"{MOD_ID}:game_meat", 8, "minecraft:emerald", 1, 12, 10),
        ("mason", 3, "minecraft:emerald", 6, f"{MOD_ID}:hub_stone_bricks", 8, 12, 15),
        ("shepherd", 2, "minecraft:emerald", 5, f"{MOD_ID}:amber", 1, 8, 10),
        ("leatherworker", 3, "minecraft:emerald", 7, f"{MOD_ID}:game_meat", 6, 10, 15),
        ("fletcher", 2, f"{MOD_ID}:jade", 2, "minecraft:emerald", 1, 8, 10),
        ("fisherman", 3, f"{MOD_ID}:aquamarine", 2, "minecraft:emerald", 1, 8, 15),
    ]
    by_tag = {}
    for i, (prof, level, ci, cc, gi, gc, mu, xp) in enumerate(new_trades):
        tid = f"mcadv_{prof}_{level}_{i}"
        w(f"villager_trade/{prof}/{level}/{tid}.json", trade(ci, cc, gi, gc, mu, xp))
        by_tag.setdefault((prof, level), []).append(f"{MOD_ID}:{prof}/{level}/{tid}")
    for (prof, level), values in by_tag.items():
        # тег уровня профессии: наши записи дописываются к ванильным
        w(f"tags/villager_trade/{prof}/level_{level}.json", {"values": values}, ns="minecraft")

def main():
    gen_dimensions()
    gen_biomes()
    gen_ores()
    gen_loot()
    gen_recipes()
    gen_tags()
    gen_villager_trades()
    print("Датапак готов")

if __name__ == "__main__":
    main()
