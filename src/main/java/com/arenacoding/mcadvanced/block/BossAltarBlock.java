package com.arenacoding.mcadvanced.block;

import com.arenacoding.mcadvanced.item.BossSummonItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Алтарь боссов: используйте на нём предмет призыва босса.
 */
public class BossAltarBlock extends Block {

    public BossAltarBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                          Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.getItem() instanceof BossSummonItem summonItem) {
            if (!level.isClientSide()) {
                if (summonItem.trySummonBoss(level, pos.above().above(), player, stack)) {
                    level.playSound(null, pos, SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.0f, 0.6f);
                    player.sendOverlayMessage(Component.translatable("message.mcadvanced.boss_summoned"));
                }
            }
            return InteractionResult.SUCCESS;
        }
        player.sendOverlayMessage(Component.translatable("message.mcadvanced.altar_needed"));
        return InteractionResult.PASS;
    }
}
