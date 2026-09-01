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
  - `NONE=0`, `BATTLE=1`, `TUMBLE=2`, `GLIDE=3`, `LOBBY=99`
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

## Build (26.1.2-forge)

Attempted:

```bash
JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64 ./gradlew :forge:compileJava --no-daemon
```

Observed exact failure:

```text
Plugin [id: 'fabric-loom', version: '1.15-SNAPSHOT', apply: false] was not found ...
```

Status: `BLOCKED – ENVIRONMENT / DEPENDENCY RESOLUTION`.

## Build (26.1.2-neoforge)

Attempted:

```bash
JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64 ./gradlew :neoforge:compileJava --no-daemon
```

Observed exact failure:

```text
Plugin [id: 'fabric-loom', version: '1.15-SNAPSHOT', apply: false] was not found ...
```

Status: `BLOCKED – ENVIRONMENT / DEPENDENCY RESOLUTION`.

## Phase 3 – Networking Migration (FactoryAPI/CommonNetwork)

- IN PROGRESS – payload data models/codecs are being ported under `wily.legacy.minigame.network.payload`.
- `Minimega` Fabric networking abstraction (`ClientNetworking`, `ServerNetworking`, `PayloadRegistry`, `FabricClientNetworking`, `FabricServerNetworking`, `FabricPayloadRegistry`) -> **REPLACED BY FactoryAPI/CommonNetwork**.
- Bootstrap hook added: `Legacy4J.init()` now calls `MinimegaNetwork.register()`.

### Networking Migration Map

| Original class | Identifier | Direction | Fields | Codec | Handler | Sender | Receiver | Thread/context | Side effects | Legacy4J target | Status |
|---|---|---|---|---|---|---|---|---|---|---|---|
| C2SReadyPayload | `minimega:ready` | C2S | `boolean ready` | `BOOL` | ready-toggle logic in minigame controllers | client ready action | server play receiver | server main executor | match readiness state | `wily.legacy.minigame.network.payload.C2SReadyPayload` | IN PROGRESS |
| C2SVotePayload | `minimega:vote` | C2S | `Identifier resourceLocation` | `Identifier` | vote selection in minigame controllers | voting UI | server play receiver | server main executor | map/mode vote updates | `...C2SVotePayload` | IN PROGRESS |
| C2SRestartPayload | `minimega:c2srestart` | C2S | `boolean fromStart` | `BOOL` | restart controller path | restart UI | server play receiver | server main executor | restarts minigame round | `...C2SRestartPayload` | IN PROGRESS |
| C2STakeAllPayload | `minimega:c2stakeall` | C2S | none | unit | battle/tumble inventory controller | take-all action | server play receiver | server main executor | bulk item transfer | `...C2STakeAllPayload` | IN PROGRESS |
| C2STimerSynchronizationPayload | `minimega:c2s_timer_synchronization` | C2S | `int number` | `INT` | timer sync controller | client timer sync | server play receiver | server main executor | timer correction | `...C2STimerSynchronizationPayload` | IN PROGRESS |
| C2S2CMinimegaProtocolVersionPayload | `minimega:protocol_version` | C2S+S2C config | `int version` | `INT` | protocol gate during configuration | both sides config handshake | both sides config receiver | configuration networking task | allows/blocks join | `...C2S2CMinimegaProtocolVersionPayload` | IN PROGRESS |
| C2SFinishedMapLoadingPayload | `minimega:finished_map_loading` | C2S | none | unit | map loading transition controller | client post-load ack | server play receiver | server main executor | starts active gameplay | `...C2SFinishedMapLoadingPayload` | IN PROGRESS |
| C2SLinkPayload | `minimega:c2slink` | C2S config | `String code (max 30)` | utf8(30) | discord/link flow | link UI submit | server config receiver | configuration networking task | account linking state | `...C2SLinkPayload` | IN PROGRESS |
| C2SLinkScreenClosedPayload | `minimega:link_screen_closed` | C2S config | none | unit | discord/link flow | close link screen | server config receiver | configuration networking task | link flow task completion | `...C2SLinkScreenClosedPayload` | IN PROGRESS |
| C2SPacksDownloadedPayload | `minimega:packs_downloaded` | C2S config | none | unit | packs gate flow | packs downloaded ack | server config receiver | configuration networking task | join gate progression | `...C2SPacksDownloadedPayload` | IN PROGRESS |
| C2SSqueakPayload | `minimega:c2ssqueak` | C2S | none | unit | squeak interaction controller | client squeak action | server play receiver | server main executor | emote/sound/event trigger | `...C2SSqueakPayload` | IN PROGRESS |
| C2SJoiningChoicePayload | `minimega:c2s_joining_choice` | C2S config | `CreateOrJoin data` | `CreateOrJoinCodecs.STREAM_CODEC` | joining controller | create/join flow UI | server config receiver | configuration networking task | lobby/match creation routing | target in `wily.legacy.minigame.network.payload` | BLOCKED – CONTROLLER MIGRATION |
| C2SRecreationPayload | `minimega:c2srecreation` | C2S | `MinigameData data` | `MinigameData.STREAM_CODEC` | recreation controller | recreation UI | server play receiver | server main executor | match recreation state | target in `wily.legacy.minigame.network.payload` | BLOCKED – CONTROLLER MIGRATION |
| S2CDisplayTextPayload | `minimega:display_text` | S2C | `Component component` | component | HUD/display-text client handler | server gameplay events | client play receiver | client executor | on-screen text display | `...S2CDisplayTextPayload` | IN PROGRESS |
| S2CDisplayShieldPayload | `minimega:s2cdisplayshield` | S2C | `Identifier sprite`, `Component`, `varint priority` | id+component+varint | HUD/shield display handler | server gameplay events | client play receiver | client executor | shield/top-banner UI | `...S2CDisplayShieldPayload` | IN PROGRESS |
| S2CGlideFinishPayload | `minimega:s2c_glide_finish` | S2C | `UUID playerUuid`, `varint place`, `boolean bestResult`, `GlideGameType` | uuid+varint+bool+enum codec | glide finish client handler | glide controller | client play receiver | client executor | finish placement/FX | `...S2CGlideFinishPayload` | IN PROGRESS |
| S2CCheckpointsRespawnUpdatePayload | `minimega:s2crpup` | S2C | `int checkpoint`, `int respawnCheckpoint`, `boolean finishedMap`, `varint score` | int+int+bool+varint | glide checkpoint client handler | glide controller | client play receiver | client executor | respawn/checkpoint HUD | `...S2CCheckpointsRespawnUpdatePayload` | IN PROGRESS |
| S2CTimerSynchronizationPayload | `minimega:s2c_timer_synchronization` | S2C | `Duration(seconds,nanos)`, `int number`, `boolean leaderboardCounted` | long+int+int+bool | timer sync client handler | server timer source | client play receiver | client executor | timer correction and LB flag | `...S2CTimerSynchronizationPayload` | IN PROGRESS |
| S2CMapTransitionStartPayload | `minimega:map_transition_start` | S2C | `MapInfo info`, `boolean inInSameLevel` | `MapInfo.STREAM_CODEC`+bool | map transition client handler | server map controller | client play receiver | client executor | transition UI/state | `...S2CMapTransitionStartPayload` | IN PROGRESS |
| S2CGlobalSoundPayload | `minimega:s2c_global_sound` | S2C | `Identifier id`, `Optional<BlockPos> pos` | id+optional blockpos | global sound client handler | server gameplay events | client play receiver | client executor | plays positional/global sound | `...S2CGlobalSoundPayload` | IN PROGRESS |
| S2CLinkPayload | `minimega:s2clink` | S2C config | `String code (max 30)` | utf8(30) | config link client handler | server link flow | client config receiver | configuration task client-side | opens/updates link UI | `...S2CLinkPayload` | IN PROGRESS |
| S2CLinkScreenUpdatePayload | `minimega:linkscreenpacket` | S2C config | `boolean successful` | bool | config link client handler | server link flow | client config receiver | configuration task client-side | updates link state | `...S2CLinkScreenUpdatePayload` | IN PROGRESS |
| S2CJoiningChoicePayload | `minimega:s2c_joining_choice` | S2C config | none | unit | config join-choice client handler | server config | client config receiver | configuration task client-side | opens join-choice flow | `...S2CJoiningChoicePayload` | IN PROGRESS |
| S2CDownloadResourcePacksPayload | `minimega:download_resource_packs` | S2C config | `List<MinimegaPackObj>` | list(packId,url,hash,required) | packs download client flow | server config | client config receiver | configuration task client-side | enqueues required packs | `...S2CDownloadResourcePacksPayload`, `...MinimegaPackObj` | IN PROGRESS |
| S2CStatusPayload | unknown (`// INTERNAL ERROR //` in source) | S2C | unknown | unknown | status UI handler | server gameplay events | client play receiver | client executor | status indicator updates | target in `wily.legacy.minigame.network.payload` | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY |
| S2CThermalsPayload | `minimega:thermals` | S2C | `List<GlideMinigameController.Thermal>` | list(Thermal codec) | glide thermal client handler | glide controller | client play receiver | client executor | thermal ring behavior | target in `wily.legacy.minigame.network.payload` | BLOCKED – CONTROLLER MIGRATION |
| S2CPlayerPositionsPayload | `minimega:s2c_player_positions` | S2C | `List<PlayerInformation>` | list(PlayerInformation codec) | glide HUD positions | glide controller | client play receiver | client executor | player position overlays | target in `wily.legacy.minigame.network.payload` | BLOCKED – CONTROLLER MIGRATION |
| S2CPlayerSlotObjPayload | `minimega:s2c_playerslotobjspayload` | S2C | `PlayerSlotObjs objs` | `PlayerSlotObjs.STREAM_CODEC` | slot object client handler | server gameplay/controller | client play receiver | client executor | UI slot state updates | target in `wily.legacy.minigame.network.payload` | BLOCKED – CONTROLLER MIGRATION |
| S2COpenDataScreenPayload | `minimega:s2copendatascreen` | S2C | `Minigame`, `NewScreenData` | minigame idMapper + `NewScreenData.STREAM_CODEC` | open data screen client handler | server controller | client play receiver | client executor | opens config/data UI | target in `wily.legacy.minigame.network.payload` | BLOCKED – CONTROLLER MIGRATION |
| S2CMatchToSubmit | `minimega:s2cmatchtosubmit` | S2C | `SubmitGlideMatchObj` | composite(map,duration,type,place,checkpointCount,deathCount,sentToServer,verified) | post-match submit client handler | glide server controller | client play receiver | client executor | matchmaking submission | target in `wily.legacy.minigame.network.payload` | BLOCKED – CONTROLLER MIGRATION |
| S2CScoreRingCollisionPayload | `minimega:score_ring_collision` | S2C | `int level`, `UUID uuid`, `varint points` | int+uuid+varint | glide ring collision handler | glide controller | client play receiver | client executor | score updates and FX | `...S2CScoreRingCollisionPayload` | IN PROGRESS |

### Registration status

- `wily.legacy.minigame.network.MinimegaNetwork` added as central registration entry point.
- Active payload registration remains **deferred** until parity-safe handlers are ported (controller migration dependency); no fake handlers were activated.

## Phase 4 – Minimega State Machine + Controller Core Migration

Status: IN PROGRESS (source-backed migration started on branch `copilot/featureminimega-controller-core`).

### State Machine Migration Map

| Original class | Purpose | Dependencies | Client/server/common | FactoryAPI dependencies | Legacy4J target | Status |
|---|---|---|---|---|---|---|
| `dev.jab125.minimega.mod.util.state.State` | Stack-state execution contract | none | common | none | `wily.legacy.minigame.state.State` (pending) | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY (upstream decompiler-artifact methods) |
| `dev.jab125.minimega.mod.util.state.AbstractState` | Core op execution (`eqn/eq/eqJS/and/or/not/concat/addn/dup/fetch`) | `State` | common | none | `wily.legacy.minigame.state.AbstractState` (pending) | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY |
| `dev.jab125.minimega.mod.util.state.ConditionParser` | Condition parser + instruction stream builder | `State` | common | none | `wily.legacy.minigame.state.ConditionParser` (pending) | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY |

### Controller Migration Map

| Original class | Purpose | Dependencies | Client/server/common | FactoryAPI dependencies | Legacy4J target | Status |
|---|---|---|---|---|---|---|
| `dev.jab125.minimega.mod.util.controller.MinigamesController` | Per-level minigame controller owner and routing hub | `Minigame`, `AbstractMinigameController` | common/server | `FactoryEvent` lifecycle hooks | `wily.legacy.minigame.controller.MinigamesController` | IN PROGRESS (routing skeleton exists, upstream attachment persistence/rules/player routing behavior incomplete) |
| `dev.jab125.minimega.mod.util.controller.AbstractMinigameController` | Shared controller base lifecycle + player action hooks | `MinigamesController`, `MinigameData` | common/server | none | `wily.legacy.minigame.controller.AbstractMinigameController` | IN PROGRESS (base scaffold exists; upstream lifecycle/state side-effects not yet ported) |
| `dev.jab125.minimega.mod.util.minigamedata.MinigameData` | Shared session/minigame setup state | config + map ids + minigame id | common | none | `wily.legacy.minigame.minigamedata.MinigameData` | PORTED |

### Networking Handler Activation Map

| Payload | Original registration | Original handler method | Original controller call | Original state mutation | Original packet response | Legacy4J implementation | Parity status |
|---|---|---|---|---|---|---|---|
| `C2SFinishedMapLoadingPayload` | `ServerPlayNetworking.registerGlobalReceiver(C2SFinishedMapLoadingPayload.TYPE, ...)` in `Minimega.onInitialize` | inline lambda in `Minimega.onInitialize` | `MinigamesController.getMinigameController(level).playerLoadedIn(player)` | level null -> disconnect, otherwise delegate to active minigame controller | disconnect only on invalid/null level | `MinimegaNetworkHandlers.handleFinishedMapLoading` delegates to `playerLoadedIn` | IN PROGRESS |
| `C2SReadyPayload` | `ServerPlayNetworking.registerGlobalReceiver(C2SReadyPayload.TYPE, ...)` in `Minimega.onInitialize` | inline lambda in `Minimega.onInitialize` | `MinigamesController.getMinigameController(level).playerReady(player, payload.ready())` | level null -> disconnect, otherwise mutate controller ready state | disconnect only on invalid/null level | `MinimegaNetworkHandlers.handleReady` delegates to `playerReady` | IN PROGRESS |
| `C2SJoiningChoicePayload` | `ServerConfigurationNetworking.registerReceiver(..., C2SJoiningChoicePayload.TYPE, ...)` in `Minimega.completeLogin` | inline lambda in `Minimega.completeLogin` | no direct minigame-controller call; updates config connection extension | `mm$setMinigameData(wrap(payload.data()))` + `completeTask(CHOICE)` | advances configuration task chain | `C2SJoiningChoicePayload.apply` currently routes to `MinigamesController.playerJoiningChoice` only | IN PROGRESS |
| `C2SVotePayload` | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY | `MinimegaNetworkHandlers.handleVote` delegates to scaffold controller hook | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY |
| `C2SRestartPayload` | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY | `MinimegaNetworkHandlers.handleRestart` delegates to scaffold controller hook | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY |
| `C2STimerSynchronizationPayload` | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY | `MinimegaNetworkHandlers.handleTimerSynchronization` delegates to scaffold controller hook | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY |
| `C2STakeAllPayload` | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY | `MinimegaNetworkHandlers.handleTakeAll` delegates to scaffold controller hook | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY |
| `C2SRecreationPayload` | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY | `C2SRecreationPayload.apply` routes to `MinigamesController.playerRecreation` only | BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY |
| Remaining S2C/client-heavy payload handlers | payload-specific upstream handlers partially unrecoverable or client-UI dependent | payload-specific | payload-specific | payload-specific | payload-specific | blocked logging stubs retained in `MinimegaNetworkHandlers` | IN PROGRESS |

### Full Payload Diff (FundyJo/Minimega: 32 sources)

| Payload source class | Classification |
|---|---|
| C2S2CMinimegaProtocolVersionPayload | PORTED |
| C2SFinishedMapLoadingPayload | PORTED |
| C2SJoiningChoicePayload | PORTED |
| C2SLinkPayload | PORTED |
| C2SLinkScreenClosedPayload | PORTED |
| C2SPacksDownloadedPayload | PORTED |
| C2SReadyPayload | PORTED |
| C2SRecreationPayload | PORTED |
| C2SRestartPayload | PORTED |
| C2SSqueakPayload | PORTED |
| C2STakeAllPayload | PORTED |
| C2STimerSynchronizationPayload | PORTED |
| C2SVotePayload | PORTED |
| MinimegaPackObj | SHARED DATA TYPE |
| S2CCheckpointsRespawnUpdatePayload | PORTED |
| S2CDisplayShieldPayload | PORTED |
| S2CDisplayTextPayload | PORTED |
| S2CDownloadResourcePacksPayload | PORTED |
| S2CGlideFinishPayload | PORTED |
| S2CGlobalSoundPayload | PORTED |
| S2CJoiningChoicePayload | PORTED |
| S2CLinkPayload | PORTED |
| S2CLinkScreenUpdatePayload | PORTED |
| S2CMapTransitionStartPayload | PORTED |
| S2CMatchToSubmit | PORTED |
| S2COpenDataScreenPayload | MISSING (BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY for `NewScreenData`/GUI-mode semantics) |
| S2CPlayerPositionsPayload | PORTED |
| S2CPlayerSlotObjPayload | PORTED |
| S2CScoreRingCollisionPayload | PORTED |
| S2CStatusPayload | MISSING (BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY) |
| S2CThermalsPayload | MISSING (BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY; upstream thermal data model not recoverable in source snapshot) |
| S2CTimerSynchronizationPayload | PORTED |

### ModNetworking.java parity check

`wily.legacy.minigame.network.MinimegaNetwork.register()` now binds FactoryAPI lifecycle directly (`serverStarted`, `serverStopping`, `afterServerTick`) without reflection.

`player removed` lifecycle parity now uses FactoryAPI directly via `FactoryEvent.PlayerEvent.REMOVED_EVENT`.

## OUT OF SCOPE – FISTFIGHT

Fistfight is intentionally excluded from the Legacy4J Minimega integration.
No Fistfight gameplay, controller, UI, resources, networking, maps, or configuration will be migrated.

## Phase 5 – Lobby / Session / Lobby GUI

Status: IN PROGRESS

Lobby/session/gameplay-adjacent components remain in migration with strict source-backing from `FundyJo/Minimega`; no synthetic lobby/controller/UI substitute is marked DONE.

## Future Phase Plan

- Phase 5 – Lobby / Session / Lobby GUI: IN PROGRESS
- Phase 6 – Battle Controller / Battle Gameplay: DEFERRED – BATTLE PHASE
- Phase 7 – Battle GUI / HUD / shared Minigame UI: DEFERRED – BATTLE PHASE
- Phase 8 – Glide Controller / Glide Gameplay / Glide GUI: DEFERRED – GLIDE PHASE
- Phase 9 – Tumble Controller / Tumble Gameplay / Tumble GUI: DEFERRED – TUMBLE PHASE
- Phase 10 – Hosting / P2P / Matchmaking: IN PROGRESS
- Phase 11 – Mixins / compatibility / parity cleanup: IN PROGRESS
- Phase 12 – Full parity / cleanup / loader/version validation: IN PROGRESS
