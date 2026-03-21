package com.gmail.anthony17j.worldloader.test;

import com.gmail.anthony17j.worldloader.WorldLoader;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.nio.file.Files;

public class WorldLoaderGameTest {

    @GameTest(maxTicks = 400)
    public void createsDefaultConfigFile(GameTestHelper helper) {
        MinecraftServer server = helper.getLevel().getServer();

        try {
            Files.deleteIfExists(WorldLoader.getConfigPath());
            WorldLoader.reloadConfigAndSpawn(server);

            if (!Files.exists(WorldLoader.getConfigPath())) {
                helper.fail("WorldLoader config file was not created at " + WorldLoader.getConfigPath());
                return;
            }

            String json = Files.readString(WorldLoader.getConfigPath());
            if (!json.contains("minecraft:overworld")) {
                helper.fail("Default config should contain minecraft:overworld but was: " + json);
                return;
            }
        } catch (Exception e) {
            helper.fail("Failed to verify default config file: " + e.getMessage());
            return;
        }

        helper.succeed();
    }

    @GameTest(maxTicks = 600)
    public void spawnsLoaderAtWorldSpawnXZBelowMap(GameTestHelper helper) {
        MinecraftServer server = helper.getLevel().getServer();
        ServerLevel overworld = server.overworld();

        helper.runAfterDelay(20, () -> {
            ServerPlayer loader = findLoader(overworld);
            if (loader == null) {
                helper.fail("Expected world loader in overworld but none was found");
                return;
            }

            var spawn = overworld.getRespawnData().pos();
            double dx = Math.abs(loader.getX() - (spawn.getX() + 0.5));
            double dz = Math.abs(loader.getZ() - (spawn.getZ() + 0.5));
            if (dx > 1.0 || dz > 1.0) {
                helper.fail("Loader must spawn at world spawn X/Z. Expected around (" + spawn.getX() + ", " + spawn.getZ() + ") but got ("
                        + loader.getX() + ", " + loader.getZ() + ")");
                return;
            }
            if (Math.abs(loader.getY() - (-128.0)) > 0.01) {
                helper.fail("Loader must stay at Y=-128 below the map, got " + loader.getY());
                return;
            }

            helper.succeed();
        });
    }

    @GameTest(maxTicks = 800)
    public void supportsConfiguredOverworldNetherEnd(GameTestHelper helper) {
        MinecraftServer server = helper.getLevel().getServer();

        String json = """
                {
                  "enabledDimensions": [
                    "minecraft:overworld",
                    "minecraft:the_nether",
                    "minecraft:the_end"
                  ]
                }
                """;

        try {
            Files.writeString(WorldLoader.getConfigPath(), json);
            WorldLoader.reloadConfigAndSpawn(server);
        } catch (Exception e) {
            helper.fail("Failed to write and reload config: " + e.getMessage());
            return;
        }

        helper.runAfterDelay(20, () -> {
            ServerLevel nether = server.getLevel(Level.NETHER);
            ServerLevel end = server.getLevel(Level.END);
            if (nether == null || end == null) {
                helper.fail("Nether or End not available for test");
                return;
            }

            if (findLoader(server.overworld()) == null) {
                helper.fail("Expected world loader in overworld after config reload");
                return;
            }
            if (findLoader(nether) == null) {
                helper.fail("Expected world loader in nether after config reload");
                return;
            }
            if (findLoader(end) == null) {
                helper.fail("Expected world loader in end after config reload");
                return;
            }

            helper.succeed();
        });
    }

    @GameTest(maxTicks = 600)
    public void acceptsCustomDimensionIdentifiersInConfig(GameTestHelper helper) {
        MinecraftServer server = helper.getLevel().getServer();

        String json = """
                {
                  "enabledDimensions": [
                    "minecraft:overworld",
                    "multiworld:test_world"
                  ]
                }
                """;

        try {
            Files.writeString(WorldLoader.getConfigPath(), json);
            WorldLoader.reloadConfigAndSpawn(server);
        } catch (Exception e) {
            helper.fail("Custom dimension id config failed to load: " + e.getMessage());
            return;
        }

        helper.runAfterDelay(20, () -> {
            if (findLoader(server.overworld()) == null) {
                helper.fail("Overworld loader missing after reloading config with custom dimension ids");
                return;
            }
            helper.succeed();
        });
    }

    private static ServerPlayer findLoader(ServerLevel level) {
        for (ServerPlayer player : level.players()) {
            if (player.getGameProfile().name().startsWith(WorldLoader.WORLD_LOADER_NAME_PREFIX)) {
                return player;
            }
        }
        return null;
    }
}
