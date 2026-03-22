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
- Recovery consumables now give players active counterplay through `Calming Tea` and `Miner's Tonic`.
- `Panic` and `Fatigue` include custom feedback such as heartbeat and breathing cues, particles, and camera pressure.
- A client-side vignette and compact darkness meter reinforce worsening ambient light conditions.
- The HUD can be disabled, resized, and repositioned from the in-game options menu.
- `/lightmatters debug` reports live values for block light, raw skylight, outdoor penalty, personal light, and exposure.

## Lantern System

`Light Matters` currently includes five lantern tiers:

- `Wood Lantern`
- `Iron Lantern`
- `Gold Lantern`
- `Diamond Lantern`
- `Netherite Lantern`

Survival lanterns can be toggled on and off, consume fuel while carried and lit, and can be refueled with coal or charcoal held in the opposite hand. Lantern items can also be placed directly into the world as their matching lantern blocks. Placed lanterns do not consume fuel.

Higher lantern tiers provide stronger personal light and better efficiency, making them more reliable for long expeditions and late-game exploration.

## World Lighting

Vanilla torches still provide useful protection, but their effective coverage is intentionally weaker than lanterns so that lantern progression remains a meaningful upgrade path. Stronger lantern tiers also improve world lighting for shelters, caves, and travel routes by placing more dependable light sources into the environment.

## Recovery Items

- `Calming Tea` reduces pitch-black exposure and helps settle panic.
- `Miner's Tonic` shortens fatigue recovery and restores focus during longer expeditions.

These items are meant to add player-controlled recovery tools without replacing the need for reliable light.

## HUD Options

The compact darkness HUD is configurable from the vanilla Options screen through the `Light Matters HUD` button.

- Enable or disable the HUD entirely.
- Resize it.
- Move it to any screen corner.
- Adjust horizontal and vertical offset.
- Reset to the default layout.

## Development

This project is built with NeoForge for Minecraft `1.21.1`.
