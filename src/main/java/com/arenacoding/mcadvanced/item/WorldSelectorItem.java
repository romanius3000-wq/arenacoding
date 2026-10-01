package com.arenacoding.mcadvanced.item;

import com.arenacoding.mcadvanced.network.ModNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

/**
 * «Выбор мира»: ПКМ — открыть меню телепортации по 12 мирам.
 */
public class WorldSelectorItem extends Item {

    public WorldSelectorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level instanceof ServerLevel && player instanceof ServerPlayer serverPlayer) {
            ModNetworking.sendOpenSelector(serverPlayer);
        }
        return InteractionResult.SUCCESS;
    }
}
