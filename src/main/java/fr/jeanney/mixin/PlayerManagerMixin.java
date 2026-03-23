package fr.jeanney.mixin;

import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static fr.jeanney.WorldLoader.WORLD_LOADER_NAME_PREFIX;

@Mixin(PlayerList.class)
public abstract class PlayerManagerMixin {

    @Shadow @Final private List<ServerPlayer> players;

    @Inject(method = "broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V", at = @At("HEAD"), cancellable = true)
    public void broadcastSystemMessage(Component component, boolean bl, CallbackInfo ci) {
        if (component.toString().contains(WORLD_LOADER_NAME_PREFIX)) {
            ci.cancel();
        }
    }

    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    public void placeNewPlayer(Connection connection, ServerPlayer serverPlayer, CommonListenerCookie commonListenerCookie, CallbackInfo ci) {
        if (serverPlayer.getGameProfile().name().startsWith(WORLD_LOADER_NAME_PREFIX)) {
            this.players.remove(serverPlayer);
        }
    }

    @Inject(method = "broadcastAll(Lnet/minecraft/network/protocol/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void broadcastAll(Packet<?> packet, CallbackInfo ci) {
        if (packet.toString().contains(WORLD_LOADER_NAME_PREFIX)) {
            ci.cancel();
        }
    }
}