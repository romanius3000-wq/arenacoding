package com.arenacoding.mcadvanced.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.client.renderer.entity.state.SlimeRenderState;
import net.minecraft.resources.Identifier;

/**
 * Рендер спорового слайма.
 */
public class RpgSlimeRenderer extends SlimeRenderer {
    private final Identifier texture;

    public RpgSlimeRenderer(EntityRendererProvider.Context context, Identifier texture) {
        super(context);
        this.texture = texture;
    }

    @Override
    public Identifier getTextureLocation(SlimeRenderState state) {
        return this.texture;
    }
}
