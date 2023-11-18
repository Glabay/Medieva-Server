package server.net.packet;

import java.util.List;

import server.game.Player;
import server.game.PlayerManager;

public class PacketDispatcher {

	private final Player player;
	
	public PacketDispatcher(final Player player) {
		this.player = player;
	}
	
	/**
	 * Initializes the multi-player session for this client.
	 * 
	 * @note This "Should" create a synchronized login state for the new client,
	 * meaning that THIS client should see everything the other clients already see.
	 */
	public synchronized void initializeSession(List<Player> clients) {
		// show <me> to other players
		Packet showMe = new Packet(3);
		showMe.putLong(player.getUid());
		showMe.putFloat(player.getX());
		showMe.putFloat(player.getY());
		showMe.putFloat(player.getZ());
		showMe.putFloat(player.getRotationY());
		showMe.putInt(player.getPrivileges());
		for (Player client : clients) {
			if (client.getUid() != player.getUid()) {
				client.getChannel().write(showMe);
			}
		}

		// show other players to <me>
		for (Player other : clients) {
			if (other.getUid() != player.getUid()) {
				Packet seeOthers = new Packet(3);
				seeOthers.putLong(other.getUid());
				seeOthers.putFloat(other.getX());
				seeOthers.putFloat(other.getY());
				seeOthers.putFloat(other.getZ());
				seeOthers.putFloat(other.getRotationY());
				seeOthers.putInt(other.getPrivileges());
				player.getChannel().write(seeOthers);
			}
		}

		// send position update so this player doesn't render at 0,0,0 client-sided
		PlayerManager.getInstance().dispatchMovementUpdate(player, player.getX(), player.getY(), player.getZ(), player.getRotationY());
	}

	public void sendMessage(String message) {
		Packet packet = new Packet(7);
		packet.putString(message);
		player.getChannel().write(packet);
	}

	public void sendAnimationUpdate(int animationId) {
		Packet packet = new Packet(10);
		packet.putInt(-1);
		packet.putInt(animationId);
		player.getChannel().write(packet);
	}

	/**
	 * Sends a packet to the <player> instance, which will update 
	 * the skills, exp, and game currency values stored on their client.
	 */
	public void sendLocalSessionData(int gameCurrency, int[] skills, int[] exp) {
		Packet packet = new Packet(2);
		packet.putInt(gameCurrency);
		
		// send stats + exp
		packet.putInt(skills.length);
		for (int i = 0; i < skills.length; i++) {
			packet.putInt(skills[i]);
			packet.putInt(exp[i]);
		}
		player.getChannel().write(packet);
	}

}
