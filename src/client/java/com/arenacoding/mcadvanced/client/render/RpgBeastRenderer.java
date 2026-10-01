package com.arenacoding.mcadvanced.client.render;

import com.arenacoding.mcadvanced.entity.RpgBeast;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

/**
 * Рендер четвероногих зверей (бык, олень, зебра, лев)
 * с атакой и idle-анимациями.
 */
public class RpgBeastRenderer extends MobRenderer<RpgBeast, BeastRenderState, QuadrupedBeastModel> {
    private final Identifier texture;

    public RpgBeastRenderer(EntityRendererProvider.Context context, Identifier texture) {
        super(context, new QuadrupedBeastModel(context.bakeLayer(ModRenderers.BEAST_LAYER)), 0.7f);
        this.texture = texture;
    }

    @Override
    public BeastRenderState createRenderState() {
        return new BeastRenderState();
    }

    @Override
    public void extractRenderState(RpgBeast entity, BeastRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.attackTime = entity.getAttackAnim(partialTick);
        state.ageInTicks = entity.tickCount + partialTick;
    }

    @Override
    public Identifier getTextureLocation(BeastRenderState state) {
        return this.texture;
    }
}
