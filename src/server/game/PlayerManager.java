package server.game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import server.Server;
import server.net.packet.Packet;

/**
 * Manages all connected players, the player update cycle, and contains a suitable
 * amount of public accessible multi-player packets.
 */
public class PlayerManager {

	private static PlayerManager singleton;

	private AtomicInteger uid = new AtomicInteger(0);
	private List<Player> usersOnline = new ArrayList<>();

	public PlayerManager() {
		System.out.println("Player manager initialized.");
	}

	public void update(final Server server) {
		for (Player player : usersOnline) {
			if (player != null) {
				player.update(server);
			}
		}
	}

	public Player getPlayerByUid(int uid) {
		for (Player player : usersOnline) {
			if (player.getUid() == uid) {
				return player;
			}
		}
		return null;
	}

	public boolean isPlayerOnline(String name) {
		for (Player player : usersOnline) {
			if (player != null && player.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public void register(Player player) {
		usersOnline.add(player);
	}

	public void unregister(Player player, Server server) {
		if (player != null) {
			Account.saveGame(player);
			dispatchClientUnregister(player.getUid());
			usersOnline.remove(player);
		}
	}

	public int getNextUid() {
		return uid.getAndIncrement();
	}

	public int getPlayerCount() {
		return usersOnline.size();
	}

	public List<Player> getUsersOnline() {
		return usersOnline;
	}

	public void dispatchMovementUpdate(Player player, float x, float y, float z, float rotationY) {
		dispatchAnimationUpdate(player, 1);
		long uid = player.getUid();
		Packet packet = new Packet(5);
		packet.putLong(uid);
		packet.putFloat(x);
		packet.putFloat(y);
		packet.putFloat(z);
		packet.putFloat(rotationY);
		for (Player other : usersOnline) {
			if (other.getUid() != uid) {
				other.getChannel().write(packet);
			}
		}
		//System.out.println("debug: "+player.getName()+" movement update sent..");
	}

	public void dispatchAnimationUpdate(Player player, int animationId) {
		Packet localUpdate = new Packet(10);
		localUpdate.putLong(player.getUid());
		localUpdate.putInt(animationId);
		player.getChannel().write(localUpdate);// local animation update
		
		Packet globalUpdate = new Packet(10); // global animation update
		globalUpdate.putLong(player.getUid());
		globalUpdate.putInt(animationId);
		for (Player p : usersOnline) {
			if (p.getUid() != player.getUid()) {
				p.getChannel().write(globalUpdate);
				//System.out.println("debug: "+player.getName()+" to "+p.getName()+" health animation update sent..");
			}
		}
	}

	public void dispatchClientUnregister(long l) {
		System.out.println("Client (uid:"+l+") has left.");
		Packet packet = new Packet(4);
		packet.putLong(l);
		for (Player p : usersOnline) {
			if (p.getUid() != l) {
				p.getChannel().write(packet);
			}
		}
	}
	public void sendMessageToAll(String message) {
		for (Player p : usersOnline) {
			p.getPacketDispatcher().sendMessage(message);
		}
	}

	public static PlayerManager getInstance() {
		if (singleton == null) {
			singleton = new PlayerManager();
		}
		return singleton;
	}

}