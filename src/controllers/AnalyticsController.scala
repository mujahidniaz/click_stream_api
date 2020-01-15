package controllers

import org.scalatra._
import org.scalatra.json.JacksonJsonSupport
import Java.JavaHelper
import analytics_business_logic.AnalyticsAPI
import org.json4s._
import java.util.Date
import java.text.SimpleDateFormat
import Java.Constants

class AnalyticsController extends ScalatraServlet {

 

  val javaHelper = new JavaHelper()
  val analyticsApi = new AnalyticsAPI()
  var latestRecord:Date = new Date()
  val sdformat = new SimpleDateFormat("yyyy-MM-dd")
  var count:Long=0
  before() {
//    contentType = formats("text")
  }

  post("/analytics") {      /// Post API End point

    var time_epoch = params("timestamp")  // Get query parameters
    var timestamp = javaHelper.getTimeStampFromEpoch(time_epoch);
    var user = params("user")
    var action = "Click"
    if (params.contains("impression")) {
      action = "Impression"
    }
    count=count+1
    println("Record Number "+count)
    analyticsApi.HandlePostRequest(user, timestamp, action) // Calling function that handles the POST event
    if(sdformat.parse(new Date().toString()).compareTo(sdformat.parse(latestRecord.toString()))>0) // If the date changes
      {
         println("Clearing Cache records Which are "+Constants.CACHE_RETENTION_HOURS+" Hours Old") 
         analyticsApi.getCacheObject().clearCache(javaHelper.getHourString(javaHelper.getOldestDate(latestRecord,Constants.CACHE_RETENTION_HOURS).toString()));  /// Clearing cache based on retention period...   
         latestRecord=new Date()
      }
   
    Ok(200);
  }

  get("/analytics") {        // GET API End point
    var time_epoch = params("timestamp") // Get query parameters
 
    val result=analyticsApi.getClientResult(time_epoch)
    Ok("unique_users,"+result.unique_users+"\n"+"clicks,"+result.clicks+"\n"+"impressions,"+result.impressions+"\n") // Return the result in plain text
  }

}