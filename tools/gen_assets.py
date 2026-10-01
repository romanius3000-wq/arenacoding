# -*- coding: utf-8 -*-
"""
Генератор клиентских ассетов: blockstates, модели, item definitions,
equipment, локализации (en_us + ru_ru).
Выход: src/main/resources/assets/mcadvanced/...
"""
import os, sys, json, io, zipfile, colorsys
from PIL import Image

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
from manifest import WORLDS, MOBS, BOSSES, TRADERS, TIER_SETS, FOODS, HUB, MOD_ID

ASSETS = os.path.join(HERE, "..", "src", "main", "resources", "assets", MOD_ID)
CLIENT_JAR = "/tmp/gradle/caches/fabric-loom/26.2/minecraft-client.jar"

def w(rel, obj):
    p = os.path.join(ASSETS, rel)
    os.makedirs(os.path.dirname(p), exist_ok=True)
    with open(p, "w", encoding="utf-8") as f:
        json.dump(obj, f, ensure_ascii=False, indent=2, sort_keys=True)

def hx(c):
    c = c.lstrip('#')
    return tuple(int(c[i:i+2], 16) for i in (0, 2, 4))

# --------------------------------------------------------------------------
def gen_blockstates():
    blocks = []
    for wl in WORLDS:
        blocks += [f"{wl['gem']}_ore", f"{wl['gem']}_block", f"portal_{wl['id']}"]
    blocks += [HUB["stone"], HUB["bricks"], HUB["pillar"], HUB["lantern"], HUB["altar"]]
    for b in blocks:
        model = f"{MOD_ID}:block/{b}"
        if b == HUB["altar"]:
            w(f"blockstates/{b}.json", {"variants": {"": {"model": model}}})
            w(f"models/block/{b}.json", {
                "parent": "minecraft:block/cube_column",
                "textures": {"end": f"{MOD_ID}:block/boss_altar_top", "side": f"{MOD_ID}:block/boss_altar"}})
        else:
            w(f"blockstates/{b}.json", {"variants": {"": {"model": model}}})
            w(f"models/block/{b}.json", {
                "parent": "minecraft:block/cube_all",
                "textures": {"all": f"{MOD_ID}:block/{b}"}})
    return blocks

def item_def(item_id, model=None, parent=None):
    """assets/<ns>/items/<id>.json + models/item/<id>.json"""
    model = model or f"{MOD_ID}:item/{item_id}"
    w(f"items/{item_id}.json", {"model": {"type": "minecraft:model", "model": model}})
    if parent:
        w(f"models/item/{item_id}.json", {"parent": parent, "textures": {"layer0": f"{MOD_ID}:item/{item_id}"}})

def gen_items(blocks):
    handheld = "minecraft:item/handheld"
    generated = "minecraft:item/generated"
    # блочные предметы — модель блока напрямую
    for b in blocks:
        w(f"items/{b}.json", {"model": {"type": "minecraft:model", "model": f"{MOD_ID}:block/{b}"}})
    # гемы, души, саммоны, еда, утилита, яйца
    simple = []
    for wl in WORLDS:
        simple += [wl["gem"], f"{wl['gem']}_soul"]
    for b in BOSSES:
        simple.append(f"summon_{b['id']}")
    for f in FOODS:
        simple.append(f["id"])
    simple += ["hub_compass", "world_selector", "crown_of_worlds"]
    for s in simple:
        item_def(s, parent=generated)
    # инструменты
    for t in TIER_SETS:
        for shape in ("sword", "pickaxe", "axe", "shovel", "hoe"):
            item_def(f"{t['gem']}_{shape}", parent=handheld)
        for piece in ("helmet", "chestplate", "leggings", "boots"):
            item_def(f"{t['gem']}_{piece}", parent=generated)
    # артефакты
    for b in BOSSES:
        item_def(b["artifact"], parent=handheld if not b["staff"] else handheld)
    # яйца спавна
    eggs = [f"{m['id']}_spawn_egg" for m in MOBS] + \
           [f"{b['id']}_spawn_egg" for b in BOSSES] + \
           [f"{t['id']}_spawn_egg" for t in TRADERS]
    for e in eggs:
        item_def(e, parent=generated)

def gen_equipment():
    """equipment JSON + перекраска ванильной алмазной брони в цвет тира"""
    with zipfile.ZipFile(CLIENT_JAR) as z:
        base_h = Image.open(io.BytesIO(z.read("assets/minecraft/textures/entity/equipment/humanoid/diamond.png"))).convert("RGBA")
        base_l = Image.open(io.BytesIO(z.read("assets/minecraft/textures/entity/equipment/humanoid_leggings/diamond.png"))).convert("RGBA")
    def recolor(img, color):
        out = img.copy()
        t = colorsys.rgb_to_hsv(*[v/255 for v in hx(color)])
        px = out.load()
        for y in range(img.size[1]):
            for x in range(img.size[0]):
                r, g, b, a = px[x, y]
                if a == 0: continue
                h, s, v = colorsys.rgb_to_hsv(r/255, g/255, b/255)
                nr, ng, nb = colorsys.hsv_to_rgb(t[0], t[1]*(0.4+0.6*s), v)
                px[x, y] = (int(nr*255), int(ng*255), int(nb*255), a)
        return out
    for t in TIER_SETS:
        w(f"equipment/{t['gem']}.json", {"layers": {
            "humanoid": [{"texture": f"{MOD_ID}:{t['gem']}"}],
            "humanoid_leggings": [{"texture": f"{MOD_ID}:{t['gem']}"}]}})
        for sub, base in [("humanoid", base_h), ("humanoid_leggings", base_l)]:
            p = os.path.join(ASSETS, "textures", "entity", "equipment", sub, f"{t['gem']}.png")
            os.makedirs(os.path.dirname(p), exist_ok=True)
            recolor(base, t["color"]).save(p)

def gen_lang():
    en, ru = {}, {}
    # блоки
    for wl in WORLDS:
        gem_ru = {"adurite":"Адуритовая","sunstone":"Солнечная","glacite":"Глацитовая","titanite":"Титанитовая",
                  "amber":"Янтарная","moonstone":"Лунная","jade":"Нефритовая","mirestone":"Трясинная",
                  "infernite":"Инфернитовая","aquamarine":"Аквамариновая","nullite":"Нуллитовая","shroomite":"Шроумитовая"}[wl["gem"]]
        en[f"block.{MOD_ID}.{wl['gem']}_ore"] = f"{wl['en']} Ore ({wl['gem']})"
        ru[f"block.{MOD_ID}.{wl['gem']}_ore"] = f"{gem_ru} руда"
        en[f"block.{MOD_ID}.{wl['gem']}_block"] = f"Block of {wl['gem']}"
        ru[f"block.{MOD_ID}.{wl['gem']}_block"] = f"{gem_ru} блок"
        en[f"block.{MOD_ID}.portal_{wl['id']}"] = f"Portal: {wl['en']}"
        ru[f"block.{MOD_ID}.portal_{wl['id']}"] = f"Портал: {wl['ru']}"
    hub_names = {"hub_stone": ("Hub Stone", "Камень хаба"), "hub_stone_bricks": ("Hub Stone Bricks", "Кирпичи хаба"),
                 "hub_pillar": ("Hub Pillar", "Колонна хаба"), "hub_lantern": ("Hub Lantern", "Фонарь хаба"),
                 "boss_altar": ("Boss Altar", "Алтарь боссов")}
    for k, (e, r) in hub_names.items():
        en[f"block.{MOD_ID}.{k}"] = e; ru[f"block.{MOD_ID}.{k}"] = r
    # предметы
    gem_ru_map = {"adurite":"Адурит","sunstone":"Солнечный камень","glacite":"Глацит","titanite":"Титанит",
                  "amber":"Янтарь","moonstone":"Лунный камень","jade":"Нефрит","mirestone":"Трясинник",
                  "infernite":"Инфернит","aquamarine":"Аквамарин","nullite":"Нуллит","shroomite":"Шроумит"}
    for wl in WORLDS:
        gr = gem_ru_map[wl["gem"]]
        en[f"item.{MOD_ID}.{wl['gem']}"] = wl["gem"].capitalize()
        ru[f"item.{MOD_ID}.{wl['gem']}"] = gr
        en[f"item.{MOD_ID}.{wl['gem']}_soul"] = f"Soul of {wl['en']}"
        ru[f"item.{MOD_ID}.{wl['gem']}_soul"] = f"Душа: {wl['ru']}"
        en[f"item.{MOD_ID}.{wl['gem']}_soul.tooltip"] = "Right-click to absorb (+2 max health)"
        ru[f"item.{MOD_ID}.{wl['gem']}_soul.tooltip"] = "ПКМ — поглотить (+2 к макс. здоровью)"
    for t in TIER_SETS:
        gr = gem_ru_map[t["gem"]]
        for shape, ru_n, en_n in [("sword", "меч", "Sword"), ("pickaxe", "кирка", "Pickaxe"),
                                   ("axe", "топор", "Axe"), ("shovel", "лопата", "Shovel"), ("hoe", "мотыга", "Hoe")]:
            en[f"item.{MOD_ID}.{t['gem']}_{shape}"] = f"{t['en'].capitalize()} {en_n}"
            ru[f"item.{MOD_ID}.{t['gem']}_{shape}"] = f"{gr.capitalize()} {ru_n}" if ru_n != "меч" else f"{gr.capitalize()} {ru_n}"
        for piece, ru_n, en_n in [("helmet", "шлем", "Helmet"), ("chestplate", "нагрудник", "Chestplate"),
                                   ("leggings", "штаны", "Leggings"), ("boots", "ботинки", "Boots")]:
            en[f"item.{MOD_ID}.{t['gem']}_{piece}"] = f"{t['en'].capitalize()} {en_n}"
            ru[f"item.{MOD_ID}.{t['gem']}_{piece}"] = f"{gr.capitalize()} {ru_n}"
    for f in FOODS:
        en[f"item.{MOD_ID}.{f['id']}"] = f["en"]; ru[f"item.{MOD_ID}.{f['id']}"] = f["ru"]
    for b in BOSSES:
        en[f"item.{MOD_ID}.{b['artifact']}"] = b["artifact_en"]
        ru[f"item.{MOD_ID}.{b['artifact']}"] = b["artifact_ru"]
        en[f"item.{MOD_ID}.summon_{b['id']}"] = f"Summon: {b['en']}"
        ru[f"item.{MOD_ID}.summon_{b['id']}"] = f"Призыв: {b['ru']}"
    en[f"item.{MOD_ID}.hub_compass"] = "Hub Compass"; ru[f"item.{MOD_ID}.hub_compass"] = "Компас хаба"
    en[f"item.{MOD_ID}.hub_compass.tooltip"] = "Right-click to return to the Grand Hub"
    ru[f"item.{MOD_ID}.hub_compass.tooltip"] = "ПКМ — вернуться в Главную Комнату"
    en[f"item.{MOD_ID}.world_selector"] = "World Selector"; ru[f"item.{MOD_ID}.world_selector"] = "Выбор мира"
    en[f"item.{MOD_ID}.crown_of_worlds"] = "Crown of Worlds"; ru[f"item.{MOD_ID}.crown_of_worlds"] = "Корона Миров"
    # яйца
    for m in MOBS:
        en[f"item.{MOD_ID}.{m['id']}_spawn_egg"] = f"{m['en']} Spawn Egg"
        ru[f"item.{MOD_ID}.{m['id']}_spawn_egg"] = f"Яйцо призыва: {m['ru']}"
    for b in BOSSES:
        en[f"item.{MOD_ID}.{b['id']}_spawn_egg"] = f"{b['en']} Spawn Egg"
        ru[f"item.{MOD_ID}.{b['id']}_spawn_egg"] = f"Яйцо призыва: {b['ru']}"
    for t in TRADERS:
        en[f"item.{MOD_ID}.{t['id']}_spawn_egg"] = f"{t['en']} Spawn Egg"
        ru[f"item.{MOD_ID}.{t['id']}_spawn_egg"] = f"Яйцо призыва: {t['ru']}"
    # сущности
    for m in MOBS:
        en[f"entity.{MOD_ID}.{m['id']}"] = m["en"]; ru[f"entity.{MOD_ID}.{m['id']}"] = m["ru"]
    for b in BOSSES:
        en[f"entity.{MOD_ID}.{b['id']}"] = b["en"]; ru[f"entity.{MOD_ID}.{b['id']}"] = b["ru"]
    for t in TRADERS:
        en[f"entity.{MOD_ID}.{t['id']}"] = t["en"]; ru[f"entity.{MOD_ID}.{t['id']}"] = t["ru"]
    # биомы
    for wl in WORLDS:
        en[f"biome.{MOD_ID}.{wl['id']}"] = wl["en"]; ru[f"biome.{MOD_ID}.{wl['id']}"] = wl["ru"]
    en[f"biome.{MOD_ID}.hub"] = HUB["en"]; ru[f"biome.{MOD_ID}.hub"] = HUB["ru"]
    # GUI / кейбинды / сообщения
    gui = {
        "gui.mcadvanced.selector.title": ("World Selection", "Выбор мира"),
        "gui.mcadvanced.selector.hint": ("Choose your destination, adventurer!", "Выбери, куда отправиться, искатель приключений!"),
        "gui.mcadvanced.selector.hub": ("Grand Hub", "Главная Комната"),
        "gui.mcadvanced.selector.close": ("Close", "Закрыть"),
        "key.category.mcadvanced.abilities": ("Minecraft Advanced", "Minecraft Advanced"),
        "key.mcadvanced.open_selector": ("Open World Selector", "Открыть выбор мира"),
        "key.mcadvanced.dash": ("Dash", "Рывок"),
        "key.mcadvanced.roll": ("Combat Roll", "Боевой перекат"),
        "message.mcadvanced.welcome": ("Welcome to Minecraft Advanced! You received a World Selector (K) and a Hub Compass.", "Добро пожаловать в Minecraft Advanced! Вы получили Выбор мира (K) и Компас хаба."),
        "message.mcadvanced.teleported": ("Travelling to %s ...", "Путешествие в %s ..."),
        "message.mcadvanced.hub": ("Returning to the Grand Hub...", "Возвращение в Главную Комнату..."),
        "message.mcadvanced.soul": ("The soul strengthens you! (+2 max health)", "Душа укрепляет вас! (+2 к макс. здоровью)"),
        "message.mcadvanced.boss_summoned": ("A great power awakens...", "Великая сила пробуждается..."),
        "message.mcadvanced.altar_needed": ("Use this on a Boss Altar!", "Используйте это на Алтаре боссов!"),
        "message.mcadvanced.cooldown": ("Wait a moment...", "Подождите немного..."),
        "message.mcadvanced.dash": ("*Dash!*", "*Рывок!*"),
        "message.mcadvanced.roll": ("*Roll!* (brief invulnerability)", "*Перекат!* (краткая неуязвимость)"),
        "message.mcadvanced.ability_cooldown": ("Ability ready in %s s", "Способность готова через %s с"),
    }
    for k, (e, r) in gui.items():
        en[k] = e; ru[k] = r
    # переводы тегов (чтобы не было dev-warning)
    for wl in WORLDS:
        gem_name = wl["gem"].replace("_", " ").title()
        en[f"tag.item.{MOD_ID}.{wl['gem']}_repair"] = f"{gem_name} repair items"
        ru[f"tag.item.{MOD_ID}.{wl['gem']}_repair"] = f"Починка: {gem_name}"
    en[f"tag.item.{MOD_ID}.crown_repair"] = "Crown of Worlds repair items"
    ru[f"tag.item.{MOD_ID}.crown_repair"] = "Починка короны миров"
    w("lang/en_us.json", en)
    w("lang/ru_ru.json", ru)
    print(f"lang: en={len(en)} ru={len(ru)} ключей")

def main():
    blocks = gen_blockstates()
    gen_items(blocks)
    gen_equipment()
    gen_lang()
    print(f"Ассеты готовы: {len(blocks)} блоков")

if __name__ == "__main__":
    main()
