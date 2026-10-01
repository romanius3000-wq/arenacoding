package com.arenacoding.mcadvanced.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;

/**
 * Паучьи мобы: скорпионы, лиановые пауки и т.п.
 * Умеют лазать по стенам (наследие паука) и отравлять.
 */
public class RpgSpider extends Spider {
    protected final MobConfig config;

    public RpgSpider(EntityType<? extends Spider> type, Level level, MobConfig config) {
        super(type, level);
        this.config = config;
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        boolean hurt = super.doHurtTarget(level, target);
        if (hurt && target instanceof LivingEntity living && this.config.hitEffect() != null) {
            living.addEffect(new MobEffectInstance(this.config.hitEffect(),
                    this.config.hitEffectSeconds() * 20, this.config.hitEffectAmplifier()));
        }
        return hurt;
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
