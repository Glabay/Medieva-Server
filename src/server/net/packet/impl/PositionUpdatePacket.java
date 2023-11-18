package server.net.packet.impl;

import org.jboss.netty.channel.Channel;

import server.Server;
import server.game.Player;
import server.game.PlayerManager;
import server.net.packet.Packet;
import server.net.packet.PacketHandler;

public class PositionUpdatePacket implements PacketHandler {

	@Override
	public void handle(Channel channel, Player player, Packet packet, Server server) {
		float x = packet.getFloat();
		float y = packet.getFloat();
		float z = packet.getFloat();
		float rotationY = 0;//packet.getFloat();
		PlayerManager.getInstance().dispatchMovementUpdate(player, x, y, z, rotationY);
	}
	
}
