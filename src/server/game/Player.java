package server.game;

import java.util.Random;

import org.jboss.netty.channel.Channel;

import server.Server;
import server.net.packet.PacketDispatcher;

import com.threed.jpct.SimpleVector;

public class Player {

	private final PlayerManager playerManager = PlayerManager.getInstance();
	private final Channel channel;
	private final long uid;
	private final boolean initialized;

	private String name = "null";
	private float rotationY = 0;
	private SimpleVector position = new SimpleVector(120, -10.7f, -120);
	
	/**
	 * 0 = mining, 1 = strength
	 */
	private int skills[] = new int[3];
	private int exp[] = new int[3];
	public long lastMinedAt;
	public long miningThrottle;
	public long lastResourceInteraction;
	
	/**
	 * Set to true if the player's animation has changed, and should be dispatched
	 * to the appropriate remote clients.
	 */
	private boolean dispatchAnimationUpdate;
	private int animationId = -1; // = swing, 1 = walk

	/**
	 * Set this to true if the player's server-controlled data has changed,
	 * and then the current data will be dispatched to the client software.
	 */
	public boolean localSessionUpdateFlag = false;
	private int gameCurrency = 0;
	
	private int maximumHealth = 6;
	private int currentHealth = maximumHealth;
	private long lastHealthIncrement = 0L;

	private PacketDispatcher packetDispatcher;
	
	public Player(String username, int uid, Channel channel, Server server) {
		this.name = username;
		this.uid = uid;
		this.channel = channel;
		channel.setAttachment(this);
		this.packetDispatcher = new PacketDispatcher(this);
		this.packetDispatcher.initializeSession(PlayerManager.getInstance().getUsersOnline());

		packetDispatcher.sendMessage("Welcome Pokey RPG.");
		packetDispatcher.sendMessage("Owned and managed by <Glabay>.");
		if (Skills.getExpMulitplier() > 1) {
			packetDispatcher.sendMessage("Bonus EXP Active! EXP Rate is 2x!");
		}
		Account.loadGame(this);
		localSessionUpdateFlag = true;
		initialized = true;
	}
	
	/**
	 * Called every 500ms
	 */
	public void update(Server server) {
		if (!initialized) {
			return;
		}
		if (dispatchAnimationUpdate) {
			dispatchAnimationUpdate = false;
			playerManager.dispatchAnimationUpdate(this, animationId);
		}
		if (currentHealth < maximumHealth && lastHealthIncrement <= System.currentTimeMillis() - 10000) {
			lastHealthIncrement = System.currentTimeMillis();
			currentHealth++; // restore some health
			localSessionUpdateFlag = true; // set the update flag
		}
		if (localSessionUpdateFlag) {
			localSessionUpdateFlag = false;
			packetDispatcher.sendLocalSessionData(gameCurrency, skills, exp);
		}
	}

	public void addGC(int gameCurrency) {
		this.gameCurrency += gameCurrency;
	}

	public void resetPosition() {
		setPosition(9507.0f, 90.9f, 9622.3f);
	}

	public void setPosition(float x, float y, float z) {
		position = new SimpleVector(x, y, z);
		playerManager.dispatchMovementUpdate(this, getX(), getY(), getZ(), rotationY);
		localSessionUpdateFlag = true;
	}

	public int getPrivileges() {
		int rank = isDeveloper() ? 2 : 0;
		if (name.equalsIgnoreCase("Glabay")) {
			rank = 2;
		}
		if (name.equalsIgnoreCase("mod")) {
			rank = 1;
		}
		if (name.equalsIgnoreCase("donor")) {
			rank = 3;
		}
		return rank;
	}

	public void executeCommand(String command) {
		if (command.equalsIgnoreCase("stuck")) {
			resetPosition(); // TODO : this doesn't work yet..
		}
		if (command.equalsIgnoreCase("hit")) {
			beenHit(null, 1);
		}
		if (command.equalsIgnoreCase("die")) {
			beenHit(null, 10);
		}
		if (command.equalsIgnoreCase("online")) {
			packetDispatcher.sendMessage("There are a total of " + playerManager.getPlayerCount() + " online.");
		}
		if (isDeveloper() && command.equalsIgnoreCase("gc")) {
			int gc = new Random().nextInt(10000);
			addGC(gc);
			localSessionUpdateFlag = true;
			packetDispatcher.sendMessage("You spawn " + gc + "GC.");
		}
	}

	public void attackPlayer(Player target) {
		int str = Skills.getLevelForExp(skills[Skills.getSkillID("Strength")]);
		Random random = new Random();
		boolean missed = false;
		if (random.nextInt(10) > 6) {
			missed = random.nextBoolean();
		}
		if (!missed) {
			int damage = random.nextInt(str / 2) + 1;
			target.beenHit(this, damage);
			this.addExp(1, 16 * Skills.getExpMulitplier()); // add 16-32 exp
		}
	}

	public void beenHit(Player attacker, int damage) {
		this.currentHealth = (currentHealth - damage); // deduct some health
		
		// player has died
		if (currentHealth < 1) {
			resetPosition();
			if (attacker != null) {
				packetDispatcher.sendMessage("Oh dear.. " + attacker.getName() + " has killed you!");
			} else {
				packetDispatcher.sendMessage("Oh dear.. You have died!");
			}
			currentHealth = maximumHealth;
			
			// lose $ for dying
			int currencyDeduction = 0;
			if (this.gameCurrency > 100) {
				currencyDeduction = gameCurrency / 10;
			}
			this.gameCurrency -= currencyDeduction;
			
			// loot message
			if (attacker != null) {
				attacker.gameCurrency += currencyDeduction;
				attacker.getPacketDispatcher().sendMessage("You loot " + currencyDeduction + "GC.");
				packetDispatcher.sendMessage(attacker.getName() + " looted " + currencyDeduction + "GC.");
			}
		}
		localSessionUpdateFlag = true;
	}

	public void addExp(int skill, int experience) {
		this.skills[skill] += experience;
		localSessionUpdateFlag = true;
	}

	public int getLevelForExp(int skill) {
		return Skills.getLevelForExp(skills[skill]);
	}

	public int[] getSkills() {
		return skills;
	}

	public boolean isDeveloper() {
		if (name.equalsIgnoreCase("Glabay")) {
			return true;
		}
		return false;
	}

	public String getName() {
		return name;
	}

	public long getUid() {
		return uid;
	}

	public float getX() {
		return position.x;
	}

	public float getY() {
		return position.y;
	}

	public float getZ() {
		return position.z;
	}

	public void setRotationY(float rotationY) {
		this.rotationY = rotationY;
	}

	public float getRotationY() {
		return rotationY;
	}

	public int getGC() {
		return gameCurrency;
	}

	public Channel getChannel() {
		return channel;
	}

	public void disconnect() {
		channel.close();
	}

	public int getCurrentHealth() {
		return currentHealth;
	}

	public SimpleVector getPosition() {
		return position;
	}
	
	public PacketDispatcher getPacketDispatcher() {
		return packetDispatcher;
	}

}