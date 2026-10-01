package com.arenacoding.mcadvanced.registry;

import com.arenacoding.mcadvanced.MinecraftAdvanced;
import com.arenacoding.mcadvanced.entity.MobConfig;
import com.arenacoding.mcadvanced.entity.RpgBeast;
import com.arenacoding.mcadvanced.entity.RpgBoss;
import com.arenacoding.mcadvanced.entity.RpgHumanoid;
import com.arenacoding.mcadvanced.entity.RpgSlime;
import com.arenacoding.mcadvanced.entity.RpgSpider;
import com.arenacoding.mcadvanced.entity.RpgTrader;
import com.arenacoding.mcadvanced.entity.RpgWisp;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.cubemob.Slime;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.arenacoding.mcadvanced.MinecraftAdvanced.MOD_ID;

/**
 * Все 37 сущностей: 22 обычных моба, 12 боссов, 3 торговца.
 */
public final class ModEntities {
    private static final Map<String, EntityType<?>> ALL = new LinkedHashMap<>();
    private static final Map<String, MobConfig> CONFIGS = new LinkedHashMap<>();

    // ------------------------------------------------------------------
    // Обычные мобы
    // ------------------------------------------------------------------
    public static final EntityType<RpgHumanoid> BANDIT = humanoid("bandit",
            MobConfig.builder(20, 4.0, 0.32).sounds(SoundEvents.ZOMBIE_AMBIENT, SoundEvents.ZOMBIE_HURT, SoundEvents.ZOMBIE_DEATH),
            0.6f, 1.95f);
    public static final EntityType<RpgBeast> BULL = beast("bull",
            MobConfig.builder(16, 2.0, 0.25).sounds(SoundEvents.HORSE_AMBIENT, SoundEvents.HORSE_HURT, SoundEvents.HORSE_DEATH),
            0.9f, 1.4f);
    public static final EntityType<RpgHumanoid> MUMMY = humanoid("mummy",
            MobConfig.builder(26, 5.0, 0.26).armor(2).sounds(SoundEvents.ZOMBIE_AMBIENT, SoundEvents.ZOMBIE_HURT, SoundEvents.ZOMBIE_DEATH),
            0.6f, 1.95f);
    public static final EntityType<RpgSpider> SCORPION = spider("scorpion",
            MobConfig.builder(16, 4.0, 0.3).hitEffect(MobEffects.POISON, 6, 0)
                    .sounds(SoundEvents.SPIDER_AMBIENT, SoundEvents.SPIDER_HURT, SoundEvents.SPIDER_DEATH),
            1.3f, 0.9f);
    public static final EntityType<RpgHumanoid> FROST_REAVER = humanoid("frost_reaver",
            MobConfig.builder(24, 5.0, 0.3).hitEffect(MobEffects.SLOWNESS, 5, 1)
                    .sounds(SoundEvents.ZOMBIE_AMBIENT, SoundEvents.ZOMBIE_HURT, SoundEvents.ZOMBIE_DEATH),
            0.6f, 1.95f);
    public static final EntityType<RpgBeast> FROST_STAG = beast("frost_stag",
            MobConfig.builder(14, 2.0, 0.3).sounds(SoundEvents.GOAT_AMBIENT, SoundEvents.GOAT_HURT, SoundEvents.GOAT_DEATH),
            0.9f, 1.4f);
    public static final EntityType<RpgHumanoid> ROCK_GOLEM = humanoid("rock_golem",
            MobConfig.builder(40, 7.0, 0.22).armor(6).knockback(0.6)
                    .sounds(SoundEvents.IRON_GOLEM_DAMAGE, SoundEvents.IRON_GOLEM_HURT, SoundEvents.IRON_GOLEM_DEATH),
            1.1f, 2.7f);
    public static final EntityType<RpgHumanoid> LION = humanoid("lion",
            MobConfig.builder(22, 6.0, 0.34).sounds(SoundEvents.POLAR_BEAR_AMBIENT, SoundEvents.POLAR_BEAR_HURT, SoundEvents.POLAR_BEAR_DEATH),
            0.9f, 1.4f);
    public static final EntityType<RpgBeast> ZEBRA = beast("zebra",
            MobConfig.builder(16, 2.0, 0.32).sounds(SoundEvents.HORSE_AMBIENT, SoundEvents.HORSE_HURT, SoundEvents.HORSE_DEATH),
            0.9f, 1.4f);
    public static final EntityType<RpgHumanoid> TREANT = humanoid("treant",
            MobConfig.builder(34, 6.0, 0.2).armor(4).knockback(0.5)
                    .sounds(SoundEvents.ZOMBIE_AMBIENT, SoundEvents.ZOMBIE_HURT, SoundEvents.ZOMBIE_DEATH),
            1.0f, 2.4f);
    public static final EntityType<RpgWisp> FOREST_WISP = wisp("forest_wisp",
            MobConfig.builder(10, 0, 0.6).sounds(SoundEvents.GHAST_AMBIENT, SoundEvents.GHAST_HURT, SoundEvents.GHAST_DEATH),
            1.0f, 1.0f);
    public static final EntityType<RpgSpider> VINE_SPIDER = spider("vine_spider",
            MobConfig.builder(18, 4.0, 0.32).hitEffect(MobEffects.POISON, 6, 0)
                    .sounds(SoundEvents.SPIDER_AMBIENT, SoundEvents.SPIDER_HURT, SoundEvents.SPIDER_DEATH),
            1.3f, 0.9f);
    public static final EntityType<RpgHumanoid> JUNGLE_BRUTE = humanoid("jungle_brute",
            MobConfig.builder(28, 6.0, 0.3).sounds(SoundEvents.ZOMBIE_AMBIENT, SoundEvents.ZOMBIE_HURT, SoundEvents.ZOMBIE_DEATH),
            0.7f, 1.95f);
    public static final EntityType<RpgHumanoid> BOG_WITCH = humanoid("bog_witch",
            MobConfig.builder(24, 5.0, 0.28).hitEffect(MobEffects.POISON, 8, 0)
                    .sounds(SoundEvents.WITCH_AMBIENT, SoundEvents.WITCH_HURT, SoundEvents.WITCH_DEATH),
            0.6f, 1.95f);
    public static final EntityType<RpgHumanoid> MAGMA_BEAST = humanoid("magma_beast",
            MobConfig.builder(32, 7.0, 0.28).armor(4).fireImmune().setTargetOnFire()
                    .sounds(SoundEvents.BLAZE_AMBIENT, SoundEvents.BLAZE_HURT, SoundEvents.BLAZE_DEATH),
            0.8f, 2.1f);
    public static final EntityType<RpgHumanoid> ASH_WRAITH = humanoid("ash_wraith",
            MobConfig.builder(18, 4.0, 0.36).fireImmune()
                    .sounds(SoundEvents.BLAZE_AMBIENT, SoundEvents.BLAZE_HURT, SoundEvents.BLAZE_DEATH),
            0.6f, 1.95f);
    public static final EntityType<RpgHumanoid> PIRATE_GHOST = humanoid("pirate_ghost",
            MobConfig.builder(20, 4.0, 0.3).rangedArrow()
                    .sounds(SoundEvents.SKELETON_AMBIENT, SoundEvents.SKELETON_HURT, SoundEvents.SKELETON_DEATH),
            0.6f, 1.95f);
    public static final EntityType<RpgSpider> SHORE_CRAWLER = spider("shore_crawler",
            MobConfig.builder(14, 3.0, 0.3)
                    .sounds(SoundEvents.SPIDER_AMBIENT, SoundEvents.SPIDER_HURT, SoundEvents.SPIDER_DEATH),
            1.2f, 0.8f);
    public static final EntityType<RpgHumanoid> VOID_WRAITH = humanoid("void_wraith",
            MobConfig.builder(28, 6.0, 0.34).hitEffect(MobEffects.WITHER, 4, 0)
                    .sounds(SoundEvents.ENDERMAN_AMBIENT, SoundEvents.ENDERMAN_HURT, SoundEvents.ENDERMAN_DEATH),
            0.6f, 2.2f);
    public static final EntityType<RpgSpider> NULL_CRAWLER = spider("null_crawler",
            MobConfig.builder(18, 4.0, 0.34).hitEffect(MobEffects.WITHER, 3, 0)
                    .sounds(SoundEvents.SPIDER_AMBIENT, SoundEvents.SPIDER_HURT, SoundEvents.SPIDER_DEATH),
            1.3f, 0.9f);
    public static final EntityType<RpgSlime> SPORE_SLIME = slime("spore_slime",
            MobConfig.builder(16, 3.0, 0.3).sounds(SoundEvents.SLIME_ATTACK, SoundEvents.SLIME_HURT, SoundEvents.SLIME_DEATH),
            1.04f, 1.04f);
    public static final EntityType<RpgTrader> MUSHROOMLING = trader("mushroomling", () -> List.of(
            entry(Items.EMERALD, 4, ModItems.item("shroomite"), 1, 8, 4),
            entry(ModItems.item("shroomite"), 3, Items.EMERALD, 1, 8, 4),
            entry(Items.EMERALD, 6, ModItems.item("mystic_stew"), 2, 8, 6),
            entry(Items.EMERALD, 12, Items.EXPERIENCE_BOTTLE, 2, 6, 8),
            entry(ModItems.item("shroomite"), 2, Items.GLOWSTONE, 4, 8, 6)),
            0.6f, 1.3f);

    // ------------------------------------------------------------------
    // Боссы (по одному на мир)
    // ------------------------------------------------------------------
    public static final EntityType<RpgBoss> BANDIT_KING = boss("bandit_king",
            MobConfig.builder(150, 9, 0.32).armor(6).knockback(0.3)
                    .sounds(SoundEvents.ZOMBIE_AMBIENT, SoundEvents.ZOMBIE_HURT, SoundEvents.ZOMBIE_DEATH),
            BossEvent.BossBarColor.RED, 0.7f, 2.6f);
    public static final EntityType<RpgBoss> PHARAOH = boss("pharaoh",
            MobConfig.builder(160, 8, 0.3).armor(6).rangedFireball().fireImmune()
                    .sounds(SoundEvents.ZOMBIE_AMBIENT, SoundEvents.ZOMBIE_HURT, SoundEvents.ZOMBIE_DEATH),
            BossEvent.BossBarColor.YELLOW, 0.7f, 2.5f);
    public static final EntityType<RpgBoss> YETI = boss("yeti",
            MobConfig.builder(180, 11, 0.28).armor(4).knockback(0.5).hitEffect(MobEffects.SLOWNESS, 6, 1)
                    .sounds(SoundEvents.ZOMBIE_AMBIENT, SoundEvents.ZOMBIE_HURT, SoundEvents.ZOMBIE_DEATH),
            BossEvent.BossBarColor.BLUE, 0.9f, 3.0f);
    public static final EntityType<RpgBoss> STONE_TITAN = boss("stone_titan",
            MobConfig.builder(220, 13, 0.22).armor(10).knockback(0.8)
                    .sounds(SoundEvents.IRON_GOLEM_DAMAGE, SoundEvents.IRON_GOLEM_HURT, SoundEvents.IRON_GOLEM_DEATH),
            BossEvent.BossBarColor.GREEN, 1.1f, 3.6f);
    public static final EntityType<RpgBoss> SUN_PREDATOR = boss("sun_predator",
            MobConfig.builder(150, 10, 0.38).armor(4).setTargetOnFire()
                    .sounds(SoundEvents.POLAR_BEAR_AMBIENT, SoundEvents.POLAR_BEAR_HURT, SoundEvents.POLAR_BEAR_DEATH),
            BossEvent.BossBarColor.WHITE, 0.9f, 2.6f);
    public static final EntityType<RpgBoss> ELDER_TREANT = boss("elder_treant",
            MobConfig.builder(200, 10, 0.2).armor(8).knockback(0.6)
                    .sounds(SoundEvents.ZOMBIE_AMBIENT, SoundEvents.ZOMBIE_HURT, SoundEvents.ZOMBIE_DEATH),
            BossEvent.BossBarColor.GREEN, 1.0f, 3.3f);
    public static final EntityType<RpgBoss> VINE_SERPENT = boss("vine_serpent",
            MobConfig.builder(170, 9, 0.32).armor(6).rangedArrow().hitEffect(MobEffects.POISON, 8, 0)
                    .sounds(SoundEvents.SPIDER_AMBIENT, SoundEvents.SPIDER_HURT, SoundEvents.SPIDER_DEATH),
            BossEvent.BossBarColor.GREEN, 0.8f, 2.8f);
    public static final EntityType<RpgBoss> BOG_HORROR = boss("bog_horror",
            MobConfig.builder(180, 10, 0.26).armor(6).rangedFireball().hitEffect(MobEffects.POISON, 8, 1)
                    .sounds(SoundEvents.WITCH_AMBIENT, SoundEvents.WITCH_HURT, SoundEvents.WITCH_DEATH),
            BossEvent.BossBarColor.PURPLE, 0.95f, 3.1f);
    public static final EntityType<RpgBoss> INFERNO_LORD = boss("inferno_lord",
            MobConfig.builder(230, 13, 0.3).armor(8).fireImmune().setTargetOnFire().rangedFireball()
                    .sounds(SoundEvents.BLAZE_AMBIENT, SoundEvents.BLAZE_HURT, SoundEvents.BLAZE_DEATH),
            BossEvent.BossBarColor.RED, 1.0f, 3.4f);
    public static final EntityType<RpgBoss> STORM_KING = boss("storm_king",
            MobConfig.builder(190, 10, 0.34).armor(6).rangedArrow()
                    .sounds(SoundEvents.SKELETON_AMBIENT, SoundEvents.SKELETON_HURT, SoundEvents.SKELETON_DEATH),
            BossEvent.BossBarColor.BLUE, 0.9f, 2.9f);
    public static final EntityType<RpgBoss> VOID_SOVEREIGN = boss("void_sovereign",
            MobConfig.builder(260, 14, 0.36).armor(8).knockback(0.4).hitEffect(MobEffects.WITHER, 6, 1).rangedFireball()
                    .sounds(SoundEvents.ENDERMAN_AMBIENT, SoundEvents.ENDERMAN_HURT, SoundEvents.ENDERMAN_DEATH),
            BossEvent.BossBarColor.PURPLE, 1.05f, 3.5f);
    public static final EntityType<RpgBoss> SPORE_MOTHER = boss("spore_mother",
            MobConfig.builder(170, 9, 0.24).armor(6).hitEffect(MobEffects.POISON, 10, 1).rangedFireball()
                    .sounds(SoundEvents.SLIME_ATTACK, SoundEvents.SLIME_HURT, SoundEvents.SLIME_DEATH),
            BossEvent.BossBarColor.PINK, 0.95f, 3.0f);

    // ------------------------------------------------------------------
    // Торговцы хаба
    // ------------------------------------------------------------------
    public static final EntityType<RpgTrader> HUNTER_TRADER = trader("hunter_trader", () -> List.of(
            entry(Items.EMERALD, 8, ModItems.item("game_meat"), 6, 12, 2),
            entry(Items.EMERALD, 14, ModItems.item("cooked_game_meat"), 8, 12, 3),
            entry(ModItems.item("adurite"), 3, Items.EMERALD, 1, 8, 4),
            entry(ModItems.item("jade"), 3, Items.EMERALD, 1, 8, 4),
            entry(Items.EMERALD, 20, ModItems.item("amber"), 1, 6, 5),
            entry(Items.EMERALD, 36, ModItems.item("hub_compass"), 1, 3, 8)),
            0.6f, 1.95f);
    public static final EntityType<RpgTrader> ALCHEMIST_TRADER = trader("alchemist_trader", () -> List.of(
            entry(Items.EMERALD, 5, Items.SPLASH_POTION, 1, 6, 4),
            entry(Items.EMERALD, 6, Items.LINGERING_POTION, 1, 6, 4),
            entry(Items.EMERALD, 4, Items.POTION, 1, 8, 3),
            entry(ModItems.item("frost_berry"), 6, ModItems.item("mystic_stew"), 2, 8, 4),
            entry(ModItems.item("shroomite"), 2, Items.EXPERIENCE_BOTTLE, 3, 8, 6)),
            0.6f, 1.95f);
    public static final EntityType<RpgTrader> BLACKSMITH_TRADER = trader("blacksmith_trader", () -> List.of(
            entry(ModItems.item("adurite"), 12, ModItems.item("adurite_sword"), 1, 4, 12),
            entry(ModItems.item("glacite"), 12, ModItems.item("glacite_pickaxe"), 1, 4, 12),
            entry(ModItems.item("infernite"), 14, ModItems.item("infernite_axe"), 1, 3, 15),
            entry(ModItems.item("nullite"), 16, ModItems.item("nullite_sword"), 1, 3, 18),
            entry(ModItems.item("titanite"), 18, ModItems.item("crown_of_worlds"), 1, 1, 30)),
            0.6f, 1.95f);

    // ------------------------------------------------------------------
    // Регистрация
    // ------------------------------------------------------------------
    public static void init() {
        MinecraftAdvanced.LOGGER.info("Зарегистрировано сущностей: {}", ALL.size());
    }

    private static RpgTrader.Entry entry(net.minecraft.world.item.Item cost, int costCount,
                                         net.minecraft.world.item.Item gives, int giveCount, int uses, int xp) {
        return new RpgTrader.Entry(cost, costCount, new ItemStack(gives, giveCount), uses, xp);
    }

    private static EntityType<RpgHumanoid> humanoid(String id, MobConfig.Builder builder, float w, float h) {
        MobConfig config = builder.build();
        return register(id, MobCategory.MONSTER,
                (type, level) -> new RpgHumanoid(type, level, config), w, h,
                fullStats(Monster.createMonsterAttributes(), config), config);
    }

    private static EntityType<RpgBoss> boss(String id, MobConfig.Builder builder, BossEvent.BossBarColor color,
                                            float w, float h) {
        MobConfig config = builder.build();
        return register(id, MobCategory.MONSTER,
                (type, level) -> new RpgBoss(type, level, config, color), w, h,
                fullStats(Monster.createMonsterAttributes(), config), config);
    }

    private static EntityType<RpgBeast> beast(String id, MobConfig.Builder builder, float w, float h) {
        MobConfig config = builder.build();
        return register(id, MobCategory.CREATURE,
                (type, level) -> new RpgBeast(type, level, config), w, h,
                Mob.createMobAttributes()
                        .add(Attributes.MAX_HEALTH, config.maxHealth())
                        .add(Attributes.MOVEMENT_SPEED, config.movementSpeed()), config);
    }

    private static EntityType<RpgSpider> spider(String id, MobConfig.Builder builder, float w, float h) {
        MobConfig config = builder.build();
        return register(id, MobCategory.MONSTER,
                (type, level) -> new RpgSpider(type, level, config), w, h,
                Spider.createAttributes()
                        .add(Attributes.MAX_HEALTH, config.maxHealth())
                        .add(Attributes.ATTACK_DAMAGE, config.attackDamage()), config);
    }

    private static EntityType<RpgSlime> slime(String id, MobConfig.Builder builder, float w, float h) {
        MobConfig config = builder.build();
        return register(id, MobCategory.MONSTER,
                (type, level) -> new RpgSlime(type, level, config), w, h,
                Mob.createMobAttributes()
                        .add(Attributes.MAX_HEALTH, config.maxHealth())
                        .add(Attributes.MOVEMENT_SPEED, config.movementSpeed())
                        .add(Attributes.ATTACK_DAMAGE, config.attackDamage()), config);
    }

    private static EntityType<RpgWisp> wisp(String id, MobConfig.Builder builder, float w, float h) {
        MobConfig config = builder.build();
        return register(id, MobCategory.AMBIENT,
                (type, level) -> new RpgWisp(type, level, config), w, h,
                Ghast.createAttributes()
                        .add(Attributes.MAX_HEALTH, config.maxHealth()), config);
    }

    private static EntityType<RpgTrader> trader(String id, Supplier<List<RpgTrader.Entry>> trades,
                                                float w, float h) {
        return register(id, MobCategory.CREATURE,
                (type, level) -> new RpgTrader(type, level,
                        Component.translatable("entity." + MOD_ID + "." + id), trades.get()),
                w, h, Mob.createMobAttributes()
                        .add(Attributes.MAX_HEALTH, 24.0)
                        .add(Attributes.MOVEMENT_SPEED, 0.25)
                        .add(Attributes.FOLLOW_RANGE, 16.0), null);
    }

    private static <T extends Mob> EntityType<T> register(String id, MobCategory category,
                                                          EntityType.EntityFactory<T> factory,
                                                          float w, float h,
                                                          AttributeSupplier.Builder attributes,
                                                          MobConfig config) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE,
                Identifier.fromNamespaceAndPath(MOD_ID, id));
        EntityType<T> type = Registry.register(BuiltInRegistries.ENTITY_TYPE, key.identifier(),
                EntityType.Builder.of(factory, category).sized(w, h).build(key));
        ALL.put(id, type);
        if (config != null) {
            CONFIGS.put(id, config);
        }
        if (attributes != null) {
            FabricDefaultAttributeRegistry.register(type, attributes);
        }
        return type;
    }

    private static AttributeSupplier.Builder fullStats(AttributeSupplier.Builder builder, MobConfig config) {
        return builder
                .add(Attributes.MAX_HEALTH, config.maxHealth())
                .add(Attributes.MOVEMENT_SPEED, config.movementSpeed())
                .add(Attributes.ATTACK_DAMAGE, config.attackDamage())
                .add(Attributes.ARMOR, config.armor())
                .add(Attributes.KNOCKBACK_RESISTANCE, config.knockbackResistance())
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    public static EntityType<?> byId(String id) {
        return ALL.get(id);
    }

    public static java.util.Collection<EntityType<?>> all() {
        return ALL.values();
    }

    private ModEntities() {
    }
}
