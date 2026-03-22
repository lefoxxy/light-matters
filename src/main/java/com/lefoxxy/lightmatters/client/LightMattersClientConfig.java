package com.lefoxxy.lightmatters.client;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.neoforged.fml.loading.FMLPaths;

public final class LightMattersClientConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("lightmatters-client.json");

    private static boolean hudEnabled = true;
    private static float hudScale = 0.85F;
    private static HudAnchor hudAnchor = HudAnchor.TOP_LEFT;
    private static int hudOffsetX = 8;
    private static int hudOffsetY = 8;

    private LightMattersClientConfig() {
    }

    public static void load() {
        resetDefaults();
        if (!Files.exists(CONFIG_PATH)) {
            save();
            return;
        }

        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            hudEnabled = getBoolean(root, "hudEnabled", hudEnabled);
            hudScale = clampScale(getFloat(root, "hudScale", hudScale));
            hudAnchor = HudAnchor.fromName(getString(root, "hudAnchor", hudAnchor.name()));
            hudOffsetX = clampOffset(getInt(root, "hudOffsetX", hudOffsetX));
            hudOffsetY = clampOffset(getInt(root, "hudOffsetY", hudOffsetY));
        } catch (Exception ignored) {
            save();
        }
    }

    public static void save() {
        JsonObject root = new JsonObject();
        root.addProperty("hudEnabled", hudEnabled);
        root.addProperty("hudScale", hudScale);
        root.addProperty("hudAnchor", hudAnchor.name());
        root.addProperty("hudOffsetX", hudOffsetX);
        root.addProperty("hudOffsetY", hudOffsetY);

        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(root, writer);
            }
        } catch (IOException ignored) {
        }
    }

    public static void resetDefaults() {
        hudEnabled = true;
        hudScale = 0.85F;
        hudAnchor = HudAnchor.TOP_LEFT;
        hudOffsetX = 8;
        hudOffsetY = 8;
    }

    public static boolean isHudEnabled() {
        return hudEnabled;
    }

    public static void setHudEnabled(boolean value) {
        hudEnabled = value;
    }

    public static float getHudScale() {
        return hudScale;
    }

    public static void setHudScale(float value) {
        hudScale = clampScale(value);
    }

    public static HudAnchor getHudAnchor() {
        return hudAnchor;
    }

    public static void setHudAnchor(HudAnchor value) {
        hudAnchor = value;
    }

    public static int getHudOffsetX() {
        return hudOffsetX;
    }

    public static void setHudOffsetX(int value) {
        hudOffsetX = clampOffset(value);
    }

    public static int getHudOffsetY() {
        return hudOffsetY;
    }

    public static void setHudOffsetY(int value) {
        hudOffsetY = clampOffset(value);
    }

    private static boolean getBoolean(JsonObject root, String key, boolean fallback) {
        return root.has(key) ? root.get(key).getAsBoolean() : fallback;
    }

    private static int getInt(JsonObject root, String key, int fallback) {
        return root.has(key) ? root.get(key).getAsInt() : fallback;
    }

    private static float getFloat(JsonObject root, String key, float fallback) {
        return root.has(key) ? root.get(key).getAsFloat() : fallback;
    }

    private static String getString(JsonObject root, String key, String fallback) {
        return root.has(key) ? root.get(key).getAsString() : fallback;
    }

    private static float clampScale(float value) {
        return Math.clamp(value, 0.6F, 1.5F);
    }

    private static int clampOffset(int value) {
        return Math.clamp(value, 0, 60);
    }

    public enum HudAnchor {
        TOP_LEFT("Top Left"),
        TOP_RIGHT("Top Right"),
        BOTTOM_LEFT("Bottom Left"),
        BOTTOM_RIGHT("Bottom Right");

        private final String label;

        HudAnchor(String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }

        public HudAnchor next() {
            HudAnchor[] values = values();
            return values[(ordinal() + 1) % values.length];
        }

        public static HudAnchor fromName(String name) {
            for (HudAnchor value : values()) {
                if (value.name().equalsIgnoreCase(name)) {
                    return value;
                }
            }

            return TOP_LEFT;
        }
    }
}
