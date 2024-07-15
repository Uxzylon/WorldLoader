package com.gmail.anthony17j.worldloader;

import com.gmail.anthony17j.worldloader.fakeplayer.EntityPlayerFake;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class WorldLoader implements ModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger(WorldLoader.class);

	public static PlayerEntity worldloader;
	public static final String playerName = "#worldloader#";

	@Override
	public void onInitialize() {
		ServerWorldEvents.LOAD.register(((server, world) -> {
			if (Objects.equals(world.getRegistryKey().getValue().getPath(), "overworld")) {
				worldloader = EntityPlayerFake.createFake(playerName, server, 0, -70, 0, 0, 0, world.getRegistryKey(), GameMode.SPECTATOR);
			}
		}));
	}
}
