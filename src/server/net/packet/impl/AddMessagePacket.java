package server.net.packet.impl;

import org.jboss.netty.channel.Channel;

import server.Server;
import server.game.Player;
import server.game.PlayerManager;
import server.net.packet.Packet;
import server.net.packet.PacketHandler;

public class AddMessagePacket implements PacketHandler {

	@Override
	public void handle(Channel channel, Player player, Packet packet, Server server) {
		String message = packet.getString();
		if (message.startsWith("::")) {
			player.executeCommand(message.substring(2));
			return;
		}
		PlayerManager.getInstance().sendMessageToAll(player.getName() + ": " + message);
	}

}