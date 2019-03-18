package ru.pkkonon.server

import com.sun.net.httpserver.HttpServer
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.*;
import java.nio.charset.StandardCharsets
import java.util.List;
import java.util.regex.*;


class FilterHandler : MessageToMessageDecoder<DefaultFullHttpRequest>() {

    @Throws(Exception::class)
    override fun decode(ctx: ChannelHandlerContext, request: DefaultFullHttpRequest, out: MutableList<Any>) {

        if (request.method() !== HttpMethod.GET) {
            sendError(ctx, "метод к данному ресурсу не применим", HttpResponseStatus.NOT_ACCEPTABLE)
            return
        }

        var url: String? = request.uri
        url = url?.toLowerCase() ?: ""
        if (!url.startsWith(PREFIX_URL)) {
            sendError(ctx, "ресурс не найден", HttpResponseStatus.NOT_FOUND)
            return
        }

        val matcher = URL_PATTERN_FILTER.matcher(url)
        if (!matcher.find())
            sendError(ctx, "некорретно указаны параметры", HttpResponseStatus.BAD_REQUEST)

        try {
            val firstNumber = java.lang.Long.parseLong(matcher.group(1))
            val secondNumber = java.lang.Long.parseLong(matcher.group(2))

            request.headers().add("firstNumber", firstNumber)
            request.headers().add("secondNumber", secondNumber)
            out.add(request)
            request.retain()

        } catch (e: NumberFormatException) {
            sendError(ctx, "неверный формат параметров", HttpResponseStatus.BAD_REQUEST)
        }

    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        sendError(ctx, "ошибка сервера:" + cause.message, HttpResponseStatus.INTERNAL_SERVER_ERROR)
    }

    companion object {
        private val PREFIX_URL = "/sum"
        private val URL_PATTERN_FILTER = Pattern.compile("$PREFIX_URL(?:\\?first=(\\d+)&second=(\\d+))")
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