package analytics_business_logic
import redis.RedisCache;
import Java.JavaHelper;
import mysql.MySQLConnector;
import models.ClientResult

class AnalyticsAPI {
  val redis = new RedisCache();
  val helper = new JavaHelper();
  val mysql = new MySQLConnector();
  mysql.connect();
  def HandlePostRequest(user: String, timestamp: String, action: String): Unit =
    {
      val keys = helper.generateKey(timestamp, user, action)
      println("Keys Generated : " + keys(0) + "/" + keys(1))
      redis.writeToRedisCache(keys(0).toString(), helper.getValue(timestamp, user))
      redis.writeToRedisCache(keys(1).toString(), user.toUpperCase())
      println("Key Added into Redis")
      println("Now Adding in MySQL database")
      var query = helper.generateQuery(timestamp, user, action)
      mysql.executeQuery(query)
    }

  def getClientResult(timestamp: String): ClientResult =
    {
      try {
        
        var result = new ClientResult()
        result.unique_users_=(redis.readRedisCache(helper.getHourString(timestamp) + "-U"));
        result.Clicks_=(redis.readRedisCache(helper.getHourString(timestamp) + "-C"));
        result.Impressions_=(redis.readRedisCache(helper.getHourString(timestamp) + "-I"));
        return result
      } catch {
        case e: Exception => { e.printStackTrace(); return null }
      }
    }
}