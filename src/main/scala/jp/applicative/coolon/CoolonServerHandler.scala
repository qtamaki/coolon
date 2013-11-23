package jp.applicative.coolon

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import java.util.logging.Level
import java.util.logging.Logger
import io.netty.channel.SimpleChannelInboundHandler
import java.net.InetAddress
import java.util.Date
import io.netty.channel.ChannelFutureListener

@Sharable
class CoolonServerHandler extends SimpleChannelInboundHandler[String] {
  import CoolonServerHandler._
  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    // Send greeting for a new connection.
    ctx.write(
      "Welcome to " + InetAddress.getLocalHost().getHostName() + "!\r\n");
    ctx.write("It is " + new Date() + " now.\r\n");
    ctx.flush();
  }

  private def response(request: String): String = {
    if (request.isEmpty()) {
      "Please type something.\r\n"
    } else if (isClose(request)) {
      "Have a good day!\r\n"
    } else {
      "Did you say '" + request + "'?\r\n"
    }
  }

  private def isClose(request: String): Boolean = "bye".equals(request.toLowerCase())

  override def channelRead0(ctx: ChannelHandlerContext, msg: String): Unit = {
    // We do not need to write a ChannelBuffer here.
    // We know the encoder inserted at TelnetPipelineFactory will do the conversion.
    val future = ctx.write(response(msg))

    // Close the connection after sending 'Have a good day!'
    // if the client has sent 'bye'.
    if (isClose(msg)) {
      future.addListener(ChannelFutureListener.CLOSE);
    }
  }

  override def channelReadComplete(ctx: ChannelHandlerContext): Unit = {
    ctx.flush();
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    logger.log(
      Level.WARNING,
      "Unexpected exception from downstream.", cause);
    ctx.close();
  }
}

object CoolonServerHandler {
  private def logger = Logger.getLogger(
    classOf[CoolonServerHandler].getName());

}