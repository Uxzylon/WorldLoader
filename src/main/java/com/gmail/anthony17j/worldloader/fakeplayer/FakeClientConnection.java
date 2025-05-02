package com.gmail.anthony17j.worldloader.fakeplayer;

import com.gmail.anthony17j.worldloader.interfaces.ClientConnectionInterface;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.protocol.PacketFlow;

public class FakeClientConnection extends Connection {

    public FakeClientConnection(PacketFlow p)
    {
        super(p);
        ((ClientConnectionInterface)this).setChannel(new EmbeddedChannel());
    }

    @Override
    public void setReadOnly()
    {
    }

    @Override
    public void handleDisconnection()
    {
    }

    @Override
    public void setListenerForServerboundHandshake(PacketListener packetListener)
    {
    }

    @Override
    public <T extends PacketListener> void setupInboundProtocol(ProtocolInfo<T> protocolInfo, T packetListener)
    {
    }
}
