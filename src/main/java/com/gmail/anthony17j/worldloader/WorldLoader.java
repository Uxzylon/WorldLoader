package com.gmail.anthony17j.worldloader;

import com.gmail.anthony17j.worldloader.fakeplayer.EntityPlayerFake;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class WorldLoader implements ModInitializer {

	public static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void onInitialize() {
		ServerWorldEvents.LOAD.register(((server, world) -> {
			if (Objects.equals(world.getRegistryKey().getValue().getPath(), "overworld")) {
				PlayerEntity player = EntityPlayerFake.createFake(new String("#worldloader#"), server, 0, -70, 0, 0, 0, world.getRegistryKey(), GameMode.SPECTATOR);
			}
		}));
	}
}
