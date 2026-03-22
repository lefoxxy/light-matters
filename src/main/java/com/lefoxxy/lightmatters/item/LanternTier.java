package com.lefoxxy.lightmatters.item;

import net.minecraft.ChatFormatting;

public enum LanternTier {
    WOOD("wood_lantern", "Wood Lantern", 10, 90, 3, ChatFormatting.DARK_RED),
    IRON("iron_lantern", "Iron Lantern", 12, 140, 4, ChatFormatting.GRAY),
    GOLD("gold_lantern", "Gold Lantern", 13, 200, 4, ChatFormatting.GOLD),
    DIAMOND("diamond_lantern", "Diamond Lantern", 14, 280, 5, ChatFormatting.AQUA),
    NETHERITE("netherite_lantern", "Netherite Lantern", 15, 360, 6, ChatFormatting.DARK_GRAY);

    private final String itemName;
    private final String displayName;
    private final int personalLight;
    private final int fuelSecondsPerCoal;
    private final int maxCoalCharges;
    private final ChatFormatting nameColor;

    LanternTier(String itemName, String displayName, int personalLight, int fuelSecondsPerCoal, int maxCoalCharges, ChatFormatting nameColor) {
        this.itemName = itemName;
        this.displayName = displayName;
        this.personalLight = personalLight;
        this.fuelSecondsPerCoal = fuelSecondsPerCoal;
        this.maxCoalCharges = maxCoalCharges;
        this.nameColor = nameColor;
    }

    public String itemName() {
        return itemName;
    }

    public String displayName() {
        return displayName;
    }

    public int personalLight() {
        return personalLight;
    }

    public int fuelSecondsPerCoal() {
        return fuelSecondsPerCoal;
    }

    public int maxCoalCharges() {
        return maxCoalCharges;
    }

    public ChatFormatting nameColor() {
        return nameColor;
    }

    public int fuelPerCoalTicks() {
        return fuelSecondsPerCoal * 20;
    }

    public int maxFuelTicks() {
        return fuelPerCoalTicks() * maxCoalCharges;
    }
}
