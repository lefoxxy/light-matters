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

- `Wood Torch`: a weaker early-game placed light for basic survival.
- `Iron`, `Gold`, `Diamond`, `Netherite`, and `Creative` lanterns can all be placed directly into the world.
- Better lantern tiers create stronger placed light, so base-building and cave routes improve alongside carried-light progression.
- Placed lantern blocks do not consume fuel; fuel only matters while the lantern is carried as an item.
- Placed lantern tiers now also differentiate through particles and ambience, while keeping close to Minecraft-style lantern visuals.

## Next Good Steps

- Expand world-light progression with more fuel-aware placed lights, braziers, or camp lamps.
- Balance outdoor darkness across weather, dawn, dusk, moonlight, and dimension-specific rules.
- Give `Panic` and `Fatigue` even more mechanical depth, such as recovery items, safe-zone bonuses, or interaction penalties.
