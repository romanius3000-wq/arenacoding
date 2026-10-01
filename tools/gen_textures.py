# -*- coding: utf-8 -*-
"""
Генератор всех текстур Minecraft Advanced.
Выход: src/main/resources/assets/mcadvanced/textures/...
Запуск: python3 tools/gen_textures.py
"""
import os, sys, math, random, colorsys, zipfile
from PIL import Image, ImageDraw

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
from manifest import WORLDS, MOBS, BOSSES, TRADERS, TIER_SETS, FOODS, HUB, MOD_ID

ROOT = os.path.join(HERE, "..", "src", "main", "resources", "assets", MOD_ID, "textures")
CLIENT_JAR = "/tmp/gradle/caches/fabric-loom/26.2/minecraft-client.jar"

def out(rel):
    p = os.path.join(ROOT, rel)
    os.makedirs(os.path.dirname(p), exist_ok=True)
    return p

def hx(c):
    c = c.lstrip('#')
    return tuple(int(c[i:i+2], 16) for i in (0, 2, 4))

def rgb(r, g, b): return (r, g, b, 255)

def shift(c, f):
    """осветление/затемнение (f>1 светлее)"""
    r, g, b = c[:3]
    if f >= 1:
        return (min(255, int(r*f)), min(255, int(g*f)), min(255, int(b*f)), 255)
    return (int(r*f), int(g*f), int(b*f), 255)

def jitter(c, rng, a=10):
    d = rng.randint(-a, a)
    return (max(0, min(255, c[0]+d)), max(0, min(255, c[1]+d)), max(0, min(255, c[2]+d)), 255)

def noise_fill(img, rect, base, rng, amp=10, alpha=255):
    x0, y0, w, h = rect
    for y in range(y0, y0+h):
        for x in range(x0, x0+w):
            px = jitter(base, rng, amp)
            if alpha < 255:
                img.putpixel((x, y), (px[0], px[1], px[2], alpha))
            else:
                img.putpixel((x, y), px)

def border(img, rect, color, t=1):
    x0, y0, w, h = rect
    d = ImageDraw.Draw(img)
    for i in range(t):
        d.rectangle([x0+i, y0+i, x0+w-1-i, y0+h-1-i], outline=color)

# --------------------------------------------------------------------------
# Ванильные текстуры-референсы (из клиентского джарника)
# --------------------------------------------------------------------------
VAN = {}
def van_path(name): return f"assets/minecraft/textures/{name}"
def load_vanilla():
    need = {
        "stone": van_path("block/stone.png"),
        "sandstone_top": van_path("block/sandstone_top.png"),
        "packed_ice": van_path("block/packed_ice.png"),
        "terracotta": van_path("block/terracotta.png"),
        "mud": van_path("block/mud.png"),
        "blackstone": van_path("block/blackstone.png"),
        "end_stone": van_path("block/end_stone.png"),
        "spider": van_path("entity/spider/spider.png"),
        "slime": van_path("entity/slime/slime.png"),
        "ghast": van_path("entity/ghast/ghast.png"),
    }
    with zipfile.ZipFile(CLIENT_JAR) as z:
        for k, p in need.items():
            try:
                data = z.read(p)
                import io
                VAN[k] = Image.open(io.BytesIO(data)).convert("RGBA")
            except KeyError:
                print(f"  ! нет ванильной текстуры {p}")

# --------------------------------------------------------------------------
# БЛОКИ
# --------------------------------------------------------------------------
BASE_BY_ORE = {
    "stone": "stone", "sandstone": "sandstone_top", "packed_ice": "packed_ice",
    "terracotta": "terracotta", "mud": "mud", "blackstone": "blackstone",
    "end_stone": "end_stone",
}

def gen_ore(w):
    rng = random.Random(f"ore-{w['gem']}")
    base = VAN[BASE_BY_ORE[w["water_ore_base"]]].copy()
    gem = hx(w["gem_color"])
    # кластеры самоцвета (как алмазная руда)
    spots = []
    for cx, cy in [(3,3),(11,2),(6,8),(2,11),(12,10),(8,13)]:
        r = rng.choice([2,2,3])
        for dy in range(-r, r+1):
            for dx in range(-r, r+1):
                if dx*dx+dy*dy <= r*r and rng.random() < 0.75:
                    spots.append((cx+dx, cy+dy))
    for (x, y) in spots:
        if 0 <= x < 16 and 0 <= y < 16:
            v = rng.choice([1.0, 0.8, 1.2, 0.65])
            base.putpixel((x, y), shift(gem, v))
    base.save(out(f"block/{w['gem']}_ore.png"))

def gem_block_pattern(color):
    """фасеточный блок самоцвета (как алмазный)"""
    rng = random.Random(f"gemblock-{color}")
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    c = hx(color)
    noise_fill(img, (0, 0, 16, 16), c, rng, 8)
    border(img, (0, 0, 16, 16), shift(c, 0.55))
    border(img, (1, 1, 14, 14), shift(c, 1.35))
    d = ImageDraw.Draw(img)
    # внутренние грани
    for pts in [((3,3),(7,3),(3,7)), ((8,3),(12,3),(12,7)), ((3,8),(3,12),(7,12)), ((8,12),(12,8),(12,12))]:
        d.line([tuple(p) for p in pts], fill=shift(c, 0.75))
    for (x, y) in [(4,4),(9,5),(5,9),(10,10)]:
        img.putpixel((x, y), shift(c, 1.5))
    return img

def gen_portal(w):
    rng = random.Random(f"portal-{w['id']}")
    c = hx(w["gem_color"])
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for y in range(16):
        for x in range(16):
            dx, dy = x-7.5, y-7.5
            d = math.hypot(dx, dy)
            ang = math.atan2(dy, dx)
            swirl = math.sin(d*0.7 - ang*2)
            f = 0.75 + 0.5*swirl + rng.uniform(-0.06, 0.06)
            px = shift(c, max(0.35, min(1.6, f)))
            a = 255 if d < 7.6 else (140 if d < 8.0 else 0)
            img.putpixel((x, y), (px[0], px[1], px[2], a))
    d = ImageDraw.Draw(img)
    d.rectangle([0,0,15,15], outline=shift(c, 0.4))
    return img

def gen_hub_blocks():
    rng = random.Random("hub-stone")
    c = hx("3a3a4a")
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    noise_fill(img, (0, 0, 16, 16), c, rng, 7)
    border(img, (0, 0, 16, 16), shift(c, 0.6))
    img.save(out("block/hub_stone.png"))
    # bricks
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    noise_fill(img, (0, 0, 16, 16), shift(c, 1.1), rng, 7)
    d = ImageDraw.Draw(img)
    mortar = shift(c, 0.5)
    d.line([(0,4),(15,4)], fill=mortar); d.line([(0,9),(15,9)], fill=mortar); d.line([(0,14),(15,14)], fill=mortar)
    d.line([(5,0),(5,3)], fill=mortar); d.line([(11,5),(11,8)], fill=mortar); d.line([(5,10),(5,13)], fill=mortar); d.line([(11,15),(11,15)], fill=mortar)
    d.line([(11,0),(11,0)], fill=mortar); d.line([(5,5),(5,5)], fill=mortar)
    gold = hx(HUB["color"])
    img.putpixel((2, 2), shift(gold, 1.1)); img.putpixel((13, 7), shift(gold, 0.9)); img.putpixel((8, 12), shift(gold, 1.2))
    img.save(out("block/hub_stone_bricks.png"))
    # pillar
    rng2 = random.Random("hub-pillar")
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    noise_fill(img, (2, 0, 12, 16), shift(c, 0.9), rng2, 6)
    for y in range(16):
        for x in (2, 3, 12, 13):
            img.putpixel((x, y), shift(c, 0.65))
    for y in (0, 15):
        for x in range(2, 14):
            img.putpixel((x, y), shift(gold, 1.0))
    img.save(out("block/hub_pillar.png"))
    # lantern
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    noise_fill(img, (3, 3, 10, 10), hx("ffe9a8"), random.Random("lan"), 12)
    border(img, (3, 3, 10, 10), hx("c8a84a"))
    for y in range(16):
        for x in range(16):
            if (x in (4, 11) or y in (4, 11)) and 3 <= x <= 12 and 3 <= y <= 12:
                p = img.getpixel((x, y))
                if p[3] > 0: img.putpixel((x, y), shift(hx("fff6d0"), 1.0))
    img.save(out("block/hub_lantern.png"))
    # boss altar
    rng3 = random.Random("altar")
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    noise_fill(img, (0, 0, 16, 16), hx("2e2e3a"), rng3, 6)
    border(img, (0, 0, 16, 16), hx(HUB["color"]))
    d = ImageDraw.Draw(img)
    d.ellipse([4, 4, 11, 11], outline=shift(hx(HUB["color"]), 1.3))
    d.ellipse([6, 6, 9, 9], fill=hx("8a2be2"))
    img.save(out("block/boss_altar.png"))
    img2 = img.copy()
    d2 = ImageDraw.Draw(img2)
    d2.line([(0,7),(15,7)], fill=shift(hx(HUB["color"]), 0.8)); d2.line([(7,0),(7,15)], fill=shift(hx(HUB["color"]), 0.8))
    img2.save(out("block/boss_altar_top.png"))

# --------------------------------------------------------------------------
# ПРЕДМЕТЫ
# --------------------------------------------------------------------------
def gem_sprite(color):
    rng = random.Random(f"gem-{color}")
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    c = hx(color)
    # кристалл: ромб с гранями
    pts = [(7,1),(11,6),(7,14),(3,6)]
    d = ImageDraw.Draw(img)
    d.polygon(pts, fill=c)
    d.line([tuple(p) for p in pts] + [pts[0]], fill=shift(c, 0.55))
    d.polygon([(7,1),(9,4),(7,6),(5,4)], fill=shift(c, 1.45))
    d.polygon([(3,6),(7,6),(7,14)], fill=shift(c, 0.8))
    img.putpixel((9, 8), shift(c, 1.6)); img.putpixel((8, 9), shift(c, 1.6))
    return img

def soul_sprite(color):
    rng = random.Random(f"soul-{color}")
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    c = hx(color)
    for y in range(16):
        for x in range(16):
            dx, dy = x-7.5, y-7.5
            d = math.hypot(dx, dy)
            if d < 6.5:
                f = 1.4 - d*0.09 + 0.08*math.sin((x+y)*0.9)
                a = int(255 * max(0.15, 1.0 - (d/6.5)**2.5))
                img.putpixel((x, y), (*shift(c, max(0.5, min(1.7, f)))[:3], a))
    d = ImageDraw.Draw(img)
    d.ellipse([5,6,7,8], fill=(255,255,255,220)); d.ellipse([9,6,11,8], fill=(255,255,255,220))
    return img

def sword_sprite(color, big=False, glow=False):
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    c = hx(color)
    # рукоять
    for i, p in enumerate([(3,12),(4,11)]):
        d.point(p, fill=hx("6b4a2a"))
    d.point((2,13), fill=hx("6b4a2a")); d.point((3,13), fill=hx("4a3018"))
    # гарда
    d.line([(4,11),(6,11)], fill=hx(HUB["color"])); d.line([(5,10),(5,12)], fill=hx(HUB["color"]))
    # клинок по диагонали
    L = 9 if big else 8
    for i in range(L):
        x, y = 5+i, 10-i
        d.point((x, y), fill=shift(c, 1.35) if glow else shift(c, 1.1))
        d.point((x+1, y), fill=shift(c, 0.85))
        d.point((x, y-1), fill=shift(c, 1.25) if glow else shift(c, 1.0))
    if big:
        d.point((13,2), fill=shift(c,1.7)); d.point((14,1), fill=(255,255,255,255))
    return img

def staff_sprite(color):
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    c = hx(color)
    for i in range(10):
        d.point((3+i, 13-i), fill=hx("5a4a3a") if i < 7 else hx("4a3a2a"))
    # кристалл наверху
    pts = [(12,1),(14,4),(12,7),(10,4)]
    d.polygon(pts, fill=c)
    d.line(pts+[pts[0]], fill=shift(c, 0.5))
    d.point((12,3), fill=(255,255,255,230))
    d.line([(9,7),(8,8)], fill=hx("5a4a3a"))
    return img

TOOL_SHAPES = {
    "sword": lambda d, c, h: (
        [d.point((6+i, 9-i), fill=c) for i in range(7)] +
        [d.point((7+i, 9-i), fill=shift(c, 0.8)) for i in range(6)]),
    "pickaxe": lambda d, c, h: (
        d.arc([2,1,13,10], 200, 340, fill=c) or
        [d.point((4+i, 5+abs(i-4)//2), fill=h) for i in range(0, 9, 2)]),
    "axe": lambda d, c, h: (
        d.polygon([(8,2),(13,4),(12,8),(8,7)], fill=c) or
        d.line([(9,6),(9,13)], fill=h, width=1)),
    "shovel": lambda d, c, h: (
        d.polygon([(9,1),(12,4),(9,7),(6,4)], fill=c) or
        d.line([(9,6),(9,13)], fill=h, width=1)),
    "hoe": lambda d, c, h: (
        d.line([(7,2),(12,3)], fill=c, width=1) or
        d.line([(9,6),(9,13)], fill=h, width=1)),
}

def tool_sprite(shape, color):
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    c = hx(color)
    h = hx("7a5a3a")
    if shape in ("pickaxe", "axe", "shovel", "hoe"):
        # ручка по диагонали
        for i in range(8):
            d.point((4+i, 12-i), fill=h)
        d.point((3,13), fill=hx("4a3018"))
    if shape == "sword":
        TOOL_SHAPES["sword"](d, c, h)
        d.line([(4,10),(6,10)], fill=hx(HUB["color"]))
        d.point((3,12), fill=h); d.point((2,13), fill=h)
    elif shape == "pickaxe":
        d.arc([2,0,12,10], 200, 340, fill=c)
        d.point((3,3), fill=c); d.point((4,2), fill=shift(c,1.2)); d.point((10,2), fill=shift(c,1.2)); d.point((11,3), fill=c)
    elif shape == "axe":
        d.polygon([(7,1),(12,3),(11,7),(7,6)], fill=c)
        d.point((9,3), fill=shift(c, 1.3))
    elif shape == "shovel":
        d.polygon([(9,1),(12,4),(9,7),(6,4)], fill=c)
        d.point((9,3), fill=shift(c, 1.3))
    elif shape == "hoe":
        d.line([(6,2),(11,2)], fill=c, width=1)
        d.line([(6,2),(7,4)], fill=c, width=1)
    return img

ARMOR_PIECES = {
    "helmet": lambda d, c: (
        d.rectangle([4,4,11,8], fill=c) or d.rectangle([4,8,11,10], fill=shift(c,0.8)) or
        d.point((4,9), fill=(0,0,0,0)) or d.point((11,9), fill=(0,0,0,0))),
    "chestplate": lambda d, c: (
        d.polygon([(4,3),(11,3),(12,6),(11,12),(4,12),(3,6)], fill=c) or
        d.line([(4,4),(11,4)], fill=shift(c,1.2))),
    "leggings": lambda d, c: (
        d.rectangle([4,3,11,6], fill=c) or d.rectangle([4,6,7,12], fill=shift(c,0.9)) or
        d.rectangle([8,6,11,12], fill=shift(c,0.9))),
    "boots": lambda d, c: (
        d.rectangle([4,7,7,12], fill=c) or d.rectangle([8,7,11,12], fill=c) or
        d.rectangle([4,11,7,12], fill=shift(c,0.7)) or d.rectangle([8,11,11,12], fill=shift(c,0.7))),
}

def armor_sprite(piece, color):
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    c = hx(color)
    ARMOR_PIECES[piece](d, c)
    return img

def food_sprites():
    # мясо
    for name, col, cooked in [("game_meat", "c85a5a", False), ("cooked_game_meat", "9a6a3a", True)]:
        rng = random.Random(name)
        img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
        d = ImageDraw.Draw(img)
        c = hx(col)
        d.ellipse([3,5,12,13], fill=c)
        d.ellipse([5,4,10,6], fill=shift(c, 1.2))
        d.ellipse([6,7,9,11], fill=shift(c, 0.75) if cooked else hx("e8b0b0"))
        img.save(out(f"item/{name}.png"))
    # ягода
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    c = hx("5ab8e8")
    d.ellipse([5,6,11,12], fill=c); d.ellipse([6,7,8,9], fill=shift(c,1.4))
    d.line([(8,6),(9,3)], fill=hx("3a7a3a")); d.point((10,3), fill=hx("5aba5a"))
    img.save(out("item/frost_berry.png"))
    # рагу
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.polygon([(3,7),(13,7),(11,13),(5,13)], fill=hx("8a6a4a"))
    d.ellipse([4,5,12,8], fill=hx("d95fd0"))
    d.point((6,6), fill=hx("f0a8e8")); d.point((9,5), fill=hx("f0a8e8"))
    img.save(out("item/mystic_stew.png"))

def utility_sprites():
    # hub compass
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    g = hx(HUB["color"])
    d.ellipse([2,2,13,13], outline=g, width=2)
    d.polygon([(8,3),(10,8),(8,13),(6,8)], fill=hx("e85a5a"))
    d.point((8,8), fill=hx("fff0c0"))
    img.save(out("item/hub_compass.png"))
    # world selector
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.rectangle([2,3,13,12], fill=hx("d8cfa8"), outline=hx("8a7a5a"))
    d.line([(3,8),(12,8)], fill=hx("9ab0d8"))
    d.line([(8,4),(8,11)], fill=hx("9ab0d8"))
    d.point((5,6), fill=hx("5a9a5a")); d.point((11,10), fill=hx("c9d7f2"))
    d.polygon([(7,1),(9,1),(8,3)], fill=hx("e8e8e8"))
    img.save(out("item/world_selector.png"))
    # crown
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    g = hx(HUB["color"])
    d.polygon([(3,10),(3,4),(6,7),(8,2),(10,7),(13,4),(13,10)], fill=g)
    d.rectangle([3,10,13,12], fill=shift(g,0.85))
    for x, c in [(4,hx("e85a5a")),(8,hx("5a8ae8")),(12,hx("5aba7a"))]:
        d.point((x,9), fill=c)

def summon_sprite(color):
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    c = hx(color)
    # тотем-звезда
    d.polygon([(8,1),(10,6),(15,8),(10,10),(8,15),(6,10),(1,8),(6,6)], fill=c)
    d.polygon([(8,4),(9,7),(11,8),(9,9),(8,12),(7,9),(5,8),(7,7)], fill=shift(c,1.35))
    return img

def egg_sprite(base, accent):
    rng = random.Random(f"egg{base}{accent}")
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    b, a = hx(base), hx(accent)
    d.ellipse([4,2,11,13], fill=b)
    d.ellipse([5,3,7,5], fill=shift(b,1.3))
    for _ in range(9):
        x, y = rng.randint(5, 10), rng.randint(3, 12)
        if (x-7.5)**2/16 + (y-7.5)**2/25 < 1:
            img.putpixel((x, y), a)
    d.point((8,14), fill=shift(b,0.7)); d.point((8,15), fill=shift(b,0.7))
    return img

# --------------------------------------------------------------------------
# СУЩНОСТИ
# --------------------------------------------------------------------------
# Раскладка 64x64 гуманоида (как зомби/игрок)
HUMANOID_UV = {
    "head_all": (0, 0, 32, 16),
    "face": (8, 8, 8, 8),
    "body": (16, 16, 24, 16),
    "body_front": (20, 20, 8, 12),
    "arm_r": (40, 16, 16, 16),
    "arm_l": (32, 48, 16, 16),
    "leg_r": (0, 16, 16, 16),
    "leg_l": (16, 48, 16, 16),
}

SKINS = {
    "leather":  dict(skin="d8a878", shirt="7a4a2a", pants="4a3a2a", eyes="2a2a3a", boot="3a2a1a"),
    "linen":    dict(skin="c9b896", shirt="c9b896", pants="b8a684", eyes="3a2a1a", boot="a89068"),
    "ice":      dict(skin="b8d8e8", shirt="6a9ab8", pants="4a6a8a", eyes="1a3a5a", boot="c8e8f8"),
    "stone":    dict(skin="8a8a8a", shirt="6a6a6a", pants="5a5a5a", eyes="3a3a4a", boot="4a4a4a"),
    "gold":     dict(skin="e8c73a", shirt="c9a52a", pants="a8841a", eyes="5a3a1a", boot="8a6a2a"),
    "bark":     dict(skin="8a6a4a", shirt="6a5a3a", pants="5a4a2a", eyes="3afa3a", boot="4a3a2a"),
    "leaf":     dict(skin="a8c97a", shirt="4a7a2a", pants="3a5a1a", eyes="2a1a0a", boot="2a4a1a"),
    "witch":    dict(skin="c8b8a8", shirt="3a4a2a", pants="2a3a1a", eyes="5aa83a", boot="1a2a1a"),
    "magma":    dict(skin="4a2a24", shirt="8a3a2a", pants="3a1a14", eyes="ffb03a", boot="2a1410"),
    "ash":      dict(skin="9a9a9a", shirt="6a6a6a", pants="5a5a5a", eyes="d84a4a", boot="4a4a4a"),
    "ghost":    dict(skin="b8d8dc", shirt="8ab8c0", pants="6a98a8", eyes="1a4a5a", boot="5a8898"),
    "void":     dict(skin="4a3a6a", shirt="2a1a4a", pants="1a1030", eyes="c88aff", boot="140a24"),
    "mushroom": dict(skin="e8d8c8", shirt="d95fd0", pants="b84ab0", eyes="3a1a3a", boot="8a3a8a"),
    "iron":     dict(skin="d8d8d8", shirt="9a9a9a", pants="7a7a7a", eyes="4a5a8a", boot="5a5a5a"),
    "yeti":     dict(skin="e8f0f8", shirt="d8e4ee", pants="c8d8e4", eyes="3a5a8a", boot="b8ccdc"),
}

def humanoid_texture(skin_key, accent_hex, eyes_override=None, boss=False):
    pal = SKINS[skin_key]
    rng = random.Random(f"skin-{skin_key}-{accent_hex}-{boss}")
    img = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    accent = hx(accent_hex)
    # голова
    noise_fill(img, HUMANOID_UV["head_all"], hx(pal["skin"]), rng, 8)
    # лицо
    fx, fy, fw, fh = HUMANOID_UV["face"]
    eyes = hx(eyes_override or pal["eyes"])
    for ex in (fx+1, fx+5):
        img.putpixel((ex, fy+3), eyes); img.putpixel((ex+1, fy+3), eyes)
    if boss:
        img.putpixel((fx+2, fy+5), hx("e8c73a")); img.putpixel((fx+5, fy+5), hx("e8c73a"))
    img.putpixel((fx+3, fy+5), shift(hx(pal["skin"]), 0.6)); img.putpixel((fx+4, fy+5), shift(hx(pal["skin"]), 0.6))
    # шапка/волосы сверху головы
    for x in range(fx-2, fx+10):
        for y in range(fy-7, fy-3):
            img.putpixel((x, y), shift(hx(pal["hair"] if "hair" in pal else pal["skin"]), 0.8))
    # тело
    noise_fill(img, HUMANOID_UV["body"], hx(pal["shirt"]), rng, 8)
    bx, by, bw, bh = HUMANOID_UV["body_front"]
    for x in range(bx, bx+bw):  # ремень
        img.putpixel((x, by+bh-2), shift(hx(pal["boot"]), 0.9)); img.putpixel((x, by+bh-1), shift(hx(pal["boot"]), 0.8))
    # эмблема-самоцвет
    cx, cy = bx+3, by+3
    img.putpixel((cx, cy), accent); img.putpixel((cx+1, cy), shift(accent, 1.2))
    img.putpixel((cx, cy+1), shift(accent, 0.8)); img.putpixel((cx+1, cy+1), accent)
    # руки
    noise_fill(img, HUMANOID_UV["arm_r"], hx(pal["skin"]), rng, 8)
    noise_fill(img, HUMANOID_UV["arm_l"], hx(pal["skin"]), rng, 8)
    # рукава (верхняя половина)
    for (ax, ay) in [(HUMANOID_UV["arm_r"][0]+4, HUMANOID_UV["arm_r"][1]+2), (HUMANOID_UV["arm_l"][0]+4, HUMANOID_UV["arm_l"][1]+2)]:
        for x in range(ax, ax+8):
            for y in range(ay, ay+5):
                if rng.random() < 0.9: img.putpixel((x, y), jitter(hx(pal["shirt"]), rng, 8))
    # ноги
    noise_fill(img, HUMANOID_UV["leg_r"], hx(pal["pants"]), rng, 8)
    noise_fill(img, HUMANOID_UV["leg_l"], hx(pal["pants"]), rng, 8)
    # ботинки
    for r in (HUMANOID_UV["leg_r"], HUMANOID_UV["leg_l"]):
        lx, ly = r[0]+4, r[1]+2
        for x in range(lx, lx+8):
            for y in range(ly+8, ly+12):
                img.putpixel((x, y), jitter(hx(pal["boot"]), rng, 6))
    return img

# Раскладка 64x64 квадрупеда (модель QuadrupedBeastModel)
QUAD_UV = {
    "head": (0, 8, 24, 12),
    "head_front": (6, 14, 6, 6),
    "body": (0, 20, 44, 26),
    "legs": [(48, 0), (48, 16), (48, 32), (48, 48)],
}

QUAD_SKINS = {
    "brown":   dict(body="6a4a32", head="7a5a3a", leg="4a3520", belly="8a6a4a"),
    "gray":    dict(body="9a9aa8", head="b8b8c4", leg="6a6a78", belly="c8c8d4"),
    "gold":    dict(body="c49a3a", head="d8b04a", leg="a8842a", belly="e8c86a"),
    "striped": dict(body="e8e8e8", head="e8e8e8", leg="2a2a2a", belly="c8c8c8"),
    "sand":    dict(body="a8865a", head="b8966a", leg="8a6842", belly="c8a878"),
}

def quadruped_texture(skin_key, accent_hex):
    pal = QUAD_SKINS[skin_key]
    rng = random.Random(f"quad-{skin_key}-{accent_hex}")
    img = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    accent = hx(accent_hex)
    noise_fill(img, QUAD_UV["head"], hx(pal["head"]), rng, 8)
    fx, fy, fw, fh = QUAD_UV["head_front"]
    for ex in (fx+1, fx+4):
        img.putpixel((ex, fy+2), (30, 30, 40, 255)); img.putpixel((ex, fy+3), (30, 30, 40, 255))
    img.putpixel((fx+2, fy+5), shift(hx(pal["head"]), 0.6))  # нос
    noise_fill(img, QUAD_UV["body"], hx(pal["body"]), rng, 8)
    # полосы для зебры / пятна для остальных
    if skin_key == "striped":
        for i in range(5):
            x = 20 + i*4
            for y in range(24, 44):
                if rng.random() < 0.85: img.putpixel((x, y), (26, 26, 26, 255))
    else:
        for _ in range(6):
            x, y = rng.randint(22, 42), rng.randint(22, 42)
            img.putpixel((x, y), shift(accent, 0.9)); img.putpixel((x+1, y), shift(accent, 0.75))
    for (lx, ly) in QUAD_UV["legs"]:
        noise_fill(img, (lx, ly, 12, 15), hx(pal["leg"]), rng, 6)
        for x in range(lx+3, lx+9):
            img.putpixel((x, ly+12), shift(hx(pal["leg"]), 0.7))
    return img

def recolor_vanilla(van_img, target_hex, keep_dark=True, keep_red=False):
    """перекраска ванильной текстуры с сохранением теней"""
    img = van_img.copy()
    t = colorsys.rgb_to_hsv(*[v/255 for v in hx(target_hex)])
    px = img.load()
    w, h = img.size
    reds = set()
    if keep_red:
        for y in range(h):
            for x in range(w):
                r, g, b, a = px[x, y]
                if a > 0 and r > 140 and g < 90 and b < 90:
                    reds.add((x, y))
    for y in range(h):
        for x in range(w):
            r, g, b, a = px[x, y]
            if a == 0: continue
            if (x, y) in reds:
                px[x, y] = (255, 40, 40, a); continue
            hh, ss, vv = colorsys.rgb_to_hsv(r/255, g/255, b/255)
            if keep_dark and vv < 0.25:  # тёмные детали (глаза/рот) сохраняем
                continue
            nr, ng, nb = colorsys.hsv_to_rgb(t[0], t[1] if ss > 0.05 else t[1]*0.5, vv)
            px[x, y] = (int(nr*255), int(ng*255), int(nb*255), a)
    return img

# --------------------------------------------------------------------------
# ГЛАВНЫЙ ГЕНЕРАТОР
# --------------------------------------------------------------------------
def main():
    print("Загрузка ванильных текстур...")
    load_vanilla()
    print("Блоки...")
    for w in WORLDS:
        gen_ore(w)
        gem_block_pattern(w["gem_color"]).save(out(f"block/{w['gem']}_block.png"))
        gen_portal(w).save(out(f"block/portal_{w['id']}.png"))
    gen_hub_blocks()

    print("Предметы...")
    for w in WORLDS:
        gem_sprite(w["gem_color"]).save(out(f"item/{w['gem']}.png"))
        soul_sprite(w["gem_color"]).save(out(f"item/{w['gem']}_soul.png"))
        summon_sprite(w["gem_color"]).save(out(f"item/summon_{BOSSES[[b['world'] for b in BOSSES].index(w['id'])]['id']}.png"))
    for t in TIER_SETS:
        for shape in ("sword", "pickaxe", "axe", "shovel", "hoe"):
            tool_sprite(shape, t["color"]).save(out(f"item/{t['gem']}_{shape}.png"))
        for piece in ("helmet", "chestplate", "leggings", "boots"):
            armor_sprite(piece, t["color"]).save(out(f"item/{t['gem']}_{piece}.png"))
    food_sprites(); utility_sprites()
    for b in BOSSES:
        if b["staff"]:
            staff_sprite(b["accent"]).save(out(f"item/{b['artifact']}.png"))
        else:
            sword_sprite(b["accent"], big=True, glow=True).save(out(f"item/{b['artifact']}.png"))
    # корона миров (по мотивам hub color)
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    g = hx(HUB["color"])
    d.polygon([(3,10),(3,4),(6,7),(8,2),(10,7),(13,4),(13,10)], fill=g)
    d.rectangle([3,10,13,12], fill=shift(g,0.85))
    colors = [hx("e85a5a"), hx("5a8ae8"), hx("5aba7a"), hx("d95fd0"), hx("f0a13a"), hx("7fd4e8")]
    for i, x in enumerate([4, 6, 8, 10, 12]):
        d.point((x, 9), fill=colors[i % len(colors)])
    img.save(out("item/crown_of_worlds.png"))
    # яйца спавна
    for m in MOBS:
        base_hex = SKINS[m["skin"]]["skin"] if m["skin"] in SKINS else QUAD_SKINS.get(m["skin"], {}).get("body", "d8d8d8")
        egg_sprite(base_hex, m["accent"]).save(out(f"item/{m['id']}_spawn_egg.png"))
    for b in BOSSES:
        egg_sprite(SKINS[b["skin"]]["skin"], b["accent"]).save(out(f"item/{b['id']}_spawn_egg.png"))
    for t in TRADERS:
        egg_sprite(SKINS[t["skin"]]["skin"], t["accent"]).save(out(f"item/{t['id']}_spawn_egg.png"))

    print("Сущности...")
    for m in MOBS:
        if m["kind"] in ("humanoid", "trader"):
            humanoid_texture(m["skin"], m["accent"], boss=False).save(out(f"entity/{m['id']}.png"))
        elif m["kind"] in ("beast", "beast_hostile"):
            quadruped_texture(m["skin"], m["accent"]).save(out(f"entity/{m['id']}.png"))
        elif m["kind"] == "spider":
            recolor_vanilla(VAN["spider"], m["accent"], keep_red=True).save(out(f"entity/{m['id']}.png"))
        elif m["kind"] == "slime":
            recolor_vanilla(VAN["slime"], m["accent"]).save(out(f"entity/{m['id']}.png"))
        elif m["kind"] == "wisp":
            recolor_vanilla(VAN["ghast"], m["accent"]).save(out(f"entity/{m['id']}.png"))
    for b in BOSSES:
        humanoid_texture(b["skin"], b["accent"], boss=True).save(out(f"entity/{b['id']}.png"))
    for t in TRADERS:
        humanoid_texture(t["skin"], t["accent"]).save(out(f"entity/{t['id']}.png"))

    # иконка мода
    icon = Image.new("RGBA", (128, 128), (24, 22, 34, 255))
    d = ImageDraw.Draw(icon)
    d.ellipse([24, 24, 104, 104], outline=hx(HUB["color"]), width=6)
    colors = [w["gem_color"] for w in WORLDS]
    for i, w in enumerate(WORLDS):
        ang = -math.pi/2 + i * 2*math.pi/12
        x, y = 64 + 44*math.cos(ang), 64 + 44*math.sin(ang)
        d.ellipse([x-9, y-9, x+9, y+9], fill=hx(w["gem_color"]), outline=(20, 18, 28, 255))
    d.ellipse([52, 52, 76, 76], fill=hx(HUB["color"]))
    icon.save(os.path.join(HERE, "..", "src", "main", "resources", "assets", MOD_ID, "icon.png"))

    # контактный лист для просмотра
    print("Контактный лист...")
    import glob
    tiles = sorted(glob.glob(os.path.join(ROOT, "item", "*.png")))
    sheet_w = 12 * 20
    rows = (len(tiles) + 11) // 12
    sheet = Image.new("RGBA", (sheet_w, rows*20), (40, 40, 50, 255))
    for i, tp in enumerate(tiles):
        t = Image.open(tp).resize((16, 16), Image.NEAREST)
        sheet.paste(t, ((i % 12)*20 + 2, (i//12)*20 + 2), t)
    sheet.save("/tmp/preview_items.png")
    tiles_b = sorted(glob.glob(os.path.join(ROOT, "block", "*.png")))
    rows = (len(tiles_b) + 11) // 12
    sheet = Image.new("RGBA", (sheet_w, rows*20), (40, 40, 50, 255))
    for i, tp in enumerate(tiles_b):
        t = Image.open(tp).resize((16, 16), Image.NEAREST)
        sheet.paste(t, ((i % 12)*20 + 2, (i//12)*20 + 2), t)
    sheet.save("/tmp/preview_blocks.png")
    tiles_e = sorted(glob.glob(os.path.join(ROOT, "entity", "*.png")))
    sheet = Image.new("RGBA", (len(tiles_e)*70, 140), (40, 40, 50, 255))
    for i, tp in enumerate(tiles_e):
        t = Image.open(tp)
        tw, th = t.size
        scale = 64/tw
        t = t.resize((int(tw*scale), int(th*scale)), Image.NEAREST)
        sheet.paste(t, (i*70 + 2, 4), t)
    sheet.save("/tmp/preview_entities.png")
    n = len(tiles) + len(tiles_b) + len(tiles_e)
    print(f"Готово: {n} текстур")

if __name__ == "__main__":
    main()
