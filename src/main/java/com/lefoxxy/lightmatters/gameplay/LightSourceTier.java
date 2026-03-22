package com.lefoxxy.lightmatters.gameplay;

public enum LightSourceTier {
    NONE(0, 0),
    EMBER(4, 45),
    TORCH(8, 120),
    LANTERN(12, 300);

    private final int targetLight;
    private final int fuelSecondsPerUnit;

    LightSourceTier(int targetLight, int fuelSecondsPerUnit) {
        this.targetLight = targetLight;
        this.fuelSecondsPerUnit = fuelSecondsPerUnit;
    }

    public int targetLight() {
        return targetLight;
    }

    public int fuelSecondsPerUnit() {
        return fuelSecondsPerUnit;
    }

    public static LightSourceTier fromAmbientLight(int effectiveLight) {
        if (effectiveLight >= LANTERN.targetLight) {
            return LANTERN;
        }

        if (effectiveLight >= TORCH.targetLight) {
            return TORCH;
        }

        if (effectiveLight >= EMBER.targetLight) {
            return EMBER;
        }

        return NONE;
    }
}
