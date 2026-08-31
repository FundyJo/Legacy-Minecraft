# MINIMEGA Migration

## Source of Truth

The only authoritative Minimega implementation used by this migration is:

FundyJo/Minimega
https://github.com/FundyJo/Minimega

No other Minimega repository or decompiled reconstruction is used as a source of truth.

## Phase 2 status

- IN PROGRESS – FundyJo/Minimega resources imported, Stage-1/Stage-2 validation refreshed.
- No secondary Minimega source-of-truth dependency remains in this migration document.

## Stage 1 re-validation against FundyJo/Minimega

Validated against repository snapshot:
`FundyJo/Minimega@92d9e44fa48b25bea02984894e3b0ba60a467b7a`

### confirmed from FundyJo/Minimega

- `wily.legacy.minigame.Minigame`
  - IDs and names confirmed from `src/main/java/dev/jab125/minimega/mod/util/Minigame.java`
  - `NONE=0`, `BATTLE=1`, `TUMBLE=2`, `GLIDE=3`, `FISTFIGHT=70`, `LOBBY=99`
- `wily.legacy.minigame.data.*`
  - `MapInfo`, `MapData`, `MapVariant`, `MapVariants`, `BattleVariants`, `NormalVariants` remain source-backed by `src/main/java/dev/jab125/minimega/mod/data/*`
- `wily.legacy.minigame.config.BattleConfig`
- `wily.legacy.minigame.config.GlideConfig`
- `wily.legacy.minigame.config.MinigameConfigCodecs`
- `wily.legacy.minigame.config.MinigameSpecificConfig`
- `wily.legacy.minigame.config.NoConfig`
- `wily.legacy.minigame.config.battle.BattleConfigSettings`
- `wily.legacy.minigame.config.battle.PreconfiguredBattleConfigSettings`
- `wily.legacy.minigame.config.battle.CasualBattleConfigSettings`
- `wily.legacy.minigame.config.battle.CompetitiveBattleConfigSettings`
- `wily.legacy.minigame.config.battle.Lives`

### BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY

The following upstream files currently contain only `// INTERNAL ERROR //` in `FundyJo/Minimega`, so direct value/codec verification is blocked:

- `dev/jab125/minimega/mod/util/controller/glide/GlideGameType.java`
- `dev/jab125/minimega/mod/util/minigamedata/battle/RoundLength.java`
- `dev/jab125/minimega/mod/util/minigamedata/battle/MapSize.java`
- `dev/jab125/minimega/mod/util/minigamedata/battle/ItemSet.java`
- `dev/jab125/minimega/mod/util/minigamedata/battle/HungerSettings.java`
- `dev/jab125/minimega/mod/util/minigamedata/battle/SpectatorMode.java`

Legacy-side classes remain present for compatibility, but are not marked DONE for full parity until those upstream files are recoverable within `FundyJo/Minimega`.

## GlideGameType codec verification

`wily.legacy.minigame.config.glide.GlideGameType` currently stays on integer-ordinal codecs (`Codec.INT.xmap(...)`, `STREAM_CODEC` ordinal mapping) as existing behavior.

Status: `NEEDS FUNDYJO/MINIMEGA VERIFICATION` because the upstream source file is currently unreadable (`// INTERNAL ERROR //`) and cannot be directly inspected for definitive codec structure.

## Phase 2 resources (FundyJo/Minimega only)

Imported directly from `FundyJo/Minimega`:

- `src/main/resources/assets/minimega/**` (333 files)
- `src/main/resources/data/minimega/**` (85 files)
- Root resources:
  - `src/main/resources/chinese_mythologypack.png`
  - `src/main/resources/fantasypack.png`
  - `src/main/resources/festivepack.png`
  - `src/main/resources/greek_mythologypack.png`
  - `src/main/resources/plasticpack.png`
  - `src/main/resources/ids.json`
  - `src/main/resources/LICENSE_minimega`
- Programmer-art namespace import:
  - `src/main/resources/programmer_art/assets/minimega/**` (99 files)

No placeholders, generated assets, or synthetic Minimega resources were introduced.

## Resource reference validation

Validated copied resources against the same FundyJo/Minimega snapshot.

### Inherent upstream-missing sound targets (also missing in FundyJo/Minimega)

From `assets/minimega/sounds.json`:

- `minimega:sounds/music/battle/dance_of_the_blocks.ogg`
- `minimega:sounds/music/battle/master_builders.ogg`
- `minimega:sounds/music/battle/toys_on_a_tear.ogg`
- `minimega:sounds/music/battle/crafters_candy_canes.ogg`
- `minimega:sounds/music/battle/giftwrapped.ogg`
- `minimega:sounds/music/battle/wondrous_workshop.ogg`

### Inherent upstream-missing texture targets (also missing in FundyJo/Minimega)

Model references without matching texture file in upstream snapshot:

- `models/block/old_booster_visualizer.json` -> `textures/block/booster_visualizer_top.png`
- `models/block/old_booster_visualizer.json` -> `textures/block/booster_visualizer.png`
- `models/block/_qbooster_visualizer.json` -> `textures/block/qbooster_visualizer_top.png`
- `models/block/_qbooster_visualizer.json` -> `textures/block/qbooster_visualizer.png`
- `models/block/thermal_visualizer.json` -> `textures/block/thermal_animated.png`

Status for those references: `BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY`.

## Stage 2 registries/resources code validation

- `wily.legacy.init.MinimegaRegistries` remains intentionally empty.
  - Rationale: only fully ported, source-backed registrations should be enabled.
  - No synthetic IDs are registered.
- `wily.legacy.minigame.MinigameResourceManager` remains inert by design until a verified loader parity implementation is introduced.

## Build (26.1.2-fabric)

Attempted:

```bash
JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64 ./gradlew :fabric:compileJava --no-daemon
```

Observed exact failure:

```text
Plugin [id: 'fabric-loom', version: '1.15-SNAPSHOT', apply: false] was not found ...
```

Status: `BLOCKED – ENVIRONMENT / DEPENDENCY RESOLUTION`.
