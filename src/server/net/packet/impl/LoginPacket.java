package server.net.packet.impl;

import org.jboss.netty.channel.Channel;

import server.Server;
import server.game.Player;
import server.game.PlayerManager;
import server.net.packet.Packet;
import server.net.packet.PacketHandler;

public class LoginPacket implements PacketHandler {

	private static final int LOGIN_OK = 1;
	private static final int LOGIN_WRONG_NAME_OR_PASS = 2;
	private static final int LOGIN_ALREADY_LOGGED_IN = 4;
	private static final int LOGIN_SERVER_IS_FULL = 3;

	@Override
	public void handle(Channel channel, Player player, Packet packet, Server server) {
		int mid = packet.getInt(); // Read the magic id
		if (mid != 36 && mid != 42) {
			player.disconnect();
			return;
		}
		String user = packet.getString();
		String pass = packet.getString();

		//Check that is there any empty slots available
		if (!(PlayerManager.getInstance().getPlayerCount() <= 200)) {
			Packet respPacket = new Packet(1);
			respPacket.putByte(LOGIN_SERVER_IS_FULL);
			channel.write(respPacket);
			channel.disconnect();
			return;
		}
		if (PlayerManager.getInstance().isPlayerOnline(user)) {
			Packet respPacket = new Packet(1);
			respPacket.putByte(LOGIN_ALREADY_LOGGED_IN); //already logged in!
			channel.write(respPacket);
			channel.disconnect();
			return;
		}

		boolean invalidCredentials = false;
		if (user == null || pass == null) {
			invalidCredentials = true; // TODO : smf integration would go here
		}
		if (invalidCredentials) {
			Packet respPacket = new Packet(1);
			respPacket.putByte(LOGIN_WRONG_NAME_OR_PASS); //wrong username or pass
			channel.write(respPacket); 
			channel.disconnect();
			return;
		}
		if (user != null && pass != null) {
			player = new Player(user, PlayerManager.getInstance().getNextUid(), channel, server);
			PlayerManager.getInstance().register(player);
			Packet loginPacket = new Packet(1);
			loginPacket.putByte(LOGIN_OK);
			channel.write(loginPacket);
			return;
		}
	}

}
