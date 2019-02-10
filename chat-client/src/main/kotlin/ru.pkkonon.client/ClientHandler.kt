package ru.pkkonon.client

import io.netty.channel.ChannelHandlerContext
import io.netty.util.CharsetUtil
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelInboundHandlerAdapter


class ClientHandler : ChannelInboundHandlerAdapter() {

    override fun channelActive(channelHandlerContext: ChannelHandlerContext) {
        channelHandlerContext.writeAndFlush(Unpooled.copiedBuffer("Netty Rocks!", CharsetUtil.UTF_8))
    }

    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        val m = msg as ByteBuf
        println("Client received: " + m.toString(CharsetUtil.UTF_8))
    }

    override fun exceptionCaught(channelHandlerContext: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        channelHandlerContext.close()
    }
}