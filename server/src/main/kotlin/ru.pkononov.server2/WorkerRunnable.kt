package ru.pkononov.server2

import java.io.*
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.io.IOException


class WorkerRunnable(clientSocket: Socket, serverText: String) : Runnable {

    protected var clientSocket: Socket? = null
    protected var serverText: String? = null

    init {
        this.clientSocket = clientSocket
        this.serverText = serverText
    }

    override fun run() {
        try {
            val input = clientSocket!!.getInputStream()
            val output = PrintWriter(clientSocket!!.getOutputStream())
            val time = System.currentTimeMillis()


            var url = ClassLoader.getSystemClassLoader().getResource("index.html").toURI()


            val path = Paths.get(url).toString()

            val contentBuilder = StringBuilder()

            try {
                Files.lines(Paths.get(path), StandardCharsets.UTF_8)
                    .use { stream -> stream.forEach { s -> contentBuilder.append(s).append("\n") } }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            output.println("HTTP/1.1 200 OK")
            output.println("Content-type: text/html")
            output.println("\n\n")
            output.println(contentBuilder)
            output.flush()
            output.close()
            input.close()
            println("Request processed: $time")
        } catch (e: IOException) {
            //report exception somewhere.
            e.printStackTrace()
        }

    }
}

