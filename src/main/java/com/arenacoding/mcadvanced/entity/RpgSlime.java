package com.arenacoding.mcadvanced.entity;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.cubemob.Slime;
import net.minecraft.world.level.Level;

/**
 * Споровый слайм: прыгучий слизень мистической рощи.
 */
public class RpgSlime extends Slime {
    protected final MobConfig config;

    public RpgSlime(EntityType<? extends Slime> type, Level level, MobConfig config) {
        super(type, level);
        this.config = config;
        this.setSize(1 + this.random.nextInt(2), true);
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
