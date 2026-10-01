package com.arenacoding.mcadvanced.network;

import com.arenacoding.mcadvanced.MinecraftAdvanced;
import com.arenacoding.mcadvanced.entity.RpgTrader;
import com.arenacoding.mcadvanced.item.HubCompassItem;
import com.arenacoding.mcadvanced.item.WorldSelectorItem;
import com.arenacoding.mcadvanced.registry.ModEntities;
import com.arenacoding.mcadvanced.registry.ModItems;
import com.arenacoding.mcadvanced.util.AbilityManager;
import com.arenacoding.mcadvanced.util.ModWorlds;
import com.arenacoding.mcadvanced.util.TeleportUtil;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

import java.util.List;

import static com.arenacoding.mcadvanced.MinecraftAdvanced.MOD_ID;

/**
 * Сеть: выбор мира из GUI (C2S), открытие GUI (S2C), стартовые предметы.
 */
public final class ModNetworking {

    /** C2S: игрок выбрал мир (index 0..11, -1 = хаб). */
    public record SelectWorldPayload(int worldIndex) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<SelectWorldPayload> TYPE =
                new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(MOD_ID, "select_world"));
        public static final StreamCodec<RegistryFriendlyByteBuf, SelectWorldPayload> CODEC =
                StreamCodec.composite(ByteBufCodecs.VAR_INT, SelectWorldPayload::worldIndex, SelectWorldPayload::new);

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /** S2C: сервер просит клиент открыть экран выбора мира. */
    public record OpenSelectorPayload() implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<OpenSelectorPayload> TYPE =
                new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(MOD_ID, "open_selector"));
        public static final StreamCodec<RegistryFriendlyByteBuf, OpenSelectorPayload> CODEC =
                StreamCodec.unit(new OpenSelectorPayload());

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /** C2S: игрок использует способность (0 = рывок, 1 = перекат). */
    public record UseAbilityPayload(int ability) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<UseAbilityPayload> TYPE =
                new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(MOD_ID, "use_ability"));
        public static final StreamCodec<RegistryFriendlyByteBuf, UseAbilityPayload> CODEC =
                StreamCodec.composite(ByteBufCodecs.VAR_INT, UseAbilityPayload::ability, UseAbilityPayload::new);

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void init() {
        PayloadTypeRegistry.serverboundPlay().register(SelectWorldPayload.TYPE, SelectWorldPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(OpenSelectorPayload.TYPE, OpenSelectorPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(UseAbilityPayload.TYPE, UseAbilityPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(UseAbilityPayload.TYPE, (payload, ctx) ->
                ctx.server().execute(() -> AbilityManager.use(payload.ability(), ctx.player())));

        ServerPlayNetworking.registerGlobalReceiver(SelectWorldPayload.TYPE, (payload, ctx) -> {
            MinecraftServer server = ctx.server();
            server.execute(() -> {
                ServerPlayer player = ctx.player();
                long now = player.level().getGameTime();
                if (TeleportUtil.onCooldown(player, now)) {
                    player.sendSystemMessage(Component.translatable("message.mcadvanced.cooldown"), true);
                    return;
                }
                TeleportUtil.markCooldown(player, now);
                if (payload.worldIndex() < 0) {
                    TeleportUtil.teleport(player, ModWorlds.HUB,
                            Component.translatable("message.mcadvanced.hub"));
                } else {
                    ModWorlds.WorldInfo info = ModWorlds.byIndex(payload.worldIndex());
                    TeleportUtil.teleport(player, info.dimension(),
                            Component.translatable("message.mcadvanced.teleported", info.displayName()));
                }
            });
        });

        // стартовые предметы + приветствие
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> server.execute(() -> {
            ServerPlayer player = handler.player;
            boolean hasSelector = false;
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                if (player.getInventory().getItem(i).is(ModItems.WORLD_SELECTOR)) {
                    hasSelector = true;
                    break;
                }
            }
            if (!hasSelector) {
                player.getInventory().add(new ItemStack(ModItems.WORLD_SELECTOR));
                player.getInventory().add(new ItemStack(ModItems.HUB_COMPASS));
                player.sendSystemMessage(Component.translatable("message.mcadvanced.welcome"), false);
            }
            // торговцы в хабе
            ServerLevel hub = server.getLevel(ModWorlds.HUB);
            if (hub != null) {
                HubManager.ensureTraders(hub);
            }
        }));
    }

    public static void sendOpenSelector(ServerPlayer player) {
        ServerPlayNetworking.send(player, new OpenSelectorPayload());
    }

    /** Спавнит трёх торговцев в хабе, если их там нет. */
    public static final class HubManager {
        private static final double ROOM = 90.0;

        public static void ensureTraders(ServerLevel hub) {
            List<RpgTrader> existing = hub.getEntitiesOfClass(RpgTrader.class,
                    new AABB(-ROOM, 60, -ROOM, ROOM, 90, ROOM));
            if (existing.size() >= 3) {
                return;
            }
            spawn(hub, ModEntities.HUNTER_TRADER, -20, -20, 135.0f);
            spawn(hub, ModEntities.ALCHEMIST_TRADER, 20, -20, 225.0f);
            spawn(hub, ModEntities.BLACKSMITH_TRADER, -20, 20, 45.0f);
        }

        private static void spawn(ServerLevel hub, net.minecraft.world.entity.EntityType<? extends RpgTrader> type,
                                  int x, int z, float yaw) {
            if (hub.getEntitiesOfClass(RpgTrader.class, new AABB(x - 2, 64, z - 2, x + 2, 75, z + 2)).isEmpty()) {
                RpgTrader trader = type.create(hub, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
                if (trader != null) {
                    trader.setPos(x + 0.5, 65.0, z + 0.5);
                    trader.setYRot(yaw);
                    trader.setXRot(0.0f);
                    trader.setPersistenceRequired();
                    hub.addFreshEntity(trader);
                }
            }
        }
    }

    private ModNetworking() {
    }
}
