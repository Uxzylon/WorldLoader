package com.gmail.anthony17j.worldloader.mixin;

import com.gmail.anthony17j.worldloader.WorldLoader;
import net.minecraft.network.MessageType;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PlayerManager.class)
public class BroadcastChatMessageMixin {

    @Inject(method = "broadcast", at = @At("INVOKE"), cancellable = true)
    void filterBroadCastMessages(Text message, MessageType type, UUID sender, CallbackInfo ci) {
        String username = ((LiteralText)((TranslatableText) message).getArgs()[0]).getString();

        if (username.equals("#worldloader#")) {
            ci.cancel();
        }
    }
}
