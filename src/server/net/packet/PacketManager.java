package server.net.packet;

import org.jboss.netty.channel.Channel;

import server.Server;
import server.game.Player;
import server.net.packet.impl.AddMessagePacket;
import server.net.packet.impl.ClientButtonPacket;
import server.net.packet.impl.PlayerInteractionPacket;
import server.net.packet.impl.PositionUpdatePacket;

public class PacketManager {

	private final Server server;

	public PacketManager(final Server server) {
		this.server = server;
	}

	public void handle(Channel channel, Player player, Packet packet, int opcode) {
		switch (opcode) {
		case 2: //Update player position
			new PositionUpdatePacket().handle(channel, player, packet, server);
			break;
		case 3: //unused ATM
			break;
		case 4: //Add chatbox message
			new AddMessagePacket().handle(channel, player, packet, server);
			break;
		case 5: //unused ATM
			break;
		case 8: //Player interaction
			new PlayerInteractionPacket().handle(channel, player, packet, server);
			break;
		case 9: //Clicked some client button
			new ClientButtonPacket().handle(channel, player, packet, server);
			break;
		default:
			break;
		}
	}
}
