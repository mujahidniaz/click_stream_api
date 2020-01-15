package analytics_business_logic
import redis.RedisCache;
import Java.JavaHelper;
import mysql.MySQLConnector;
import models.ClientResult
import java.util.Date
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import Java.Constants;
class AnalyticsAPI {
  val redis = new RedisCache();
  val helper = new JavaHelper();
  val mysql = new MySQLConnector();

  mysql.connect();
  def HandlePostRequest(user: String, timestamp: String, action: String): Unit = /// This function handles the POST call of api which is POST /analytics?timestamp={millis_since_epoch}&user={username}&{click|impression}
    {
      val keys = helper.generateKey(timestamp, user, action) // Generates Hash values (keys) for the aggregates to store in Redis Cache
      println("Keys Generated : " + keys(0) + "/" + keys(1))
      redis.writeToRedisCache(keys(0).toString(), helper.getValue(timestamp, user)) // Writing the Impressions and Clicks values to Cache which in fact increments the counts of each Key if there  is new value
      redis.writeToRedisCache(keys(1).toString(), user.toUpperCase()) /// Writing Cache for  users values
      println("Key Added into Redis")
      println("Now Adding in MySQL database")

      var query = helper.generateQuery(timestamp, user, action) /// generating query to dump raw data in sql
      mysql.executeQuery(query) // inserting data to mysql
    }
  def getCacheObject(): RedisCache = { return redis }; /// Returns the Cache Object so that we can use the same single object in other classes instread of making new one
  def getClientResult(timestmp: String): ClientResult = /// This function handles the GET request GET /analytics?timestamp={millis_since_epoch}
    {
      try {

        println()
        var result = new ClientResult() // Create Result Object that will be returned to user
        var dt = helper.DateObjectFromEpoch(timestmp)
        var timestamp = helper.getTimeStampFromEpoch(timestmp) // Convert epoch to readable timestamp
        println("GET request for " + timestamp)
        if (helper.GetHoursDifference(dt) < Constants.CACHE_RETENTION_HOURS) { /// If the  records are not older than retention period (24 hours) currently
          result.unique_users_=(redis.readRedisCache(helper.getHourString(timestamp) + "-U")); // get users
          result.Clicks_=(redis.readRedisCache(helper.getHourString(timestamp) + "-C")); /// get Clicks
          result.Impressions_=(redis.readRedisCache(helper.getHourString(timestamp) + "-I")); /// get Impressions
        } else {                                                                             /// If we get timestamp which is older than 24 hours our cache will not hold that so we have to query the MySQL database 

          try {

            val res = mysql.getResultSet(helper.generateUserCountQuery(dt))        // Get user count from MySQL
            while (res.next()) {
              result.Users = res.getInt(0);      
            }

          } catch {
            case e: Exception => result.Users = 0
          }
          try {

            val res = mysql.getResultSet(helper.generateClickImpressionCountQuery(dt))      /// Get Clicks and Impressions from MySQL
            while (res.next()) {
              try { result.Clicks = res.getInt(0) } catch { case e: Exception => result.Clicks = 0 }
              try { result.Impressions = res.getInt(1) } catch { case e: Exception => result.Impressions = 0 }

            }

          } catch {
            case e: Exception => { e.printStackTrace() }
          }
        }
        return result

      } catch {
        case e: Exception => { e.printStackTrace(); return null }
      }
    }
}