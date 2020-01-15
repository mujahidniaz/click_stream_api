package mysql

import java.sql._;
import java.sql.DriverManager

class MySQLConnector {
  val driver = "com.mysql.jdbc.Driver"
  val url = "jdbc:mysql://localhost/mysql?useSSL=false"
  val username = "root"
  val password = "root"

  def MySQLConnector() {

    this.connect();

  }
  // there's probably a better way to do this
  var connection: Connection = null
  def connect(): Boolean = {
    try {
      // make the connection
      Class.forName(driver)
      connection = DriverManager.getConnection(url, username, password)
      println("MySQL connection Successful")
      return true;
    } catch {
      case e: Exception => { println("MySQL Connection Failed"); return false }

    }
  }

  def executeQuery(query: String): Boolean = {
    try {

      val st = connection.createStatement()
      st.executeUpdate(query)
      return true;
    } catch {
      case e: Exception => { e.printStackTrace(); return false }
    }

  }

  def getResultSet(query: String): ResultSet = {
    try {

      val st = connection.createStatement()
      val result = st.executeQuery(query)
      return result;
    } catch {
      case e: Exception => return null
    }

  }
}

