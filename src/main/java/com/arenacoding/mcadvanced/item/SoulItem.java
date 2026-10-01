package com.arenacoding.mcadvanced.item;

import com.arenacoding.mcadvanced.MinecraftAdvanced;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

/**
 * Душа побеждённого босса. ПКМ — поглотить: +2 к максимальному здоровью
 * (накапливается!). Или скрафтить из 12 душ Корону Миров.
 */
public class SoulItem extends Item {
    private static final double HEALTH_PER_SOUL = 2.0;

    public SoulItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level instanceof ServerLevel serverLevel) {
            AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
            if (maxHealth == null) {
                return InteractionResult.FAIL;
            }
            Identifier modifierId = Identifier.fromNamespaceAndPath(MinecraftAdvanced.MOD_ID, "soul_health");
            AttributeModifier existing = maxHealth.getModifier(modifierId);
            double amount = (existing == null ? 0.0 : existing.amount()) + HEALTH_PER_SOUL;
            maxHealth.addOrReplacePermanentModifier(new AttributeModifier(
                    modifierId, amount, AttributeModifier.Operation.ADD_VALUE));

            player.heal((float) HEALTH_PER_SOUL);
            player.getItemInHand(hand).shrink(1);
            serverLevel.playSound(null, player.blockPosition(),
                    SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.8f, 1.4f);
            player.sendOverlayMessage(
                    net.minecraft.network.chat.Component.translatable("message.mcadvanced.soul"));
        }
        return InteractionResult.SUCCESS;
    }
}
