package com.arenacoding.mcadvanced.client;

import com.arenacoding.mcadvanced.client.render.ModRenderers;
import com.arenacoding.mcadvanced.client.screen.WorldSelectorScreen;
import com.arenacoding.mcadvanced.network.ModNetworking;
import com.arenacoding.mcadvanced.util.AbilityManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import static com.arenacoding.mcadvanced.MinecraftAdvanced.MOD_ID;

/**
 * Клиентская часть: рендеры мобов, кейбинды (K — выбор мира,
 * R — рывок, V — перекат), GUI выбора мира.
 */
public class MinecraftAdvancedClient implements ClientModInitializer {
    public static KeyMapping OPEN_SELECTOR_KEY;
    public static KeyMapping DASH_KEY;
    public static KeyMapping ROLL_KEY;

    /** Своя категория в настройках управления. */
    public static final KeyMapping.Category MCADVANCED_CATEGORY =
            new KeyMapping.Category(Identifier.fromNamespaceAndPath(MOD_ID, "abilities"));

    @Override
    public void onInitializeClient() {
        ModRenderers.init();

        OPEN_SELECTOR_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.mcadvanced.open_selector", GLFW.GLFW_KEY_K, MCADVANCED_CATEGORY));
        DASH_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.mcadvanced.dash", GLFW.GLFW_KEY_R, MCADVANCED_CATEGORY));
        ROLL_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.mcadvanced.roll", GLFW.GLFW_KEY_V, MCADVANCED_CATEGORY));

        // сервер прислал «открой выбор мира» (ПКМ по предмету)
        ClientPlayNetworking.registerGlobalReceiver(ModNetworking.OpenSelectorPayload.TYPE,
                (payload, ctx) -> ctx.client().execute(
                        () -> Minecraft.getInstance().setScreenAndShow(new WorldSelectorScreen())));

        // кейбинды
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_SELECTOR_KEY.consumeClick()) {
                client.setScreenAndShow(new WorldSelectorScreen());
            }
            while (DASH_KEY.consumeClick()) {
                ClientPlayNetworking.send(new ModNetworking.UseAbilityPayload(AbilityManager.DASH));
            }
            while (ROLL_KEY.consumeClick()) {
                ClientPlayNetworking.send(new ModNetworking.UseAbilityPayload(AbilityManager.ROLL));
            }
        });
    }
}
