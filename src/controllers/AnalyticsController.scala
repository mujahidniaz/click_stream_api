package controllers

import org.scalatra._
import org.scalatra.json.JacksonJsonSupport
import Java.JavaHelper
import analytics_business_logic.AnalyticsAPI
import org.json4s._
import java.util.Date

class AnalyticsController extends ScalatraServlet {

 

  val javaHelper = new JavaHelper()
  val analyticsApi = new AnalyticsAPI();
  var current_date:Date = new Date();
  before() {
//    contentType = formats("text")
  }

  post("/analytics") {

    var time_epoch = params("timestamp")
    var timestamp = javaHelper.getTimeStampFromEpoch(time_epoch);
    var user = params("user")
    var action = "Click"
    if (params.contains("impression")) {
      action = "Impression"
    }
    println(timestamp)
    analyticsApi.HandlePostRequest(user, timestamp, action)
    Ok(200);
  }

  get("/analytics") {
    println(current_date.toString())
    var time_epoch = params("timestamp")
    var timestamp = javaHelper.getTimeStampFromEpoch(time_epoch)
    val result=analyticsApi.getClientResult(timestamp)
    Ok("unique_users,"+result.unique_users+"\n"+"clicks,"+result.clicks+"\n"+"impressions,"+result.impressions+"\n")
  }

}