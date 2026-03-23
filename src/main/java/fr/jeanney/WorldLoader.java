package fr.jeanney;

import fr.jeanney.fakeplayer.EntityPlayerFake;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorldLoader implements ModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger(WorldLoader.class);

	public static final String WORLD_LOADER_NAME_PREFIX = "#worldloader#";
	private static final int WORLD_LOADER_Y = -128;
	private static final String CONFIG_FILE_NAME = "worldloader.json";
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private static final Set<ResourceKey<Level>> SPAWNED_WORLDS = new HashSet<>();
	private static Set<ResourceKey<Level>> enabledDimensions = Set.of(Level.OVERWORLD);

	@Override
	public void onInitialize() {
		reloadConfig();

		ServerLevelEvents.LOAD.register((WorldLoader::trySpawnLoaderForWorld));

		ServerLifecycleEvents.SERVER_STARTED.register(WorldLoader::spawnConfiguredLoadersForLoadedWorlds);
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> SPAWNED_WORLDS.clear());
	}

	public static Path getConfigPath() {
		return FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE_NAME);
	}

	public static synchronized void reloadConfig() {
		Path configPath = getConfigPath();
		ConfigData config = new ConfigData();

		try {
			Files.createDirectories(configPath.getParent());
			if (Files.exists(configPath)) {
				String rawJson = Files.readString(configPath);
				ConfigData loaded = GSON.fromJson(rawJson, ConfigData.class);
				if (loaded != null && loaded.enabledDimensions != null && !loaded.enabledDimensions.isEmpty()) {
					config = loaded;
				}
			} else {
				writeConfig(configPath, config);
			}
		} catch (IOException | JsonSyntaxException e) {
			LOGGER.warn("Failed to load worldloader config, using defaults: {}", e.getMessage());
		}

		Set<ResourceKey<Level>> parsedDimensions = parseEnabledDimensions(config.enabledDimensions);
		if (parsedDimensions.isEmpty()) {
			parsedDimensions = Set.of(Level.OVERWORLD);
		}
		enabledDimensions = Set.copyOf(parsedDimensions);

		try {
			writeConfig(configPath, configForWrite(parsedDimensions));
		} catch (IOException e) {
			LOGGER.warn("Failed to write worldloader config: {}", e.getMessage());
		}
	}

	public static void reloadConfigAndSpawn(MinecraftServer server) {
		reloadConfig();
		spawnConfiguredLoadersForLoadedWorlds(server);
	}

	private static synchronized boolean isDimensionEnabled(ResourceKey<Level> dimensionKey) {
		return enabledDimensions.contains(dimensionKey);
	}

	private static synchronized void markSpawned(ResourceKey<Level> dimensionKey) {
		SPAWNED_WORLDS.add(dimensionKey);
	}

	private static synchronized boolean alreadySpawned(ResourceKey<Level> dimensionKey) {
		return SPAWNED_WORLDS.contains(dimensionKey);
	}

	private static void spawnConfiguredLoadersForLoadedWorlds(MinecraftServer server) {
		for (ServerLevel level : server.getAllLevels()) {
			trySpawnLoaderForWorld(server, level);
		}
	}

	private static void trySpawnLoaderForWorld(MinecraftServer server, ServerLevel world) {
		ResourceKey<Level> worldKey = world.dimension();
		if (!isDimensionEnabled(worldKey)) {
			return;
		}
		BlockPos sharedSpawn = world.getRespawnData().pos();
		Vec3 loaderPos = new Vec3(sharedSpawn.getX() + 0.5, WORLD_LOADER_Y, sharedSpawn.getZ() + 0.5);

		if (hasLoaderInWorld(world)) {
			repositionLoadersInWorld(world, loaderPos);
			markSpawned(worldKey);
			return;
		}

		if (alreadySpawned(worldKey)) {
			return;
		}

		String loaderName = getLoaderNameForWorld(world.dimension());
		EntityPlayerFake.createFake(loaderName, server, loaderPos, 0, 0, world.dimension(), GameType.SPECTATOR);
		markSpawned(worldKey);
		LOGGER.info("Spawned world loader '{}' in {} at {}", loaderName, world.dimension().identifier(), loaderPos);
	}

	private static void repositionLoadersInWorld(ServerLevel world, Vec3 loaderPos) {
		for (ServerPlayer player : world.players()) {
			if (player.getGameProfile().name().startsWith(WORLD_LOADER_NAME_PREFIX)) {
				player.snapTo(loaderPos.x, loaderPos.y, loaderPos.z, 0.0F, 0.0F);
			}
		}
	}

	private static boolean hasLoaderInWorld(ServerLevel world) {
		for (ServerPlayer player : world.players()) {
			if (player.getGameProfile().name().startsWith(WORLD_LOADER_NAME_PREFIX)) {
				return true;
			}
		}
		return false;
	}

	private static String getLoaderNameForWorld(ResourceKey<Level> dimensionKey) {
		String hash = Integer.toUnsignedString(dimensionKey.identifier().toString().hashCode(), 36);
		return WORLD_LOADER_NAME_PREFIX + "_" + hash;
	}

	private static Set<ResourceKey<Level>> parseEnabledDimensions(List<String> dimensionStrings) {
		Set<ResourceKey<Level>> result = new HashSet<>();
		for (String raw : dimensionStrings) {
			Identifier id = normalizeDimensionIdentifier(raw);
			if (id == null) {
				LOGGER.warn("Ignoring invalid worldloader dimension id '{}'.", raw);
				continue;
			}
			result.add(ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, id));
		}
		return result;
	}

	private static Identifier normalizeDimensionIdentifier(String raw) {
		if (raw == null || raw.isBlank()) {
			return null;
		}
		String lower = raw.toLowerCase();
		return switch (lower) {
			case "overworld" -> Level.OVERWORLD.identifier();
			case "nether", "the_nether" -> Level.NETHER.identifier();
			case "end", "the_end" -> Level.END.identifier();
			default -> Identifier.tryParse(raw);
		};
	}

	private static void writeConfig(Path configPath, ConfigData config) throws IOException {
		Files.writeString(configPath, GSON.toJson(config));
	}

	private static ConfigData configForWrite(Set<ResourceKey<Level>> dimensions) {
		ConfigData output = new ConfigData();
		output.enabledDimensions = dimensions.stream().map(key -> key.identifier().toString()).sorted().toList();
		return output;
	}

	private static final class ConfigData {
		List<String> enabledDimensions = List.of(Level.OVERWORLD.identifier().toString());
	}
}
