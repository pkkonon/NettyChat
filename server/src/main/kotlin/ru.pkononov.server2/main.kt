package ru.pkononov.server2

fun main(args: Array<String>) {
    val server = ThreadPooledServer(9000)
    Thread(server).start()
}