package com.arenacoding.mcadvanced.entity;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Мирный парящий дух леса. Не стреляет — просто светится и наблюдает.
 */
public class RpgWisp extends Ghast {
    protected final MobConfig config;

    public RpgWisp(EntityType<? extends Ghast> type, Level level, MobConfig config) {
        super(type, level);
        this.config = config;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 16.0f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.config.ambientSound();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return this.config.hurtSound();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.config.deathSound();
    }
}
