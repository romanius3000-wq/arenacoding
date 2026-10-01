package com.arenacoding.mcadvanced.item;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import com.arenacoding.mcadvanced.MinecraftAdvanced;

/**
 * Артефакт-посох босса: стреляет магическими снарядами.
 */
public class ArtifactStaffItem extends Item {
    private static final int COOLDOWN_TICKS = 25;

    public ArtifactStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level instanceof ServerLevel serverLevel) {
            if (player.getCooldowns().isOnCooldown(stack)) {
                return InteractionResult.FAIL;
            }
            Vec3 look = player.getLookAngle().normalize().scale(0.9);
            Vec3 eye = player.getEyePosition();
            SmallFireball projectile = new SmallFireball(serverLevel, player, look);
            projectile.setPos(eye.x + look.x, eye.y + look.y * 0.8, eye.z + look.z);
            serverLevel.addFreshEntity(projectile);
            serverLevel.playSound(null, player.blockPosition(),
                    SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0f, 1.2f);
            player.getCooldowns().addCooldown(
                    Identifier.fromNamespaceAndPath(MinecraftAdvanced.MOD_ID, "artifact_staff"), COOLDOWN_TICKS);
            player.awardStat(net.minecraft.stats.Stats.ITEM_USED.get(this));
        }
        return InteractionResult.SUCCESS;
    }
}
