package com.arenacoding.mcadvanced.entity;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;

/**
 * Конфиг моба: статы, звуки, эффекты при ударе.
 * Позволяет создавать десятки уникальных мобов из нескольких классов.
 */
public record MobConfig(
        double maxHealth,
        double attackDamage,
        double movementSpeed,
        double knockbackResistance,
        double armor,
        SoundEvent ambientSound,
        SoundEvent hurtSound,
        SoundEvent deathSound,
        boolean rangedArrow,
        boolean rangedFireball,
        Holder<MobEffect> hitEffect,
        int hitEffectSeconds,
        int hitEffectAmplifier,
        boolean setTargetOnFire,
        boolean fireImmune) {

    public static Builder builder(double hp, double dmg, double speed) {
        return new Builder(hp, dmg, speed);
    }

    public static final class Builder {
        private final double hp, dmg, speed;
        private double knockback = 0.0;
        private double armor = 0.0;
        private SoundEvent ambient, hurt, death;
        private boolean rangedArrow, rangedFireball, setTargetOnFire, fireImmune;
        private Holder<MobEffect> effect;
        private int effectSeconds = 5, effectAmplifier = 0;

        private Builder(double hp, double dmg, double speed) {
            this.hp = hp;
            this.dmg = dmg;
            this.speed = speed;
        }

        public Builder knockback(double v) { this.knockback = v; return this; }
        public Builder armor(double v) { this.armor = v; return this; }
        public Builder sounds(SoundEvent ambient, SoundEvent hurt, SoundEvent death) {
            this.ambient = ambient; this.hurt = hurt; this.death = death; return this;
        }
        public Builder rangedArrow() { this.rangedArrow = true; return this; }
        public Builder rangedFireball() { this.rangedFireball = true; return this; }
        public Builder setTargetOnFire() { this.setTargetOnFire = true; return this; }
        public Builder fireImmune() { this.fireImmune = true; return this; }
        public Builder hitEffect(Holder<MobEffect> effect, int seconds, int amplifier) {
            this.effect = effect; this.effectSeconds = seconds; this.effectAmplifier = amplifier; return this;
        }

        public MobConfig build() {
            return new MobConfig(hp, dmg, speed, knockback, armor, ambient, hurt, death,
                    rangedArrow, rangedFireball, effect, effectSeconds, effectAmplifier,
                    setTargetOnFire, fireImmune);
        }
    }
}
