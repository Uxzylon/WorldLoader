package com.gmail.anthony17j.worldloader.fakeplayer;

import com.gmail.anthony17j.worldloader.interfaces.ClientConnectionInterface;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;

public class ClientConnectionFake extends ClientConnection {

    public ClientConnectionFake(NetworkSide p)
    {
        super(p);
        ((ClientConnectionInterface)this).setChannel(new EmbeddedChannel());
    }
}
