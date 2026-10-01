package com.arenacoding.mcadvanced.client.render;

import com.arenacoding.mcadvanced.entity.RpgWisp;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.GhastRenderer;
import net.minecraft.client.renderer.entity.state.GhastRenderState;
import net.minecraft.resources.Identifier;

/**
 * Рендер лесного духа: маленький светящийся гаст.
 */
public class RpgWispRenderer extends GhastRenderer {
    private final Identifier texture;
    private final float scale;

    public RpgWispRenderer(EntityRendererProvider.Context context, Identifier texture, float scale) {
        super(context);
        this.texture = texture;
        this.scale = scale;
    }

    @Override
    public Identifier getTextureLocation(GhastRenderState state) {
        return this.texture;
    }

    @Override
    protected void scale(GhastRenderState state, PoseStack poseStack) {
        poseStack.scale(this.scale, this.scale, this.scale);
    }
}
