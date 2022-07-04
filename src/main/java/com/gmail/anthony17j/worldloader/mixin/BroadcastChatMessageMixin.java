package com.gmail.anthony17j.worldloader.mixin;

import net.minecraft.network.message.MessageType;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.Text;
import net.minecraft.util.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class BroadcastChatMessageMixin {

    @Inject(method = "broadcast", at = @At("INVOKE"), cancellable = true)
    void broadcast(Text message, RegistryKey<MessageType> typeKey, CallbackInfo ci) {

        if (message.getString().contains("#worldloader#")) {
            ci.cancel();
        }
    }
}
