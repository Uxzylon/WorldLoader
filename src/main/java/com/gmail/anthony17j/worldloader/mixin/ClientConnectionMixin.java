package com.gmail.anthony17j.worldloader.mixin;

import com.gmail.anthony17j.worldloader.interfaces.ClientConnectionInterface;
import io.netty.channel.Channel;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin implements ClientConnectionInterface
{
    @Accessor
    public abstract void setChannel(Channel channel);
}
