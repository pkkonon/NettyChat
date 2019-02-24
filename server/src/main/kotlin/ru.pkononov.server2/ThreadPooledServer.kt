package ru.pkononov.server2

import java.net.ServerSocket
import java.util.concurrent.Executors
import java.io.IOException
import java.net.Socket


class ThreadPooledServer(port : Int) : Runnable{

    protected val serverPort = port
    protected var serverSocket:ServerSocket? = null
    protected var isStopped = false
    protected var runningThread : Thread? = null
    protected val threadPool = Executors.newFixedThreadPool(500)

    override fun run() {
        synchronized(this){
            this.runningThread = Thread.currentThread()
        }
        openServerSocket()
        while (!isStopped) {
            var clientSocket: Socket? = null
            try {
                clientSocket = this.serverSocket!!.accept()
            } catch (e: IOException) {
                if (isStopped) {
                    println("Server Stopped.")
                    break
                }
                throw RuntimeException(
                    "Error accepting client connection", e
                )
            }

            this.threadPool.execute(
                WorkerRunnable(
                    clientSocket,
                    "Thread Pooled Server"
                )
            )
        }
        this.threadPool.shutdown()
        println("Server Stopped.")

    }

    @Synchronized
    fun isStop():Boolean{
        return isStopped
    }

    @Synchronized
    fun stop() {
        this.isStopped = true
        try {
            this.serverSocket!!.close()
        } catch (e: IOException) {
            throw RuntimeException("Error closing server", e)
        }

    }

    private fun openServerSocket() {
        try {
            this.serverSocket = ServerSocket(this.serverPort)
        } catch (e: IOException) {
            throw RuntimeException("Cannot open port $serverPort", e)
        }

    }

}