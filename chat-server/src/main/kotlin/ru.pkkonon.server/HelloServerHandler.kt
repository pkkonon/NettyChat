package ru.pkkonon.server

import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil.close
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelFutureListener
import io.netty.buffer.Unpooled
import io.netty.util.CharsetUtil
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelInboundHandlerAdapter


class HelloServerHandler : ChannelInboundHandlerAdapter() {

    @Throws(Exception::class)
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        val inBuffer = msg as ByteBuf

        val received = inBuffer.toString(CharsetUtil.UTF_8)
        println("Server received: $received")

        ctx.write(Unpooled.copiedBuffer("Hello $received", CharsetUtil.UTF_8))
    }

    @Throws(Exception::class)
    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
            .addListener(ChannelFutureListener.CLOSE)
    }

    @Throws(Exception::class)
    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }
}