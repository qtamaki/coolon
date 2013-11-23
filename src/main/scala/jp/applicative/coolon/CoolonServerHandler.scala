package jp.applicative.coolon

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.logging.Level;
import java.util.logging.Logger;


@Sharable
class CoolonServerHandler extends ChannelInboundHandlerAdapter {
	import CoolonServerHandler._
	
    override def channelRead(ctx: ChannelHandlerContext, msg: AnyRef): Unit = {
		print(msg.toString())
        //ctx.write(msg);
    }

    override def channelReadComplete(ctx: ChannelHandlerContext): Unit = {
        ctx.flush();
    }

    override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
        // Close the connection when an exception is raised.
        logger.log(Level.WARNING, "Unexpected exception from downstream.", cause);
        ctx.close();
    }
}

object CoolonServerHandler {
    private def logger = Logger.getLogger(
            classOf[CoolonServerHandler].getName());
  
}