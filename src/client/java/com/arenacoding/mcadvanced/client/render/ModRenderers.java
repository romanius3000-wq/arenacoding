package com.arenacoding.mcadvanced.client.render;

import com.arenacoding.mcadvanced.MinecraftAdvanced;
import com.arenacoding.mcadvanced.registry.ModEntities;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;

import static com.arenacoding.mcadvanced.MinecraftAdvanced.MOD_ID;

/**
 * Регистрация всех рендеров и слоёв моделей.
 */
public final class ModRenderers {
    public static final ModelLayerLocation BEAST_LAYER = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(MOD_ID, "quadruped_beast"), "main");

    public static void init() {
        ModelLayerRegistry.registerModelLayer(BEAST_LAYER, QuadrupedBeastModel::createBodyLayer);

        // ---- обычные гуманоиды ----
        humanoid(ModEntities.BANDIT, "bandit", 1.0f);
        humanoid(ModEntities.MUMMY, "mummy", 1.0f);
        humanoid(ModEntities.FROST_REAVER, "frost_reaver", 1.0f);
        humanoid(ModEntities.ROCK_GOLEM, "rock_golem", 1.4f);
        humanoid(ModEntities.TREANT, "treant", 1.3f);
        humanoid(ModEntities.JUNGLE_BRUTE, "jungle_brute", 1.05f);
        humanoid(ModEntities.BOG_WITCH, "bog_witch", 1.0f);
        humanoid(ModEntities.MAGMA_BEAST, "magma_beast", 1.1f);
        humanoid(ModEntities.ASH_WRAITH, "ash_wraith", 1.0f);
        humanoid(ModEntities.PIRATE_GHOST, "pirate_ghost", 1.0f);
        humanoid(ModEntities.VOID_WRAITH, "void_wraith", 1.15f);
        humanoid(ModEntities.LION, "lion", 1.1f);

        // ---- звери ----
        beast(ModEntities.BULL, "bull");
        beast(ModEntities.FROST_STAG, "frost_stag");
        beast(ModEntities.ZEBRA, "zebra");

        // ---- пауки ----
        spider(ModEntities.SCORPION, "scorpion");
        spider(ModEntities.VINE_SPIDER, "vine_spider");
        spider(ModEntities.SHORE_CRAWLER, "shore_crawler");
        spider(ModEntities.NULL_CRAWLER, "null_crawler");

        // ---- слайм и дух ----
        EntityRendererRegistry.register(ModEntities.SPORE_SLIME,
                context -> new RpgSlimeRenderer(context, tex("spore_slime")));
        EntityRendererRegistry.register(ModEntities.FOREST_WISP,
                context -> new RpgWispRenderer(context, tex("forest_wisp"), 0.35f));

        // ---- торговцы ----
        humanoid(ModEntities.MUSHROOMLING, "mushroomling", 0.75f);
        humanoid(ModEntities.HUNTER_TRADER, "hunter_trader", 1.0f);
        humanoid(ModEntities.ALCHEMIST_TRADER, "alchemist_trader", 1.0f);
        humanoid(ModEntities.BLACKSMITH_TRADER, "blacksmith_trader", 1.0f);

        // ---- боссы ----
        humanoid(ModEntities.BANDIT_KING, "bandit_king", 1.25f);
        humanoid(ModEntities.PHARAOH, "pharaoh", 1.2f);
        humanoid(ModEntities.YETI, "yeti", 1.5f);
        humanoid(ModEntities.STONE_TITAN, "stone_titan", 1.8f);
        humanoid(ModEntities.SUN_PREDATOR, "sun_predator", 1.3f);
        humanoid(ModEntities.ELDER_TREANT, "elder_treant", 1.6f);
        humanoid(ModEntities.VINE_SERPENT, "vine_serpent", 1.45f);
        humanoid(ModEntities.BOG_HORROR, "bog_horror", 1.5f);
        humanoid(ModEntities.INFERNO_LORD, "inferno_lord", 1.7f);
        humanoid(ModEntities.STORM_KING, "storm_king", 1.45f);
        humanoid(ModEntities.VOID_SOVEREIGN, "void_sovereign", 1.75f);
        humanoid(ModEntities.SPORE_MOTHER, "spore_mother", 1.5f);

        MinecraftAdvanced.LOGGER.info("Рендеры зарегистрированы");
    }

    private static Identifier tex(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, "textures/entity/" + path + ".png");
    }

    private static void humanoid(net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.PathfinderMob> type,
                                 String texture, float scale) {
        EntityRendererRegistry.register(type,
                context -> new RpgHumanoidRenderer<>(context, tex(texture), scale));
    }

    private static void beast(net.minecraft.world.entity.EntityType<? extends com.arenacoding.mcadvanced.entity.RpgBeast> type,
                              String texture) {
        EntityRendererRegistry.register(type,
                context -> new RpgBeastRenderer(context, tex(texture)));
    }

    private static void spider(net.minecraft.world.entity.EntityType<? extends com.arenacoding.mcadvanced.entity.RpgSpider> type,
                               String texture) {
        EntityRendererRegistry.register(type,
                context -> new RpgSpiderRenderer(context, tex(texture)));
    }

    private ModRenderers() {
    }
}
