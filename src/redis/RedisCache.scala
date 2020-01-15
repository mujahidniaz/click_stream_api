package redis
import redis.clients.jedis.Jedis
class RedisCache {
  val jedis = new Jedis("localhost");

  def writeToRedisCache(key: String, value: String): Unit = {
    try {
      jedis.pfadd(key, value)
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }

  def readRedisCache(key: String): Long = {
    try {
      jedis.pfcount(key)
    } catch {
      case e: Exception => return 0
    }
  }
}