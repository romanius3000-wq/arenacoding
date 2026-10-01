package com.arenacoding.mcadvanced.util;

import com.arenacoding.mcadvanced.MinecraftAdvanced;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Телепортация между 12 мирами и Хабом + защита от спама.
 */
public final class TeleportUtil {
    private static final Map<UUID, Long> COOLDOWNS = new HashMap<>();
    private static final long COOLDOWN_TICKS = 40L; // 2 секунды

    public static boolean onCooldown(ServerPlayer player, long now) {
        Long until = COOLDOWNS.get(player.getUUID());
        return until != null && now < until;
    }

    public static void markCooldown(ServerPlayer player, long now) {
        COOLDOWNS.put(player.getUUID(), now + COOLDOWN_TICKS);
    }

    /** Главная Комната — точка спавна. */
    public static Vec3 hubSpawn() {
        return new Vec3(0.5, 65.0, 0.5);
    }

    /** Телепорт в измерение с безопасной позицией. */
    public static void teleport(ServerPlayer player, ResourceKey<Level> destination, Component message) {
        MinecraftServer server = player.level().getServer();
        if (server == null) {
            return;
        }
        ServerLevel target = server.getLevel(destination);
        if (target == null) {
            MinecraftAdvanced.LOGGER.warn("Измерение {} не найдено!", destination.identifier());
            return;
        }
        Vec3 pos = destination == ModWorlds.HUB ? hubSpawn() : findSafeSpawn(target);
        player.teleport(new net.minecraft.world.level.portal.TeleportTransition(
                target, pos, Vec3.ZERO, 180.0f, 0.0f,
                net.minecraft.world.level.portal.TeleportTransition.PLAY_PORTAL_SOUND));
        if (message != null) {
            player.sendSystemMessage(message, true);
        }
    }

    /** Ищем безопасную позицию: верхушка мира, с ручным сканом для «крытых» миров. */
    public static Vec3 findSafeSpawn(ServerLevel level) {
        int x = 8, z = 8;
        int y;
        if (level.dimensionType().hasCeiling()) {
            // Незер-подобные: ищем воздушный карман с 32 до 100
            y = -1;
            BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
            for (int ty = 100; ty > 32; ty--) {
                cursor.set(x, ty, z);
                if (level.getBlockState(cursor).isSolidRender()) {
                    y = ty + 1;
                    break;
                }
            }
            if (y < 0) {
                y = 70;
            }
        } else {
            y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) + 1;
            if (y <= level.getMinY() + 1) {
                // пустота (острова Бездны) — ставим стек из камня и возвращаемся на него
                BlockPos pillar = new BlockPos(x, 64, z);
                for (int i = 0; i < 4; i++) {
                    level.setBlockAndUpdate(pillar.below(i), net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());
                }
                y = 65;
            }
        }
        return new Vec3(x + 0.5, y + 0.1, z + 0.5);
    }

    private TeleportUtil() {
    }
}
