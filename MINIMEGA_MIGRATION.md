# MINIMEGA Migration

## Source Recovery Status
- DONE: Target repository validated as `FundyJo/Legacy-Minecraft`.
- DONE: Requested branch `feature/minimega-integration` was fetched and used.
- DONE: Current target start commit confirmed: `cb7c0b270ede4cd9a936fd5b2fffc1ed7f3b661b`.
- DONE: Source parity review against `Minimega-Project/minimega-decomp` performed.
- IN PROGRESS: First loader-neutral Minimega core/data/config stage.
- BLOCKED – SOURCE RECOVERY: `FundyJo/Minimega` is inaccessible (GitHub API returns 404). Unresolved semantics deferred until access is restored.
- BLOCKED – SOURCE RECOVERY: Commit reference `06cdc412...` could not be resolved in fetched history.
- DONE: Verified `wily.legacy.minigame.*` is absent on `main` at the starting commit.

## Source Parity Review — minimega-decomp vs Legacy4J port

### `Minigame` (wily.legacy.minigame.Minigame)
- Original (`dev/jab125/minimega/util/Minigame.java`):
  - IDs confirmed: NONE=0, GLIDE=3, FISTFIGHT=70, LOBBY=99
  - CODEC: `Codec.INT` / `fromId(int)` (integer-id based)
  - No BATTLE or TUMBLE constants in original
- Legacy4J port:
  - DONE: NONE, GLIDE, FISTFIGHT, LOBBY with correct IDs confirmed.
  - BLOCKED – SOURCE RECOVERY: `BATTLE` (id=1) and `TUMBLE` (id=2) are Legacy4J additions not present in decomp. IDs/names/progress values are unverified best-effort. Must be validated when Battle/Tumble source is available.

### `GlideGameType` (wily.legacy.minigame.config.glide.GlideGameType)
- Original (`dev/jab125/minimega/util/controller/glide/GlideGameType.java`):
  - Constants (exact order): `TIME_ATTACK` (ordinal 0), `SCORE_ATTACK` (ordinal 1)
  - CODEC: `Codec.INT.xmap(a -> values()[a], Enum::ordinal)` — **integer-ordinal, not string-based**
  - No STREAM_CODEC in original (not a network payload in source)
- Legacy4J port (CORRECTED):
  - DONE: Constants match original (TIME_ATTACK ordinal 0, SCORE_ATTACK ordinal 1).
  - DONE: CODEC corrected to `Codec.INT.xmap(...)` matching original (was incorrectly using `StringRepresentable.fromEnum`).
  - DONE: STREAM_CODEC retained as Legacy4J addition with correct ordinal order.

### `ItemSet` (wily.legacy.minigame.config.battle.ItemSet)
- BLOCKED – SOURCE RECOVERY: Battle minigame absent from `minimega-decomp`. No original source found.
- Current value `NORMAL("normal")` is a Legacy4J placeholder — **not a verified Minimega value**.
- Class retained for compilation; marked with BLOCKED javadoc. Do not treat as 1:1 Minimega port.

### `HungerSettings` (wily.legacy.minigame.config.battle.HungerSettings)
- BLOCKED – SOURCE RECOVERY: Battle minigame absent from `minimega-decomp`. No original source found.
- Current value `NORMAL("normal")` is a Legacy4J placeholder — **not a verified Minimega value**.
- Class retained for compilation; marked with BLOCKED javadoc. Do not treat as 1:1 Minimega port.

### `RoundLength` (wily.legacy.minigame.config.battle.RoundLength)
- BLOCKED – SOURCE RECOVERY (partially confirmed): `NORMAL` is a confirmed known value per issue specification.
- Additional constants (e.g. SHORT, LONG) may exist — Battle minigame absent from `minimega-decomp`.
- Class retained; marked with BLOCKED javadoc noting incomplete constant set.

### `MapSize` (wily.legacy.minigame.config.battle.MapSize)
- BLOCKED – SOURCE RECOVERY (partially confirmed): `AUTO` is a confirmed known value per issue specification.
- Additional constants (e.g. SMALL, LARGE) may exist — Battle minigame absent from `minimega-decomp`.
- Class retained; marked with BLOCKED javadoc noting incomplete constant set.

### `SpectatorMode` (wily.legacy.minigame.config.battle.SpectatorMode)
- Confirmed values: `BAT` (ordinal 0), `INVISIBLE` (ordinal 1) — verified per issue specification.
- Additional constants may exist — Battle minigame absent from `minimega-decomp`.
- Class retained with source note.

### `Lives` (wily.legacy.minigame.config.battle.Lives)
- Structure confirmed: sealed interface with `Infinite` (encodes as int 0) and `Numbered(int amount)`.
- CODEC: `Codec.INT.xmap(Lives::fromAmount, Lives::asAmount)` — matches port.

### `BattleConfigSettings` / `CasualBattleConfigSettings` / `CompetitiveBattleConfigSettings`
- BLOCKED – SOURCE RECOVERY: Battle minigame absent from `minimega-decomp`. Entire battle config system is a Legacy4J addition.
- `CasualBattleConfigSettings` and `CompetitiveBattleConfigSettings` are correctly implemented as unit records.
- `PreconfiguredBattleConfigSettings` fields/order matches the intended design but cannot be verified against original.

### `NoConfig` (wily.legacy.minigame.config.NoConfig)
- Confirmed: unit type, `Codec.unit(INSTANCE)`, no-op encode. Correct.

### `BattleConfig` / `GlideConfig` (wily.legacy.minigame.config)
- Battle minigame absent from decomp — BattleConfig is Legacy4J addition (BLOCKED – SOURCE RECOVERY).
- GlideConfig is a Legacy4J addition wrapping confirmed `GlideGameType`.

### `MapInfo`, `MapData`, `MapVariant`, `MapVariants`, `BattleVariants`, `NormalVariants`
- These are Legacy4J data model additions. No equivalent in decomp.
- Structures appear sound for the intended purpose.

### `MinigameSpecificConfig`, `MinigameConfigCodecs`
- Legacy4J additions providing sealed dispatch codec infrastructure.
- No equivalent in decomp; purpose-built for this integration stage.

## 1. Architecture Overview
- DONE: Introduced a new loader-neutral package root: `wily.legacy.minigame`.
- DONE: Added core value/registry type (`Minigame`) and serializable data/config model classes.
- TODO: Wire gameplay controllers/network/runtime systems in later phases (Phase 6+).

## 2. Minimega Package Inventory
- DONE: Added:
  - `wily.legacy.minigame`
  - `wily.legacy.minigame.data`
  - `wily.legacy.minigame.config`
  - `wily.legacy.minigame.config.battle`
  - `wily.legacy.minigame.config.glide`
- TODO: Add `controller`, `network`, `state`, `client` once controller/runtime migration starts.

## 3. Fabric-specific Dependencies
- DONE: First stage intentionally excludes Fabric-only abstractions in common code.
- TODO: Fabric-only integrations remain in future loader-layer phases.

## 4. Legacy4J Existing Equivalents
- DONE: Codecs use Mojang codec + network stream codec patterns already used in Legacy4J common code.
- TODO: Align final integration points with Legacy4J registries and game flow once controllers are ported.

## 5. FactoryAPI Existing Equivalents
- DONE: Added loader-neutral interface codec helper (`MinigameConfigCodecs`) for tagged interface dispatch where a direct existing helper was not available in current tree.
- TODO: Replace/merge helper if a canonical FactoryAPI helper is identified as a better fit in later phase.

## 6. Networking Migration Map
- DONE: Added stream codecs for first-stage core/data/config types.
- TODO: Packet/channel registration and networking adapters (Fabric/Forge/NeoForge specifics) deferred.

## 7. Event Migration Map
- TODO: No event bridge ported yet.
- BLOCKED – SOURCE RECOVERY: Event parity details require unavailable Minimega source modules.

## 8. Storage Migration Map
- DONE: Added codec-backed persistent representations for minigame core/data/config models.
- TODO: Integrate with world/session persistence points after controllers/state layers are ported.

## 9. Registry Migration Map
- DONE: Added `wily.legacy.init.MinimegaRegistries`, using loader-neutral `FactoryAPIPlatform.createRegister("minimega", ...)` for blocks and sounds.
- DONE: Registered resource-backed visualizer blocks and sound IDs for the known Minimega IDs (`absolute_speed_boost`, `beacon_beam`, `booster_visualizer`, `diamond_ring_block`, `emerald_ring_block`, `gold_ring_block`, `qbooster_visualizer`, `thermal_visualizer`, and the battle/glide/timer/showdown sound events).
- IN PROGRESS: Items remain intentionally unregistered until confirmed from upstream source; no fabricated item IDs were added.
- TODO: Hook additional minigame-specific registry entries into broader gameplay lifecycle once upstream source confirms the remaining IDs.

## 10. UI Migration Map
- DONE: Added loader-neutral `MapInfo` with translation-key semantics (`displayName`, `description`).
- TODO: Actual UI screens/widgets remain for later migration phase.

## 11. Mixin Collision Map
- TODO: No minigame mixin migration in this stage.
- BLOCKED – SOURCE RECOVERY: Collision analysis needs full source/mixin set.

## 12. Resource Migration Map
- DONE: Added the `minimega` resource namespace under `src/main/resources/assets/minimega/` with blockstates, items, lang, models, sounds metadata, and placeholder `.ogg`/texture resources.
- DONE: Added `src/main/resources/data/minimega/minimega_minigames/` with `battle/`, `fistfight/`, `glide/`, `lobby/`, `tumble/`, `gamerules/`, and `maps/` metadata placeholders.
- IN PROGRESS: Full 1:1 upstream recovery of the original `FundyJo/Minimega` resource tree remains blocked because the referenced repository is unavailable from the current environment.
- BLOCKED – SOURCE RECOVERY: The original upstream asset tree, names, and pack-specific metadata cannot be verified byte-for-byte without access to the source repository or recovered artifact zip.
- DONE: Confirmed `Minimega-Project/minimega-decomp` contains no `assets/minimega` or `data/minimega` tree; any missing files remain source-recovery blockers rather than code defects.

## 13. Multiplayer / Hosting Map
- TODO: No hosting/session orchestration migration yet.
- BLOCKED – SOURCE RECOVERY: Full parity requires networking/session logic from unavailable source modules.

## 14. Version Compatibility Risks
- IN PROGRESS: Implemented with active Stonecutter target `26.1.2-fabric` APIs.
- TODO: Validate behavior against `1.21.11-*` lines after core runtime integration.

## 15. Loader Compatibility Risks
- IN PROGRESS: Common code avoids Fabric-only imports and should be loader-neutral.
- TODO: Run explicit Forge/NeoForge compile verification once first-stage fabric compile passes in this branch context.

## 16. Ordered Migration Phases
1. DONE: Establish migration plan and package scaffold.
2. DONE: Port first-stage loader-neutral core/data/config classes and codecs.
3. DONE: Source parity review against `Minimega-Project/minimega-decomp`; corrected `GlideGameType.CODEC`; documented all BLOCKED types.
4. TODO: Migrate/verify minimega resource namespace.
5. TODO: Introduce map/state persistence integration points.
6. TODO: Add networking payloads + protocol adapters.
7. TODO: Hook controller factories and gameplay controllers.
8. TODO: Port client UI/rendering layers.
9. TODO: Port and resolve mixins/events.
10. TODO: Feature parity verification across loaders and versions.

## 17. Feature Parity Checklist
- [x] DONE: Initial minigame value registry type (`Minigame`) — confirmed IDs for NONE/GLIDE/FISTFIGHT/LOBBY; BATTLE/TUMBLE flagged as unverified Legacy4J additions.
- [x] DONE: Loader-neutral core map/config model class scaffolding and codecs.
- [x] DONE: `NoConfig` unit codec + unit stream codec — confirmed correct.
- [x] DONE: `Lives` integer mapping semantics (`<=0` infinite, `>0` numbered) — confirmed correct.
- [x] DONE: `GlideGameType` codec CORRECTED to integer-ordinal (`Codec.INT.xmap`) matching original source.
- [x] DONE: `GlideGameType` constants confirmed: `TIME_ATTACK` (ordinal 0), `SCORE_ATTACK` (ordinal 1).
- [x] DONE: `SpectatorMode` — confirmed values: `BAT` (ordinal 0), `INVISIBLE` (ordinal 1).
- [x] DONE: `RoundLength.NORMAL` — confirmed known value; marked as potentially incomplete.
- [x] DONE: `MapSize.AUTO` — confirmed known value; marked as potentially incomplete.
- [x] BLOCKED – SOURCE RECOVERY: `ItemSet` — entire Battle config system absent from decomp; `NORMAL` is an unverified Legacy4J placeholder.
- [x] BLOCKED – SOURCE RECOVERY: `HungerSettings` — entire Battle config system absent from decomp; `NORMAL` is an unverified Legacy4J placeholder.
- [x] DONE: Added the `minimega` resource namespace under `src/main/resources` and the `data/minimega/minimega_minigames/` metadata skeleton.
- [x] DONE: Added loader-neutral Minimega registry entries for visualizer blocks and sound events via `FactoryAPIPlatform.createRegister("minimega", ...)`.
- [x] DONE: Added `MinimegaResourceManager` foundation without Fabric-specific resource reload abstractions.
- [ ] BLOCKED – SOURCE RECOVERY: Full byte-identical import of original `assets/minimega` and `data/minimega` resources from `FundyJo/Minimega`.
- [ ] TODO: Controller/runtime hook-up (Phase 6+).
- [ ] BLOCKED – SOURCE RECOVERY: Validate unresolved semantics against `FundyJo/Minimega` source when available.

## 18. Build attempts and blockers
- 2026-08-31: `JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64 ./gradlew compileJava --no-daemon`
- RESULT: failed before Java compilation because the repository's build setup resolves `fabric-loom` snapshot `1.15-SNAPSHOT`, which is not available from the configured plugin repositories in this environment.
- Exact error excerpt: `Plugin [id: 'fabric-loom', version: '1.15-SNAPSHOT', apply: false] was not found ...`.
- Status: `BLOCKED – ENVIRONMENT / REPOSITORY` rather than code regression. The build could not reach the project compilation step here.
