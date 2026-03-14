# Minigame Template Packs

Minigame worlds are loaded from `.mcsave` template pack files.
These files are ZIP archives containing a Minecraft world save (region files, entity data, etc.).

## Directory Structure

Place template packs in one of these locations (server operators can add custom maps):

```
server_root/
└── world_templates/
    └── minigame/
        ├── glide/
        │   ├── bedrock_box.mcsave
        │   ├── crimson_fortress.mcsave
        │   ├── skylines.mcsave
        │   ├── trial_chamber.mcsave
        │   ├── supernova_laboratory.mcsave
        │   └── undersea_oasis.mcsave
        ├── fistfight/
        │   ├── skywars.mcsave
        │   └── basic_arena.mcsave
        ├── tumble/
        │   └── basic_arena.mcsave
        └── lobby/
            └── default.mcsave
```

Or bundled in the mod JAR at:
```
assets/legacy/minigame/templates/{minigame}/{map_name}.mcsave
```

## .mcsave Format

A `.mcsave` file is a ZIP archive with the following structure:
```
region/
  r.0.0.mca
  r.0.-1.mca
  ...
entities/
  r.0.0.mca
  ...
poi/
  r.0.0.mca
  ...
```

This matches the structure of a Minecraft world save dimension folder.

## GRF Reference

Each map also has a `.grf` XML file at `assets/legacy/minigame/maps/{minigame}/{map_name}.grf`
that defines the game-logic metadata:
- `<TemplatePack path="..."/>` - reference to the .mcsave file
- `<SpawnPos x="..." y="..." z="..."/>` - default player spawn position
- `<Checkpoint .../>` - Glide course checkpoints
- `<FistfightFlag .../>` - Fistfight spawn and zone markers
- `<LevelRules .../>` - Game rule overrides

## Example GRF File

```xml
<?xml version="1.0" encoding="UTF-8"?>
<MinigameMap>
    <TemplatePack path="glide/my_map"/>
    <SpawnPos x="0" y="80" z="0"/>
    <LevelRules pvp="false" naturalRegeneration="false" keepInventory="false" timeOfDay="6000" weather="false"/>
    <Checkpoint index="0" x="0" y="75" z="50" radius="5"/>
    <Checkpoint index="1" x="50" y="68" z="80" radius="5"/>
    <Checkpoint index="2" x="0" y="60" z="0" radius="5"/>
</MinigameMap>
```
