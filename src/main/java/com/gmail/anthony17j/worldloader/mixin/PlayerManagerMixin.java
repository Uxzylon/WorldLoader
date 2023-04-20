package com.gmail.anthony17j.worldloader.mixin;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.util.List;
import java.util.Objects;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

    @Shadow @Final private static final Logger LOGGER = LogUtils.getLogger();
    @Shadow @Final private List<ServerPlayerEntity> players;

    @Shadow @Final private MinecraftServer server;

    @Shadow public abstract NbtCompound loadPlayerData(ServerPlayerEntity player);
    @Shadow public abstract void sendWorldInfo(ServerPlayerEntity player, ServerWorld world);

    @Inject(method = "onPlayerConnect(Lnet/minecraft/network/ClientConnection;Lnet/minecraft/server/network/ServerPlayerEntity;)V", at = @At("TAIL"))
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        if (player.getGameProfile().getName().equals("#worldloader#")) {
            this.players.remove(player);
        }
    }

    @Inject(method = "sendToAll", at = @At("HEAD"), cancellable = true)
    public void sendToAll(Packet<?> packet, CallbackInfo ci) {
        if (packet.toString().contains("#worldloader#")) {
            ci.cancel();
        }
    }
}