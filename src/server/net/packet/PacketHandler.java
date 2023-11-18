package server.net.packet;

import org.jboss.netty.channel.Channel;

import server.Server;
import server.game.Player;

public interface PacketHandler {

    public void handle(Channel channel, Player optional, Packet packet, Server server);
    
}
