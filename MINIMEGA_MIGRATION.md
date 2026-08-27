# MINIMEGA Migration

## Source Recovery Status
- DONE: Target repository validated as `FundyJo/Legacy-Minecraft`.
- DONE: Requested branch `feature/minimega-integration` was fetched and used.
- DONE: Current target start commit confirmed: `cb7c0b270ede4cd9a936fd5b2fffc1ed7f3b661b`.
- IN PROGRESS: First loader-neutral Minimega core/data/config stage.
- BLOCKED – SOURCE RECOVERY: Source-of-truth repository `FundyJo/Minimega` is not currently accessible in this environment (GitHub API returns 404), so unresolved semantics are being deferred until direct source access is restored.
- BLOCKED – SOURCE RECOVERY: Old/indexed minigame implementation commit reference `06cdc412...` could not be resolved in fetched history of this repository.
- DONE: Verified that `wily.legacy.minigame.*` is not present on current `main` at the starting commit.

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
- IN PROGRESS: `Minigame` now acts as the first-stage ID/name registry value type.
- TODO: Hook into broader registry/bootstrap lifecycle once gameplay systems are wired.

## 10. UI Migration Map
- DONE: Added loader-neutral `MapInfo` with translation-key semantics (`displayName`, `description`).
- TODO: Actual UI screens/widgets remain for later migration phase.

## 11. Mixin Collision Map
- TODO: No minigame mixin migration in this stage.
- BLOCKED – SOURCE RECOVERY: Collision analysis needs full source/mixin set.

## 12. Resource Migration Map
- BLOCKED – SOURCE RECOVERY: Could not copy `assets/minimega` and `data/minimega` from `FundyJo/Minimega` because repository access is unavailable.
- TODO: Copy full namespace resources (`assets/minimega/**`, `data/minimega/**`) as soon as source access is restored.

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
3. TODO: Migrate/verify minimega resource namespace.
4. TODO: Introduce map/state persistence integration points.
5. TODO: Add networking payloads + protocol adapters.
6. TODO: Hook controller factories and gameplay controllers.
7. TODO: Port client UI/rendering layers.
8. TODO: Port and resolve mixins/events.
9. TODO: Feature parity verification across loaders and versions.

## 17. Feature Parity Checklist
- [x] DONE: Initial minigame value registry type (`Minigame`) with preserved IDs/names/progress/playable metadata.
- [x] DONE: Loader-neutral core map/config model class scaffolding and codecs.
- [x] DONE: `NoConfig` unit codec + unit stream codec.
- [x] DONE: `Lives` integer mapping semantics (`<=0` infinite, `>0` numbered).
- [x] DONE: Required first-stage confirmed constants:
  - `GlideGameType`: `TIME_ATTACK`, `SCORE_ATTACK`
  - `RoundLength`: `NORMAL`
  - `MapSize`: `AUTO`
  - `SpectatorMode`: `BAT`, `INVISIBLE`
  - `ItemSet`: `NORMAL`
  - `HungerSettings`: `NORMAL`
- [ ] TODO: Full resource namespace migration from source-of-truth repository.
- [ ] TODO: Controller/runtime hook-up (Phase 6+).
- [ ] BLOCKED – SOURCE RECOVERY: Validate unresolved semantics against unavailable `FundyJo/Minimega` source and `06cdc412...` indexed implementation.
