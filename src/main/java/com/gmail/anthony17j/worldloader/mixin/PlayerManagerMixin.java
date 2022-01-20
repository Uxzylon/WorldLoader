package com.gmail.anthony17j.worldloader.mixin;

import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

    @Inject(method = "onPlayerConnect(Lnet/minecraft/network/ClientConnection;Lnet/minecraft/server/network/ServerPlayerEntity;)V", at = @At("RETURN"))
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity entity, CallbackInfo info) {

        Set<ServerPlayerEntity> set = new HashSet<>();

        ServerPlayerEntity player = entity.getServer().getPlayerManager().getPlayer("#worldloader#");
        if (player != null) {
            set.add(new ServerPlayerEntity(entity.getServer(), entity.getWorld(), player.getGameProfile()));
        }

        PlayerListS2CPacket packet = new PlayerListS2CPacket(PlayerListS2CPacket.Action.REMOVE_PLAYER, set);
        entity.getServer().getPlayerManager().sendToAll(packet);

    }
}