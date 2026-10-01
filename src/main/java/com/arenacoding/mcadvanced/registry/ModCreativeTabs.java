package com.arenacoding.mcadvanced.registry;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import java.util.List;

import static com.arenacoding.mcadvanced.MinecraftAdvanced.MOD_ID;

/**
 * Раскладываем предметы мода по ванильным креативным вкладкам.
 */
public final class ModCreativeTabs {

    public static void init() {
        modify("building_blocks", ModItems.TAB_BLOCKS);
        modify("natural_blocks", ModItems.TAB_NATURAL);
        modify("tools_and_utilities", ModItems.TAB_TOOLS);
        modify("combat", ModItems.TAB_COMBAT);
        modify("food_and_drinks", ModItems.TAB_FOOD);
        modify("ingredients", ModItems.TAB_INGREDIENTS);
        modify("spawn_eggs", ModItems.TAB_EGGS);
    }

    private static void modify(String tabId, List<Item> items) {
        ResourceKey<CreativeModeTab> key = ResourceKey.create(Registries.CREATIVE_MODE_TAB,
                Identifier.fromNamespaceAndPath("minecraft", tabId));
        CreativeModeTabEvents.modifyOutputEvent(key).register(output -> {
            for (Item item : items) {
                output.accept(item);
            }
        });
    }

    private ModCreativeTabs() {
    }
}
