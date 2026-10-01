package com.arenacoding.mcadvanced;

import com.arenacoding.mcadvanced.network.ModNetworking;
import com.arenacoding.mcadvanced.registry.ModBlocks;
import com.arenacoding.mcadvanced.registry.ModCreativeTabs;
import com.arenacoding.mcadvanced.registry.ModEntities;
import com.arenacoding.mcadvanced.registry.ModItems;
import com.arenacoding.mcadvanced.util.ModWorlds;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Minecraft Advanced — RPG-мод: 12 миров, боссы, уникальные мобы,
 * новые жители и торги, новые блоки, предметы и инструменты.
 */
public class MinecraftAdvanced implements ModInitializer {
    public static final String MOD_ID = "mcadvanced";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("[Minecraft Advanced] Инициализация: {} миров, {} мобов, {} боссов",
                ModWorlds.count(), 22 + 3, 12);
        ModWorlds.init();
        ModBlocks.init();
        ModEntities.init();
        ModItems.init();
        ModCreativeTabs.init();
        ModNetworking.init();
        LOGGER.info("[Minecraft Advanced] Готово! Удачи в приключениях.");
    }
}
