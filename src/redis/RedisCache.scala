package redis
import redis.clients.jedis.Jedis
import Java.Constants

class RedisCache {

  val jedis = new Jedis(Constants.HOST_NAME);

  def writeToRedisCache(key: String, value: String): Unit = {
    try {
      jedis.pfadd(key, value)
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }
  
  
  def clearCache(key:String)
  {
     try {
      jedis.del(key+"-C")
      jedis.del(key+"-I")
      jedis.del(key+"-U")
    } catch {
      case e: Exception =>{ e.printStackTrace();}
    }
  }

  def readRedisCache(key: String): Long = {
    try {
      jedis.pfcount(key)
    } catch {
      case e: Exception =>{ e.printStackTrace(); return 0}
    }
  }
}