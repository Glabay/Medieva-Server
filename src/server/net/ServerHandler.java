package server.net;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import server.Server;
import server.game.Player;
import server.game.PlayerManager;
import server.net.packet.Packet;
import server.net.packet.PacketDispatcher;
import server.net.packet.PacketManager;
import server.net.packet.impl.LoginPacket;


/**
 * Handle the server events, like a new connection, disconnection, etc.
 */
public class ServerHandler extends SimpleChannelHandler {

	private final Server server;
	private final PacketManager packetManager;
	private final PlayerManager playerManager;

	public ServerHandler(final Server server) {
		this.server = server;
		this.packetManager = new PacketManager(server);
		this.playerManager = PlayerManager.getInstance();
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		Packet packet = (Packet) e.getMessage();
		if (packet != null) {
	        final int opcode = packet.getOpcode();
	        if (opcode == 1) {
	        	/*
	        	 * handle login request
	        	 */
		        new LoginPacket().handle(ctx.getChannel(), null, packet, server);
	        } else {
	        	/*
	        	 * Handle game packet
	        	 */
	        	Player player = (Player) ctx.getChannel().getAttachment();
				packetManager.handle(ctx.getChannel(), player, packet, opcode);
	        }
		}
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
    	Player player = (Player) ctx.getChannel().getAttachment();
		playerManager.unregister(player, server); // Unregister the player
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		if (!(e.getCause() instanceof IOException)) {
			Player player = (Player) ctx.getAttachment();
			if (player != null) {
				playerManager.unregister(player, server);
			}
			Logger.getLogger(PacketDispatcher.class.getName()).log(Level.SEVERE, "Exception caught: ", e.getCause());
		}
	}
	
}