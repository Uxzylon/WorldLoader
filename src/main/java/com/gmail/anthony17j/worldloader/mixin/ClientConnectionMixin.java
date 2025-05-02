package com.gmail.anthony17j.worldloader.mixin;

import com.gmail.anthony17j.worldloader.interfaces.ClientConnectionInterface;
import io.netty.channel.Channel;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Connection.class)
public abstract class ClientConnectionMixin implements ClientConnectionInterface
{
    @Override
    @Accessor
    public abstract void setChannel(Channel channel);
}
