# MINIMEGA Migration

## Phase 2 status

- BLOCKED – ORIGINAL RESOURCE RECOVERY
- Source of truth: upstream Minimega project and verified original artifacts.
- Current status: all generated placeholder resources were removed from the repository to avoid checking in fake Minimega assets.
- No verified `assets/minimega` or `data/minimega` tree is currently available from the available sources in this environment.

## Placeholder resource cleanup

The following synthetic placeholder content was removed because it was not traceable to an original Minimega artifact and violates the Minimega source-of-truth requirement:

- `src/main/resources/assets/minimega/**`
  - blockstates JSON
  - item JSON
  - model JSON
  - lang JSON
  - sound metadata JSON
  - `.ogg` sound files
  - PNG textures
- `src/main/resources/data/minimega/**`
  - game config JSON
  - gamerules XML
  - map JSON
  - generated metadata placeholders with `status: placeholder`

This includes the known placeholder file types explicitly called out by the issue: fake `.ogg`, fake textures, generated JSON/XML/NBT/GRF-style metadata, and fabricated Minimega registry IDs.

## Source recovery findings

### Attempted sources
- `FundyJo/Minimega` — inaccessible from the environment; repository lookup failed and was not usable as a source.
- `Minimega-Project/minimega-decomp` — inspected; no `assets/minimega` or `data/minimega` tree was present.
- local git history / tags / branch content — no original Minimega jar or resource archive was located in the checked-out repository state.
- known release artifacts and local recovered zip/jar files — none that could be verified as the canonical Minimega 6.5.32 / Minecraft 26.1.2 resource set.

### Result
- No verified original resource tree was recovered in this environment.
- Therefore no production-grade Minimega resource import is possible at this stage.
- The repository now contains no fake Minimega assets, and the source recovery blocker is documented honestly.

## Registry parity result

`MinimegaRegistries` is intentionally not registering any block or sound IDs while source parity remains unresolved.

Mapping table (verified versus blocked):

- Original Minimega `ModBlocks.*` -> Legacy4J port: `BLOCKED – SOURCE RECOVERY`
- Original Minimega `ModSounds.*` -> Legacy4J port: `BLOCKED – SOURCE RECOVERY`
- Original Minimega item registrations -> Legacy4J port: `BLOCKED – SOURCE RECOVERY`
- Fake/generated IDs found in this branch -> removed

No registry IDs remain active unless they are recovered from the original Minimega upstream source and verified byte-for-byte against the real artifact.

## Resource manager parity result

`MinimegaResourceManager` is kept inert and is not initialized during `Legacy4J.init()`.

Rationale:
- no verified original resource namespace was recovered,
- no verified asset loader logic was identified,
- keeping an empty loader abstraction would incorrectly imply source parity that does not exist.

`Legacy4J` startup therefore does not call the Minimega resource loader or registry registration while the source-recovery blocker remains open.

## Migration status summary

### Verified/acceptable
- the repository no longer contains artificial Minimega placeholder resources;
- no fake block/sound registration IDs remain active;
- no startup hook loads unverified Minimega resource code;
- the migration file explicitly records the source-recovery blocker.

### Blocked
- original Minimega 6.5.32 resource tree recovery;
- original `ModBlocks` / `ModSounds` parity verification;
- original `MinimegaResourceManager` runtime logic parity verification;
- any real port of block/sound/item registrations that depend on upstream source assets.

## Build blocker

Attempted build command:

```bash
JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64 ./gradlew compileJava --no-daemon
```

Observed exact failure:

```text
Plugin [id: 'fabric-loom', version: '1.15-SNAPSHOT', apply: false] was not found ...
```

Status: `BLOCKED – ENVIRONMENT / DEPENDENCY RESOLUTION`

This is not a Minimega code bug. The build cannot reach project compilation in this environment because the repository's configured Fabric Loom snapshot dependency is unavailable.

## Commit note

The codebase now intentionally avoids placeholders and keeps the recovery blocker explicit rather than fabricating a Minimega resource tree that has no source provenance.
