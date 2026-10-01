package com.arenacoding.mcadvanced.util;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Способности игрока: рывок и перекат с кулдаунами.
 */
public final class AbilityManager {
    public static final int DASH = 0;
    public static final int ROLL = 1;

    private static final long DASH_COOLDOWN_MS = 3000L;
    private static final long ROLL_COOLDOWN_MS = 5000L;

    private static final Map<UUID, Long> DASH_READY = new HashMap<>();
    private static final Map<UUID, Long> ROLL_READY = new HashMap<>();

    public static void use(int ability, ServerPlayer player) {
        if (player.isSpectator() || !player.isAlive()) {
            return;
        }
        switch (ability) {
            case DASH -> dash(player);
            case ROLL -> roll(player);
        }
    }

    /** Рывок: мощный толчок в направлении взгляда. */
    private static void dash(ServerPlayer player) {
        long now = System.currentTimeMillis();
        long ready = DASH_READY.getOrDefault(player.getUUID(), 0L);
        if (now < ready) {
            cooldown(player, ready - now);
            return;
        }
        DASH_READY.put(player.getUUID(), now + DASH_COOLDOWN_MS);

        Vec3 look = player.getLookAngle();
        double dy = Mth.clamp(look.y * 0.4 + 0.18, -0.1, 0.35);
        Vec3 push = new Vec3(look.x, dy, look.z).normalize().scale(1.65);
        player.addDeltaMovement(push);
        player.hurtMarked = true;

        ServerLevel level = player.level();
        level.sendParticles(ParticleTypes.CLOUD,
                player.getX(), player.getY() + 0.3, player.getZ(), 14, 0.3, 0.2, 0.3, 0.02);
        level.sendParticles(ParticleTypes.GUST,
                player.getX(), player.getY() + 0.8, player.getZ(), 1, 0, 0, 0, 0);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PHANTOM_SWOOP, SoundSource.PLAYERS, 0.7f, 1.3f);
        player.sendSystemMessage(Component.translatable("message.mcadvanced.dash"), true);
    }

    /** Перекат: короткий рывок вбок/вперёд с неуязвимостью. */
    private static void roll(ServerPlayer player) {
        long now = System.currentTimeMillis();
        long ready = ROLL_READY.getOrDefault(player.getUUID(), 0L);
        if (now < ready) {
            cooldown(player, ready - now);
            return;
        }
        ROLL_READY.put(player.getUUID(), now + ROLL_COOLDOWN_MS);

        // направление: куда смотрит игрок (горизонталь)
        Vec3 look = player.getLookAngle();
        Vec3 dir = new Vec3(look.x, 0, look.z);
        if (dir.lengthSqr() < 1.0e-4) {
            dir = new Vec3(0, 0, 1);
        }
        dir = dir.normalize();
        player.addDeltaMovement(dir.scale(1.35).add(0, 0.22, 0));
        player.hurtMarked = true;
        // короткое окно неуязвимости (~0.6 сек)
        player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 12, 4, false, false));

        ServerLevel level = player.level();
        level.sendParticles(ParticleTypes.POOF,
                player.getX(), player.getY() + 0.4, player.getZ(), 10, 0.3, 0.25, 0.3, 0.05);
        level.sendParticles(ParticleTypes.CRIT,
                player.getX(), player.getY() + 0.5, player.getZ(), 8, 0.35, 0.3, 0.35, 0.1);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.9f, 0.7f);
        player.sendSystemMessage(Component.translatable("message.mcadvanced.roll"), true);
    }

    private static void cooldown(ServerPlayer player, long msLeft) {
        long sec = msLeft / 1000L + 1L;
        player.sendSystemMessage(
                Component.translatable("message.mcadvanced.ability_cooldown", sec), true);
    }

    private AbilityManager() {
    }
}
