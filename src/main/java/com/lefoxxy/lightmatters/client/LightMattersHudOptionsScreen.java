package com.lefoxxy.lightmatters.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class LightMattersHudOptionsScreen extends Screen {
    private static final float[] SCALE_STEPS = {0.6F, 0.75F, 0.85F, 1.0F, 1.15F, 1.3F, 1.5F};
    private static final int[] OFFSET_STEPS = {0, 8, 16, 24, 32, 40, 48, 60};

    private final Screen parent;
    private Button enabledButton;
    private Button scaleButton;
    private Button anchorButton;
    private Button xOffsetButton;
    private Button yOffsetButton;

    public LightMattersHudOptionsScreen(Screen parent) {
        super(Component.literal("Light Matters HUD"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int y = height / 4;

        enabledButton = addRenderableWidget(Button.builder(Component.empty(), button -> {
            LightMattersClientConfig.setHudEnabled(!LightMattersClientConfig.isHudEnabled());
            LightMattersClientConfig.save();
            refreshLabels();
        }).bounds(centerX - 100, y, 200, 20).build());

        scaleButton = addRenderableWidget(Button.builder(Component.empty(), button -> {
            LightMattersClientConfig.setHudScale(nextScale(LightMattersClientConfig.getHudScale()));
            LightMattersClientConfig.save();
            refreshLabels();
        }).bounds(centerX - 100, y + 24, 200, 20).build());

        anchorButton = addRenderableWidget(Button.builder(Component.empty(), button -> {
            LightMattersClientConfig.setHudAnchor(LightMattersClientConfig.getHudAnchor().next());
            LightMattersClientConfig.save();
            refreshLabels();
        }).bounds(centerX - 100, y + 48, 200, 20).build());

        xOffsetButton = addRenderableWidget(Button.builder(Component.empty(), button -> {
            LightMattersClientConfig.setHudOffsetX(nextOffset(LightMattersClientConfig.getHudOffsetX()));
            LightMattersClientConfig.save();
            refreshLabels();
        }).bounds(centerX - 100, y + 72, 200, 20).build());

        yOffsetButton = addRenderableWidget(Button.builder(Component.empty(), button -> {
            LightMattersClientConfig.setHudOffsetY(nextOffset(LightMattersClientConfig.getHudOffsetY()));
            LightMattersClientConfig.save();
            refreshLabels();
        }).bounds(centerX - 100, y + 96, 200, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Reset Defaults"), button -> {
            LightMattersClientConfig.resetDefaults();
            LightMattersClientConfig.save();
            refreshLabels();
        }).bounds(centerX - 100, y + 128, 98, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Done"), button -> onClose())
                .bounds(centerX + 2, y + 128, 98, 20)
                .build());

        refreshLabels();
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(font, title, width / 2, 20, 0xF3E3C2);
        guiGraphics.drawCenteredString(font, Component.literal("Compact HUD controls"), width / 2, 34, 0xB9AA90);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void refreshLabels() {
        enabledButton.setMessage(Component.literal("HUD: " + (LightMattersClientConfig.isHudEnabled() ? "Enabled" : "Disabled")));
        scaleButton.setMessage(Component.literal("Scale: " + Math.round(LightMattersClientConfig.getHudScale() * 100.0F) + "%"));
        anchorButton.setMessage(Component.literal("Anchor: " + LightMattersClientConfig.getHudAnchor().label()));
        xOffsetButton.setMessage(Component.literal("Horizontal Offset: " + LightMattersClientConfig.getHudOffsetX()));
        yOffsetButton.setMessage(Component.literal("Vertical Offset: " + LightMattersClientConfig.getHudOffsetY()));
    }

    private static float nextScale(float current) {
        for (float value : SCALE_STEPS) {
            if (value > current + 0.01F) {
                return value;
            }
        }

        return SCALE_STEPS[0];
    }

    private static int nextOffset(int current) {
        for (int value : OFFSET_STEPS) {
            if (value > current) {
                return value;
            }
        }

        return OFFSET_STEPS[0];
    }
}
