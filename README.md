# WorldLoader

WorldLoader is a server-side Fabric mod that keeps selected dimensions loaded even when no real players are online.

It does this by spawning an internal fake spectator player per configured dimension.

## What it does

- Spawns one loader player in each enabled dimension.
- Keeps those dimensions active so chunk ticking and automation continue.
- Reuses existing loader players after restart/reload instead of creating duplicates.
- Places loader players at:
	- X/Z: the world respawn position
	- Y: fixed at -128

## Configuration

WorldLoader uses a JSON config file:

- Path: `config/worldloader.json`
- Created automatically on first start.

### Default config

```json
{
	"enabledDimensions": [
		"minecraft:overworld"
	]
}
```

### Options

- `enabledDimensions`: list of dimension ids to keep loaded.

Supported values include:

- Full ids, for example:
	- `minecraft:overworld`
	- `minecraft:the_nether`
	- `minecraft:the_end`
	- `yourmod:your_dimension`
- Aliases:
	- `overworld`
	- `nether` / `the_nether`
	- `end` / `the_end`

If the config is invalid or empty, WorldLoader falls back to loading only the overworld.

## How to use

1. Install the mod on your server.
2. Start the server once to generate `config/worldloader.json`.
3. Edit `enabledDimensions` to match the dimensions you want loaded.
4. Restart the server.

## Notes

- This is a server-side mod; clients do not need it.
- There are no user commands currently; configuration is file-based.
- Loader entities use internal names prefixed with `#worldloader#`.