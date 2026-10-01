package com.arenacoding.mcadvanced.block;

import com.arenacoding.mcadvanced.util.ModWorlds;
import com.arenacoding.mcadvanced.util.TeleportUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Портал-площадка в один из 12 миров.
 * Встаньте на неё — и вы уже в другом мире!
 */
public class WorldPortalBlock extends Block {
    private final ModWorlds.WorldInfo world;

    public WorldPortalBlock(Properties properties, ModWorlds.WorldInfo world) {
        super(properties);
        this.world = world;
    }

    public ModWorlds.WorldInfo world() {
        return this.world;
    }

    public ResourceKey<Level> destination() {
        return this.world.dimension();
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (level instanceof ServerLevel serverLevel && entity instanceof net.minecraft.server.level.ServerPlayer player) {
            long now = serverLevel.getGameTime();
            if (!TeleportUtil.onCooldown(player, now)) {
                TeleportUtil.markCooldown(player, now);
                TeleportUtil.teleport(player, this.world.dimension(),
                        Component.translatable("message.mcadvanced.teleported", this.world.displayName()));
            }
        }
    }
}
