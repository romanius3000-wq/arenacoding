package com.arenacoding.mcadvanced.client.render;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * Модель четвероногого зверя (бык, олень, зебра, лев).
 * Анимации: ходьба (4 ноги), покачивание головы, фырканье в покое,
 * выпад головой при атаке, наклон корпуса на бегу.
 * Раскладка UV 64x64 совпадает с генератором текстур (tools/gen_textures.py).
 */
public class QuadrupedBeastModel extends EntityModel<BeastRenderState> {
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart legFrontLeft;
    private final ModelPart legFrontRight;
    private final ModelPart legBackLeft;
    private final ModelPart legBackRight;

    public QuadrupedBeastModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.legFrontLeft = root.getChild("leg_front_left");
        this.legFrontRight = root.getChild("leg_front_right");
        this.legBackLeft = root.getChild("leg_back_left");
        this.legBackRight = root.getChild("leg_back_right");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 8).addBox(-3.0f, -3.5f, -6.0f, 6, 6, 6),
                PartPose.offset(0.0f, 12.0f, -8.0f));
        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 20).addBox(-4.0f, -8.0f, -7.0f, 8, 8, 14),
                PartPose.offset(0.0f, 14.0f, 0.0f));
        root.addOrReplaceChild("leg_front_left",
                CubeListBuilder.create().texOffs(48, 0).addBox(-1.5f, 0.0f, -1.5f, 3, 10, 3),
                PartPose.offset(2.5f, 14.0f, -5.0f));
        root.addOrReplaceChild("leg_front_right",
                CubeListBuilder.create().texOffs(48, 16).addBox(-1.5f, 0.0f, -1.5f, 3, 10, 3),
                PartPose.offset(-2.5f, 14.0f, -5.0f));
        root.addOrReplaceChild("leg_back_left",
                CubeListBuilder.create().texOffs(48, 32).addBox(-1.5f, 0.0f, -1.5f, 3, 10, 3),
                PartPose.offset(2.5f, 14.0f, 5.0f));
        root.addOrReplaceChild("leg_back_right",
                CubeListBuilder.create().texOffs(48, 48).addBox(-1.5f, 0.0f, -1.5f, 3, 10, 3),
                PartPose.offset(-2.5f, 14.0f, 5.0f));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(BeastRenderState state) {
        float age = state.ageInTicks;
        float attack = state.attackTime;

        // --- голова: взгляд + покачивание при ходьбе + фырканье в покое ---
        this.head.yRot = (state.yRot - state.bodyRot) * Mth.DEG_TO_RAD;
        this.head.xRot = state.xRot * Mth.DEG_TO_RAD * 0.6f;

        float speed = Mth.clamp(state.walkAnimationSpeed, 0.0f, 1.0f);
        this.head.xRot += Mth.cos(state.walkAnimationPos * 0.6662f) * 0.8f * speed;

        if (speed < 0.05f) {
            // idle: голова опускается и поднимается (принюхивание)
            this.head.xRot += Mth.sin(age * 0.06f) * 0.12f - 0.05f;
            this.head.yRot += Mth.sin(age * 0.04f) * 0.06f;
        }

        // --- атака: выпад головой вперёд-вниз ---
        if (attack > 0.0f) {
            float lunge = Mth.sin(attack * (float) Math.PI);
            this.head.xRot += lunge * 0.9f;
            this.body.xRot = lunge * 0.18f;
        } else {
            this.body.xRot = 0.0f;
            // корпус слегка качается при беге
            this.body.zRot = Mth.cos(state.walkAnimationPos * 0.3331f) * 0.06f * speed;
        }

        // --- ноги: диагональный галоп ---
        float swing = state.walkAnimationPos * 0.6662f;
        float amount = speed * 1.4f;
        this.legFrontRight.xRot = Mth.cos(swing) * amount;
        this.legFrontLeft.xRot = Mth.cos(swing + Mth.PI) * amount;
        this.legBackRight.xRot = Mth.cos(swing + Mth.PI) * amount;
        this.legBackLeft.xRot = Mth.cos(swing) * amount;
    }
}
