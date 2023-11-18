package server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import server.game.PlayerManager;
import server.net.ServerPipelineFactory;

public class Server implements Runnable {

	private final ScheduledExecutorService serverExecutor = Executors.newScheduledThreadPool(1);

	public static void main(String[] args) {
		new Server();
	}

	public Server() {
		try {
			System.out.println("Initializing server bootstrap..");
			ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
			bootstrap.setPipelineFactory(new ServerPipelineFactory(this));
			bootstrap.bind(new InetSocketAddress(2513));
			System.out.println("Server is bound to network..");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Port unavailable!");
			System.exit(0);
		}
		System.out.println("Ready for players!");
		this.serverExecutor.scheduleAtFixedRate(this, 0, 500, TimeUnit.MILLISECONDS);
	}

	@Override
	public void run() {
		try {
			PlayerManager.getInstance().update(this);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error updating server!");
			System.exit(0);
		}
	}

}