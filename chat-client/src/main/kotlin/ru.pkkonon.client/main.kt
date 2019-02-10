package ru.pkkonon.client

import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import java.net.InetSocketAddress
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.EventLoopGroup
import io.netty.channel.socket.SocketChannel


fun main(args:Array<String>) {
    val group = NioEventLoopGroup()
    try {
        val clientBootstrap = Bootstrap()

        clientBootstrap.group(group)
        clientBootstrap.channel(NioSocketChannel::class.java)
        clientBootstrap.remoteAddress(InetSocketAddress("localhost", 9999))
        clientBootstrap.handler(object : ChannelInitializer<SocketChannel>() {
            @Throws(Exception::class)
            override fun initChannel(socketChannel: SocketChannel) {
                socketChannel.pipeline().addLast(ClientHandler())
            }
        })
        val channelFuture = clientBootstrap.connect().sync()
        channelFuture.channel().closeFuture().sync()
    } finally {
        group.shutdownGracefully().sync()
    }
}