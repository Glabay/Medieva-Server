package server.net.packet.impl;

import org.jboss.netty.channel.Channel;

import server.Server;
import server.game.Player;
import server.game.PlayerManager;
import server.net.packet.Packet;
import server.net.packet.PacketHandler;
import utils.MathUtils;

public class PlayerInteractionPacket implements PacketHandler {

	private final int ATTACK = 2;
	private final int FOLLOW = 4;
	private final int TRADE = 8;

	@Override
	public void handle(Channel channel, Player player, Packet packet, Server server) {
		int uid = packet.getInt();
		int hotKey = packet.getInt();

		Player target = PlayerManager.getInstance().getPlayerByUid(uid);
		if (target == null) {
			return;
		}
		
		int networkErrorRoom = 2; // give a little bit of extra space to the distance check,
		// incase out clients aren't synchronized due to missing packet data
		// this is why movement needs to be handled via UDP - and then we'll
		// implement some jPCT background world on the server so we can validate movement requests
		// and collisions..
		if (MathUtils.isInRange(8 + networkErrorRoom, player.getPosition(), target.getPosition())) {
			switch (hotKey) {
			case ATTACK:
				player.getPacketDispatcher().sendMessage("You attack " + target.getName() + "!");
				target.getPacketDispatcher().sendMessage(player.getName() + " has attacked you!");
				player.attackPlayer(target);
				break;
			case FOLLOW:
				player.getPacketDispatcher().sendMessage("You begin following " + target.getName() + ".");
				target.getPacketDispatcher().sendMessage(player.getName() + " has started following you.");
				break;
			case TRADE:
				player.getPacketDispatcher().sendMessage("You send a trade request to " + target.getName() + "..");
				target.getPacketDispatcher().sendMessage(player.getName() + " wishes to trade with you.");
				break;
			}
		}
	}
	
}