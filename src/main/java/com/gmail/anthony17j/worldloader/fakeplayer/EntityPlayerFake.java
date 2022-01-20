package com.gmail.anthony17j.worldloader.fakeplayer;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.NetworkSide;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.UserCache;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

public class EntityPlayerFake extends ServerPlayerEntity {

    public static EntityPlayerFake createFake(String username, MinecraftServer server, double d0, double d1, double d2, double yaw, double pitch, RegistryKey<World> dimensionId, GameMode gamemode)
    {
        ServerWorld worldIn = server.getWorld(dimensionId);
        UserCache.setUseRemote(false);
        GameProfile gameprofile = server.getUserCache().findByName(username).orElse(null);
        EntityPlayerFake instance = new EntityPlayerFake(server, worldIn, gameprofile);
        server.getPlayerManager().onPlayerConnect(new ClientConnectionFake(NetworkSide.SERVERBOUND), instance);
        instance.teleport(worldIn, d0, d1, d2, (float)yaw, (float)pitch);
        instance.setHealth(20.0F);
        instance.stepHeight = 0.6F;
        instance.interactionManager.changeGameMode(gamemode);
        return instance;
    }

    private EntityPlayerFake(MinecraftServer server, ServerWorld worldIn, GameProfile profile)
    {
        super(server, worldIn, profile);
    }
}
