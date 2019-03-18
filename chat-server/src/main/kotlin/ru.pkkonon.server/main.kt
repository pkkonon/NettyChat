package ru.pkkonon.server

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpRequestDecoder
import io.netty.handler.codec.http.HttpResponseEncoder


fun main(args: Array<String>) {
    val workerGroup = NioEventLoopGroup()
    val bossGroup = NioEventLoopGroup(1)
    var channelFuture: ChannelFuture? = null
    try {
        val server = ServerBootstrap()
            .group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel::class.java)
            .childHandler(object : ChannelInitializer<SocketChannel>() {
                // private val corsConfig = CorsConfig(CorsConfigBuilder)

                @Throws(Exception::class)
                public override fun initChannel(ch: SocketChannel) {
                    ch.pipeline()
                        .addLast(HttpResponseEncoder())
                        .addLast(HttpRequestDecoder())
                        .addLast(HttpObjectAggregator(Integer.MAX_VALUE))
                        // .addLast(CorsHandler(corsConfig))
                        .addLast(FilterHandler())
                        .addLast(WorkerHandler())
                }
            })
            .option(ChannelOption.SO_BACKLOG, 500)
            .childOption(ChannelOption.SO_KEEPALIVE, true)

        channelFuture = server.bind(5000).sync()

        channelFuture!!.channel().closeFuture().sync()

    } finally {
        workerGroup.shutdownGracefully()
        channelFuture?.channel()?.close()?.awaitUninterruptibly()
    }
}

