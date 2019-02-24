package ru.pkkonon.server

import io.netty.channel.ChannelInitializer
import java.net.InetSocketAddress
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.bootstrap.ServerBootstrap


fun main(args: Array<String>) {
    val group = NioEventLoopGroup()

    try {
        val serverBootstrap = ServerBootstrap()
        serverBootstrap.group(group)
        serverBootstrap.channel(NioServerSocketChannel::class.java)
        serverBootstrap.localAddress(InetSocketAddress("localhost", 6666))

        serverBootstrap.childHandler(object : ChannelInitializer<SocketChannel>() {
            @Throws(Exception::class)
            override fun initChannel(socketChannel: SocketChannel) {
                socketChannel.pipeline().addLast(HelloServerHandler())
            }
        })
        val channelFuture = serverBootstrap.bind().sync()
        channelFuture.channel().closeFuture().sync()
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        group.shutdownGracefully().sync()
    }
}