package com.arenacoding.mcadvanced.client.screen;

import com.arenacoding.mcadvanced.network.ModNetworking;
import com.arenacoding.mcadvanced.util.ModWorlds;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Экран выбора мира: 12 миров + Главная Комната.
 */
public class WorldSelectorScreen extends Screen {

    public WorldSelectorScreen() {
        super(Component.translatable("gui.mcadvanced.selector.title"));
    }

    @Override
    protected void init() {
        int cols = 3;
        int bw = 165;
        int bh = 20;
        int gapX = 10;
        int gapY = 6;
        int totalW = cols * bw + (cols - 1) * gapX;
        int startX = (this.width - totalW) / 2;
        int startY = 64;

        for (int idx = 0; idx < ModWorlds.count(); idx++) {
            final int i = idx;
            int col = i % cols;
            int row = i / cols;
            ModWorlds.WorldInfo info = ModWorlds.byIndex(i);
            addRenderableWidget(Button.builder(info.displayName(), button -> select(i))
                    .pos(startX + col * (bw + gapX), startY + row * (bh + gapY))
                    .size(bw, bh)
                    .build());
        }

        int y = startY + 4 * (bh + gapY) + 10;
        addRenderableWidget(Button.builder(
                        Component.translatable("gui.mcadvanced.selector.hub"), button -> select(-1))
                .pos(this.width / 2 - bw - 5, y).size(bw, bh).build());
        addRenderableWidget(Button.builder(
                        Component.translatable("gui.mcadvanced.selector.close"), button -> onClose())
                .pos(this.width / 2 + 5, y).size(bw, bh).build());
    }

    private void select(int worldIndex) {
        ClientPlayNetworking.send(new ModNetworking.SelectWorldPayload(worldIndex));
        if (this.minecraft != null) {
            this.minecraft.gui.setScreen(null);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0xC0101828);
        graphics.fill(0, 40, this.width, this.height - 30, 0x90101828);
        graphics.centeredText(this.font, this.title, this.width / 2, 22, 0xFFD080);
        graphics.centeredText(this.font,
                Component.translatable("gui.mcadvanced.selector.hint"), this.width / 2, 36, 0x9AB8C8);
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
