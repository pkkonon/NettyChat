package ru.pkononov.server2

import java.sql.DriverManager

private val selectCommand = "select * from users"
private val users:MutableList<UserImpl> = mutableListOf()


fun main(args: Array<String>) {
   // val server = ThreadPooledServer(9000)
   // Thread(server).start()

    val connection = DriverManager.getConnection("jdbc:mysql://localhost/users", "root", "328220")
    val stmt = connection.createStatement()
    val rs = stmt.executeQuery(selectCommand)

    while(rs.next()){
        users.add(UserImpl(rs.getInt(1), rs.getString(2), rs.getString(3)))
        println(users.get(users.lastIndex))
    }

    connection.close()
}