# Light Matters

`Light Matters` is a NeoForge mod prototype where darkness is a real survival system instead of just a mob-spawn check.

## Current Prototype

- Darkness is sampled from block light, reduced skylight, and personal lantern light around the player.
- Outdoor darkness matters more now, especially at night and during rain, so torches and placed lighting stay relevant above ground.
- Low light applies vision pressure and the custom `Fatigue` effect.
- Prolonged pitch-black exposure builds into the custom `Panic` effect.
- A client HUD vignette deepens as ambient light gets worse.
- `/lightmatters debug` reports live block, skylight, personal light, and exposure values.

## Lantern Progression

- `Wood Lantern`: early-game portable light with low brightness and weak efficiency.
- `Iron Lantern`: sturdier mid-tier lantern with better burn time.
- `Gold Lantern`: brighter lantern with improved fuel economy.
- `Diamond Lantern`: high-brightness lantern built for deep expeditions.
- `Netherite Lantern`: strongest survival lantern with the best efficiency.
- `Creative Lantern`: infinite-fuel lantern with extremely high personal light for testing or creative play.

All survival lanterns can be toggled on and off, consume fuel while lit, and can be refueled with coal or charcoal while fuel is held in the other hand.

## Next Good Steps

- Add torch and placed-light progression so world lighting supports the same survival loop as carried lanterns.
- Add custom sounds, particles, and stronger visual differentiation between lantern tiers.
- Give `Panic` and `Fatigue` more bespoke gameplay hooks beyond attribute modifiers, such as aim sway, heartbeat, or recovery windows.
- Balance outdoor skylight penalties across weather, dawn, dusk, and dimension-specific rules.
