package com.arenacoding.mcadvanced.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.PathfinderMob;

/**
 * Универсальный рендер гуманоидных мобов, боссов и торговцев:
 * своя текстура + масштаб (боссы крупнее) + расширенные анимации.
 */
public class RpgHumanoidRenderer<T extends PathfinderMob> extends HumanoidMobRenderer<T, RpgHumanoidRenderState, RpgHumanoidModel> {
    private final Identifier texture;
    private final float scale;

    public RpgHumanoidRenderer(EntityRendererProvider.Context context, Identifier texture, float scale) {
        super(context,
                new RpgHumanoidModel(context.bakeLayer(ModelLayers.ZOMBIE)),
                0.5f * scale);
        this.texture = texture;
        this.scale = scale;
        this.model.hat.visible = false;
    }

    @Override
    public RpgHumanoidRenderState createRenderState() {
        return new RpgHumanoidRenderState();
    }

    @Override
    public void extractRenderState(T entity, RpgHumanoidRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.ageInTicks = entity.tickCount + partialTick;
        state.isAggressive = entity.getTarget() != null;
    }

    @Override
    public Identifier getTextureLocation(RpgHumanoidRenderState state) {
        return this.texture;
    }

    @Override
    protected void scale(RpgHumanoidRenderState state, PoseStack poseStack) {
        poseStack.scale(this.scale, this.scale, this.scale);
    }
}
