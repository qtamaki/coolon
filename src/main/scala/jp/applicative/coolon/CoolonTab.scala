package jp.applicative.coolon

import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.DelimiterBasedFrameDecoder
import io.netty.handler.codec.Delimiters
import io.netty.handler.codec.string.StringDecoder
import io.netty.handler.codec.string.StringEncoder

class CoolonTab(val host: String, val port: Int, val firstMessageSize: Int) {

   def run():Unit = {
        // Configure the client.
        val group = new NioEventLoopGroup();
        try {
            val b = new Bootstrap();
            b.group(group)
             .channel(classOf[NioSocketChannel]);
             b.option(ChannelOption.TCP_NODELAY, scala.Boolean.box(true))
             .handler(new ChannelInitializer[SocketChannel]() {
                 override def initChannel(ch: SocketChannel):Unit = {
			        // Add the text line codec combination first,
                   val x = new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter(): _*);
                   ch.pipeline.addLast("framer", x);
			        // the encoder and decoder are static as these are sharable
			        ch.pipeline.addLast("decoder", new StringDecoder());
			        ch.pipeline.addLast("encoder", new StringEncoder());
			
			        // and then business logic.
			        ch.pipeline.addLast("handler", new CoolonClientHandler(firstMessageSize));                 
                 }
             });

            // Start the client.
            val ch = b.connect(host, port).sync().channel();

            while(true) {
              println("> ")
              val line = readLine
              val w = ch.writeAndFlush(line)
            }
            ch.closeFuture().sync()
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }
}

object CoolonTab {
	def firstMessageSize(args: Array[String]) = 
        if (args.length == 3) {
            Integer.parseInt(args(2))
        } else {
            256
        }

	def main(args: Array[String]): Unit = {
        // Print usage if no argument is specified.
        if (args.length < 2 || args.length > 3) {
            System.err.println(
                    "Usage: " + classOf[CoolonTab].getSimpleName() +
                    " <host> <port> [<first message size>]");
            return;
        }

        // Parse options.
        val host = args(0)
        val port = Integer.parseInt(args(1))
        new CoolonTab(host, port, firstMessageSize(args)).run();
  }
  
}