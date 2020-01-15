package mysql

import java.sql._;
import java.sql.DriverManager
import Java.Constants
class MySQLConnector {
  
  


  def MySQLConnector() {

    this.connect();

  }
  // there's probably a better way to do this
  var connection: Connection = null
  def connect(): Boolean = {
    try {
      // make the connection
      Class.forName(Constants.MYSQL_JDBC_DRIVER)
      connection = DriverManager.getConnection(Constants.MYSQL_JDBC_URL, Constants.MYSQL_USERNAME, Constants.MYSQL_PASSWORD)
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

