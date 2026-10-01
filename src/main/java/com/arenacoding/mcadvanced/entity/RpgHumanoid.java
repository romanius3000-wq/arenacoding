package com.arenacoding.mcadvanced.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Базовый «гуманоидный» моб: ближний бой (или стрелки/фаерболы),
 * эффекты при ударе, настраиваемые звуки.
 */
public class RpgHumanoid extends Monster implements RangedAttackMob {
    protected final MobConfig config;

    public RpgHumanoid(EntityType<? extends Monster> type, Level level, MobConfig config) {
        super(type, level);
        this.config = config;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        if (this.config.rangedArrow() || this.config.rangedFireball()) {
            this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0, 30, 14.0f));
        } else {
            this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
        }
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        boolean hurt = super.doHurtTarget(level, target);
        if (hurt && target instanceof LivingEntity living) {
            if (this.config.hitEffect() != null) {
                living.addEffect(new MobEffectInstance(this.config.hitEffect(),
                        this.config.hitEffectSeconds() * 20, this.config.hitEffectAmplifier()));
            }
            if (this.config.setTargetOnFire()) {
                target.igniteForSeconds(5.0f);
            }
        }
        return hurt;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        if (this.config.rangedFireball()) {
            Vec3 dir = target.getEyePosition().subtract(this.getEyePosition()).normalize().scale(0.7);
            SmallFireball fireball = new SmallFireball(this.level(), this, dir);
            fireball.setPos(this.getEyePosition().x, this.getEyePosition().y, this.getEyePosition().z);
            this.level().addFreshEntity(fireball);
        } else {
            Arrow arrow = new Arrow(this.level(), this, new ItemStack(Items.ARROW), ItemStack.EMPTY);
            arrow.shootFromRotation(this, this.getXRot(), this.getYRot(), 0.0f, power * 2.0f, 1.0f);
            this.level().addFreshEntity(arrow);
        }
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

    public MobConfig config() {
        return this.config;
    }

    public boolean isHostile() {
        return this instanceof Enemy;
    }
}
