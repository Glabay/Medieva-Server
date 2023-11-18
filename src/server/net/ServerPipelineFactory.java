package server.net;

import static org.jboss.netty.channel.Channels.pipeline;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;

import server.Server;
import server.net.codec.PacketDecoder;
import server.net.codec.PacketEncoder;

public class ServerPipelineFactory implements ChannelPipelineFactory {
    private Server server = null;
    
    public ServerPipelineFactory(Server server) {
        this.server = server;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = pipeline();
        pipeline.addLast("decoder", new PacketDecoder());
        pipeline.addLast("encoder", new PacketEncoder());
        pipeline.addLast("handler", new ServerHandler(server));
        return pipeline;
    }
}
