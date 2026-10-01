package com.arenacoding.mcadvanced.item;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.function.Supplier;

/**
 * Предмет призыва босса. Используйте на Алтаре боссов (boss_altar).
 */
public class BossSummonItem extends Item {
    private final Supplier<EntityType<? extends Mob>> bossType;

    public BossSummonItem(Properties properties, Supplier<EntityType<? extends Mob>> bossType) {
        super(properties);
        this.bossType = bossType;
    }

    public boolean trySummonBoss(Level level, BlockPos pos, Player player, ItemStack stack) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        // не спавним, если рядом уже есть босс
        var nearby = serverLevel.getEntitiesOfClass(Entity.class,
                new AABB(pos).inflate(24.0), e -> e.getType() == this.bossType.get());
        if (!nearby.isEmpty()) {
            return false;
        }
        Mob boss = this.bossType.get().create(serverLevel, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
        if (boss == null) {
            return false;
        }
        boss.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        boss.setYRot(player.getYRot() + 180.0f);
        boss.setXRot(0.0f);
        boss.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(pos),
                net.minecraft.world.entity.EntitySpawnReason.TRIGGERED, null);
        if (serverLevel.addFreshEntity(boss)) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            serverLevel.playSound(null, pos, SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.0f, 0.7f);
            return true;
        }
        return false;
    }

    @Override
    public InteractionResult use(Level level, Player player, net.minecraft.world.InteractionHand hand) {
        if (!level.isClientSide()) {
            player.sendOverlayMessage(
                    net.minecraft.network.chat.Component.translatable("message.mcadvanced.altar_needed"));
        }
        return InteractionResult.PASS;
    }
}
