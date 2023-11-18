package server.net.codec;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import server.net.packet.Packet;

public class PacketEncoder extends OneToOneEncoder {

	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel chl, Object obj) throws Exception {
		Packet packet = (Packet) obj;

		//Create a buffer
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();

		//Write the packet's length
		buffer.writeShort(packet.getReadableByteCount());

		//Write the opcode and the packet
		buffer.writeByte(packet.getOpcode());
		buffer.writeBytes(packet.getDataByteArray());

		return buffer;
	}

}