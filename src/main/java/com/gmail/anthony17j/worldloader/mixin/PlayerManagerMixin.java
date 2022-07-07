package com.gmail.anthony17j.worldloader.mixin;

import com.mojang.logging.LogUtils;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

    // @Shadow @Final private static final Logger LOGGER = LogUtils.getLogger();
    // @Shadow @Final private List<ServerPlayerEntity> players;

    @Shadow @Final private MinecraftServer server;

    @Shadow public abstract NbtCompound loadPlayerData(ServerPlayerEntity player);
    @Shadow public abstract void sendWorldInfo(ServerPlayerEntity player, ServerWorld world);

    @Inject(method = "onPlayerConnect(Lnet/minecraft/network/ClientConnection;Lnet/minecraft/server/network/ServerPlayerEntity;)V", at = @At("HEAD"), cancellable = true)
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {

        if (player.getGameProfile().getName().equals("#worldloader#")) {
            NbtCompound nbtCompound = this.loadPlayerData(player);
            ServerWorld serverWorld = this.server.getWorld(player.getEntityWorld().getRegistryKey());
            player.setWorld(serverWorld);
            BlockPos spawnPoint = serverWorld.getSpawnPos();
            player.setGameMode(nbtCompound);
            new ServerPlayNetworkHandler(this.server, connection, player);
            // this.players.add(player); // Visible to others
            serverWorld.onPlayerConnected(player);
            this.server.getBossBarManager().onPlayerConnect(player);
            this.sendWorldInfo(player, serverWorld);
            this.server.getResourcePackProperties().ifPresent(properties -> player.sendResourcePackUrl(properties.url(), properties.hash(), properties.isRequired(), properties.prompt()));
            player.sendServerMetadata(this.server.getServerMetadata());
            player.onSpawn();
            player.teleport(spawnPoint.getX(), -70, spawnPoint.getZ());
            // LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", player.getName().getString(), "local", player.getId(), player.getX(), player.getY(), player.getZ());
            ci.cancel();
        }

    }
}