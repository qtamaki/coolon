package jp.applicative.coolon

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import java.util.logging.Level
import java.util.logging.Logger;
import io.netty.channel.SimpleChannelInboundHandler

class CoolonClientHandler(val firstMessageSize: Int) extends SimpleChannelInboundHandler[String] {
  import CoolonClientHandler._
  if (firstMessageSize <= 0) {
    throw new IllegalArgumentException("firstMessageSize: " + firstMessageSize)
  }
  val firstMessage = Unpooled.buffer(firstMessageSize)
  for (i <- 0 to firstMessage.capacity()) {
    firstMessage.writeByte(i);
  }

  override def channelRead0(ctx: ChannelHandlerContext, msg: String):Unit = {
		  println(msg)
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    logger.log(
      Level.WARNING,
      "Unexpected exception from downstream.", cause);
    ctx.close();
  }
}

object CoolonClientHandler {

  val logger = Logger.getLogger(
    classOf[CoolonClientHandler].getName());

}