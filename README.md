# Light Matters

`Light Matters` is a NeoForge mod prototype where darkness is a real survival system instead of just a mob-spawn check.

## Current Prototype

- Darkness is sampled from block light, time-of-day-adjusted skylight, and personal lantern light around the player.
- Outdoor darkness now ramps in progressively as the sun sets, stays threatening through the night, fades by dawn, and gets stronger during bad weather.
- Low light applies vision pressure and the custom `Fatigue` effect.
- Prolonged pitch-black exposure builds into the custom `Panic` effect with lingering recovery instead of snapping off instantly.
- A client HUD vignette deepens as ambient light gets worse.
- `Panic` and `Fatigue` now add custom feedback like heartbeat/breath audio, particles, and camera pressure.
- `/lightmatters debug` reports live block light, raw skylight, outdoor penalty, personal light, and exposure values.

## Lantern Progression

- `Wood Lantern`: early-game portable light with low brightness and weak efficiency.
- `Iron Lantern`: sturdier mid-tier lantern with better burn time.
- `Gold Lantern`: brighter lantern with improved fuel economy.
- `Diamond Lantern`: high-brightness lantern built for deep expeditions.
- `Netherite Lantern`: strongest survival lantern with the best efficiency.
- `Creative Lantern`: infinite-fuel lantern with extremely high personal light for testing or creative play.

All survival lanterns can be toggled on and off, consume fuel while carried and lit, and can be refueled with coal or charcoal while fuel is held in the other hand. Lantern items can also be placed directly as their matching world lantern blocks.

## World Lighting

- Vanilla torches still help, but their useful protection is intentionally weaker so lanterns become the better long-term lighting investment.
- `Iron`, `Gold`, `Diamond`, `Netherite`, and `Creative` lanterns can all be placed directly into the world.
- Better lantern tiers create stronger placed light, so base-building and cave routes improve alongside carried-light progression.
- Placed lantern blocks do not consume fuel; fuel only matters while the lantern is carried as an item.
- Placed lantern tiers now also differentiate through particles and ambience, while keeping close to Minecraft-style lantern visuals.

## Recommended Roadmap

### Survival Systems

- Add recovery items and safe-zone mechanics for `Panic` and `Fatigue`, such as tea, campfires, bedrolls, or lit shelters.
- Add darkness-specific interaction penalties like reduced bow steadiness, slower tool handling, or more fragile combat timing.
- Introduce player adaptation or resistance progression so prepared players can push deeper into darkness.

### Light Progression

- Expand placed-light progression with braziers, oil lamps, miner lamps, camp lanterns, and permanent base lights.
- Add more meaningful fuel types with tradeoffs, such as oil, resin, coal bricks, and rare long-burn fuels.
- Make lantern tiers visually and mechanically distinct through sound, particles, durability of placement, and special perks.

### World Balance

- Keep tuning outdoor darkness across weather, dawn, dusk, moonlight, biome exposure, and dimension-specific rules.
- Give caves, forests, storms, and the Deep Dark more distinct darkness behavior so different places feel different, not just darker.
- Add mob or world interactions that respond to light quality, not only brightness level.

### Atmosphere And UX

- Add stronger custom sounds, heartbeat layers, breathing variations, lantern hums, and extinguish/refuel feedback.
- Add a clearer HUD or status meter for darkness pressure, recovery, and current light safety.
- Add config options for thresholds, accessibility, and effect intensity so balancing is easier.
