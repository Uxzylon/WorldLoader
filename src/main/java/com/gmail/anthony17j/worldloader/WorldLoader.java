package com.gmail.anthony17j.worldloader;

import com.gmail.anthony17j.worldloader.fakeplayer.EntityPlayerFake;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorldLoader implements ModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger(WorldLoader.class);

	public static final String playerName = "#worldloader#";

	@Override
	public void onInitialize() {
		ServerWorldEvents.LOAD.register(((server, world) -> {
			if (world.dimension() == Level.OVERWORLD) {
				EntityPlayerFake.createFake(playerName, server, new Vec3(0,-128,0), 0, 0, world.dimension(), GameType.SPECTATOR);
			}
		}));
	}
}
