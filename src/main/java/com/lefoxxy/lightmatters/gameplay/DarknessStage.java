package com.lefoxxy.lightmatters.gameplay;

public enum DarknessStage {
    BRIGHT(1.0F, 0.0F, -1, -1),
    GLOOM(0.85F, 0.12F, 0, -1),
    DARK(0.65F, 0.24F, 1, 0),
    PITCH_BLACK(0.4F, 0.42F, 2, 1);

    private final float miningSpeedMultiplier;
    private final float overlayAlpha;
    private final int darknessAmplifier;
    private final int weaknessAmplifier;

    DarknessStage(float miningSpeedMultiplier, float overlayAlpha, int darknessAmplifier, int weaknessAmplifier) {
        this.miningSpeedMultiplier = miningSpeedMultiplier;
        this.overlayAlpha = overlayAlpha;
        this.darknessAmplifier = darknessAmplifier;
        this.weaknessAmplifier = weaknessAmplifier;
    }

    public float miningSpeedMultiplier() {
        return miningSpeedMultiplier;
    }

    public float overlayAlpha() {
        return overlayAlpha;
    }

    public int darknessAmplifier() {
        return darknessAmplifier;
    }

    public int weaknessAmplifier() {
        return weaknessAmplifier;
    }

    public boolean appliesDarknessEffect() {
        return darknessAmplifier >= 0;
    }

    public boolean appliesWeakness() {
        return weaknessAmplifier >= 0;
    }

    public boolean isSevere() {
        return this == PITCH_BLACK;
    }

    public static DarknessStage fromEffectiveLight(int effectiveLight) {
        if (effectiveLight >= 10) {
            return BRIGHT;
        }

        if (effectiveLight >= 6) {
            return GLOOM;
        }

        if (effectiveLight >= 3) {
            return DARK;
        }

        return PITCH_BLACK;
    }
}
