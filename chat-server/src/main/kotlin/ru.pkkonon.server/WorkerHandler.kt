package ru.pkkonon.server


import com.sun.net.httpserver.HttpServer
import io.netty.buffer.*
import io.netty.channel.*
import io.netty.handler.codec.http.*

import java.nio.charset.StandardCharsets


class WorkerHandler : ChannelInboundHandlerAdapter() {
    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
       sendError(ctx, "ошибка сервера:" + cause.message, HttpResponseStatus.INTERNAL_SERVER_ERROR)
    }

    @Throws(Exception::class)
    override fun channelRead(ctx: ChannelHandlerContext, obj: Any) {
        val request = obj as DefaultFullHttpRequest

        val firstNumber = Integer.parseInt(request.headers().get("firstNumber"))
        val secondNumber = Integer.parseInt(request.headers().get("secondNumber"))

        val sum = (firstNumber + secondNumber).toLong()

        val content = Unpooled.copiedBuffer("результат:$sum", StandardCharsets.UTF_8)
        val response = DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1,
            HttpResponseStatus.OK,
            content
        )

        response.headers().set(HttpHeaders.Names.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain")
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes())
        response.headers().set(HttpHeaders.Names.ACCEPT_CHARSET, StandardCharsets.UTF_8.name())

        val channelFuture = ctx.writeAndFlush(response)
        channelFuture.addListener(ChannelFutureListener.CLOSE)

        request.release()
    }

    fun sendError(ctx: ChannelHandlerContext, errorMessage: String, status: HttpResponseStatus) {
        val content = Unpooled.copiedBuffer(errorMessage, StandardCharsets.UTF_8)
        val response = DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1,
            status,
            content
        )

        response.headers().set(HttpHeaders.Names.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain")
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes())
        response.headers().set(HttpHeaders.Names.ACCEPT_CHARSET, StandardCharsets.UTF_8.name())

        val channelFuture = ctx.writeAndFlush(response)
        channelFuture.addListener(ChannelFutureListener.CLOSE)
    }
}