package com.arenacoding.mcadvanced.client.render;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

/**
 * Расширенный стейт гуманоидов: возраст в тиках (для idle-анимаций)
 * и признак агрессии (стойка боя).
 */
public class RpgHumanoidRenderState extends HumanoidRenderState {
    public float ageInTicks;
    public boolean isAggressive;
}
