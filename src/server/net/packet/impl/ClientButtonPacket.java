package server.net.packet.impl;

import org.jboss.netty.channel.Channel;

import server.Server;
import server.game.Player;
import server.net.packet.Packet;
import server.net.packet.PacketHandler;

public class ClientButtonPacket implements PacketHandler {

	@Override
	public void handle(Channel channel, Player player, Packet packet, Server server) {
		int button_id = packet.getInt();

		switch (button_id) {
			case 0: // logout request
				player.getPacketDispatcher().sendMessage("You have pressed the logout button, this is a dummy button.");
				break;
			default:
				player.getPacketDispatcher().sendMessage("Unregistered button pressed:\nButton: " + button_id + "\nPressed by: " + player.getName());
				break;

		}
	}

}