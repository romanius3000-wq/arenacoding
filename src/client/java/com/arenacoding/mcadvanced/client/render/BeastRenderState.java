package com.arenacoding.mcadvanced.client.render;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

/**
 * Стейт зверя: фаза атаки и возраст для idle-анимаций.
 */
public class BeastRenderState extends LivingEntityRenderState {
    public float attackTime;
    public float ageInTicks;
}
