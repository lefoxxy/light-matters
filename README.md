# Light Matters

`Light Matters` is a NeoForge mod prototype that turns darkness into a survival mechanic rather than a simple mob-spawning rule. Light level, time of day, weather, and carried equipment all shape how safe the world feels, pushing players to plan around illumination instead of treating it as background detail.

## Overview

The current prototype focuses on three core ideas:

- Darkness should create meaningful pressure during exploration, especially at night and underground.
- Portable light should be a progression system, not a cosmetic convenience.
- World lighting should support survival play by rewarding deliberate base lighting and route planning.

## Current Features

- Darkness is sampled from nearby block light, time-of-day-adjusted skylight, and personal lantern light.
- Outdoor darkness builds gradually at sunset, remains threatening through the night, fades at dawn, and intensifies during bad weather.
- Low light applies visual pressure and the custom `Fatigue` effect.
- Extended pitch-black exposure builds into the custom `Panic` effect with lingering recovery behavior.
- `Panic` and `Fatigue` include custom feedback such as heartbeat and breathing cues, particles, and camera pressure.
- A client-side vignette reinforces worsening ambient light conditions.
- `/lightmatters debug` reports live values for block light, raw skylight, outdoor penalty, personal light, and exposure.

## Lantern System

`Light Matters` currently includes six lantern tiers:

- `Wood Lantern`
- `Iron Lantern`
- `Gold Lantern`
- `Diamond Lantern`
- `Netherite Lantern`
- `Creative Lantern`

Survival lanterns can be toggled on and off, consume fuel while carried and lit, and can be refueled with coal or charcoal held in the opposite hand. Lantern items can also be placed directly into the world as their matching lantern blocks. Placed lanterns do not consume fuel.

Higher lantern tiers provide stronger personal light and better efficiency, making them more reliable for long expeditions and late-game exploration.

## World Lighting

Vanilla torches still provide useful protection, but their effective coverage is intentionally weaker than lanterns so that lantern progression remains a meaningful upgrade path. Stronger lantern tiers also improve world lighting for shelters, caves, and travel routes by placing more dependable light sources into the environment.

## Development

This project is built with NeoForge for Minecraft `1.21.1`.

To compile the mod:

```powershell
.\gradlew.bat compileJava processResources
```

To launch the development client:

```powershell
.\gradlew.bat runClient
```
