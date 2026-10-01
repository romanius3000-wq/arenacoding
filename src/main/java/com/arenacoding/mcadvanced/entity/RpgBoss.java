package com.arenacoding.mcadvanced.entity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.level.Level;

import java.util.UUID;

/**
 * Босс: боссбар над головой, не деспавнится, богатый лут (см. loot tables).
 */
public class RpgBoss extends RpgHumanoid {
    private final ServerBossEvent bossBar;

    public RpgBoss(EntityType<? extends Monster> type, Level level, MobConfig config,
                   BossEvent.BossBarColor barColor) {
        super(type, level, config);
        this.bossBar = new ServerBossEvent(UUID.randomUUID(), this.getDisplayName(),
                barColor, BossEvent.BossBarOverlay.NOTCHED_10);
        this.setPersistenceRequired();
        this.xpReward = 60;
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossBar.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossBar.removePlayer(player);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide()) {
            this.bossBar.setProgress(this.getHealth() / this.getMaxHealth());
        }
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        this.bossBar.removeAllPlayers();
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public void checkDespawn() {
        // Боссы никогда не деспавнятся
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WITHER_DEATH;
    }
}
