package com.arenacoding.mcadvanced.client.render;

import com.arenacoding.mcadvanced.entity.RpgSpider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

/**
 * Рендер паучьих мобов (скорпионы, ползуны) на ванильской модели паука.
 */
public class RpgSpiderRenderer extends SpiderRenderer<RpgSpider> {
    private final Identifier texture;

    public RpgSpiderRenderer(EntityRendererProvider.Context context, Identifier texture) {
        super(context);
        this.texture = texture;
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return this.texture;
    }
}
