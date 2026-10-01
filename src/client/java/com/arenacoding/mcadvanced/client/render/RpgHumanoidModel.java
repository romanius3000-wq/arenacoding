package com.arenacoding.mcadvanced.client.render;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

/**
 * Модель гуманоидных мобов с живыми анимациями:
 * дыхание в покое, боевая стойка при агрессии, выпад корпусом при атаке.
 * Базовая ходьба и замах руками остаются ванильными (super.setupAnim).
 */
public class RpgHumanoidModel extends HumanoidModel<RpgHumanoidRenderState> {

    public RpgHumanoidModel(ModelPart root) {
        super(root);
    }

    @Override
    public void setupAnim(RpgHumanoidRenderState state) {
        super.setupAnim(state);

        float age = state.ageInTicks;
        float attack = state.attackTime;

        // --- дыхание в покое: лёгкое покачивание тела и рук ---
        float breathe = Mth.sin(age * 0.09f) * 0.015f;
        this.body.xRot += breathe;
        this.head.yRot += Mth.sin(age * 0.05f) * 0.03f;

        // --- боевая стойка: руки чуть подняты, корпус наклонён ---
        if (state.isAggressive && attack <= 0.0f) {
            this.rightArm.xRot = Mth.lerp(0.35f, this.rightArm.xRot, -0.9f);
            this.leftArm.xRot = Mth.lerp(0.35f, this.leftArm.xRot, -0.6f);
            this.body.xRot += 0.08f;
        }

        // --- выпад при атаке: резкий замах обеими руками + наклон корпуса ---
        if (attack > 0.0f) {
            float swing = Mth.sin(attack * (float) Math.PI);
            this.rightArm.xRot -= swing * 1.4f;
            this.leftArm.xRot -= swing * 0.8f;
            this.body.xRot += swing * 0.35f;
            this.head.xRot -= swing * 0.2f;
        }

        // --- шаг: лёгкий размах рук при ходьбе (усиливаем ваниль) ---
        float speed = state.walkAnimationSpeed;
        if (speed > 0.01f && attack <= 0.0f) {
            float stride = Mth.clamp(speed, 0, 1);
            this.rightArm.xRot -= stride * 0.35f;
            this.leftArm.xRot -= stride * 0.35f;
        }
    }
}
