package com.gmail.anthony17j.worldloader.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

    @Shadow @Final private List<ServerPlayerEntity> players;
    
    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        if (player.getGameProfile().getName().equals("playerName")) {
            this.players.remove(player);
        }
    }

    @Inject(method = "sendToAll", at = @At("HEAD"), cancellable = true)
    public void sendToAll(Packet<?> packet, CallbackInfo ci) {
        if (packet.toString().contains("playerName")) {
            ci.cancel();
        }
    }
}