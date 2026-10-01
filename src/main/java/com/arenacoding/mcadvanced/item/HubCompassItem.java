package com.arenacoding.mcadvanced.item;

import com.arenacoding.mcadvanced.util.ModWorlds;
import com.arenacoding.mcadvanced.util.TeleportUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

/**
 * Компас хаба: ПКМ — мгновенное возвращение в Главную Комнату.
 */
public class HubCompassItem extends Item {

    public HubCompassItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            long now = serverLevel.getGameTime();
            if (TeleportUtil.onCooldown(serverPlayer, now)) {
                serverPlayer.sendSystemMessage(Component.translatable("message.mcadvanced.cooldown"), true);
                return InteractionResult.FAIL;
            }
            TeleportUtil.markCooldown(serverPlayer, now);
            serverLevel.playSound(null, player.blockPosition(),
                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
            TeleportUtil.teleport(serverPlayer, ModWorlds.HUB,
                    Component.translatable("message.mcadvanced.hub"));
        }
        return InteractionResult.SUCCESS;
    }
}
