package jp.applicative.coolon

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import io.netty.handler.codec.DelimiterBasedFrameDecoder
import io.netty.handler.codec.Delimiters
import io.netty.handler.codec.string.StringDecoder
import io.netty.handler.codec.string.StringEncoder

class Coolon(val port: Int) {
    def run(): Unit = {
        // Configure the server.
        val bossGroup = new NioEventLoopGroup();
        val workerGroup = new NioEventLoopGroup();
        try {
            val b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(classOf[NioServerSocketChannel])
             .option(ChannelOption.SO_BACKLOG, new Integer(100))
             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new ChannelInitializer[SocketChannel]() {
                 override def initChannel(ch: SocketChannel):Unit = {
			        // Add the text line codec combination first,
                   val x = new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter(): _*);
                   ch.pipeline.addLast("framer", x);
			        // the encoder and decoder are static as these are sharable
			        ch.pipeline.addLast("decoder", new StringDecoder());
			        ch.pipeline.addLast("encoder", new StringEncoder());
			
			        // and then business logic.
			        ch.pipeline.addLast("handler", new CoolonServerHandler());                 }
             });

            // Start the server.
            val f = b.bind(port).sync();

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }  
}

object Coolon {

  def port(args: Array[String]) = 
        if (args.length > 0) {
            Integer.parseInt(args(0))
        } else {
            8080
        }
  
  def main(args: Array[String]): Unit = {
        new Coolon(port(args)).run();
  }
}