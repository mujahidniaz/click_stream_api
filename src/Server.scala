
import javax.servlet.ServletContext
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext
import org.json4s.{ DefaultFormats, Formats }
import org.scalatra._
import org.scalatra.json._
import org.scalatra.servlet.ScalatraListener


import javax.servlet.ServletContext
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext
import org.json4s.{ DefaultFormats, Formats }
import org.scalatra._
import org.scalatra.json._
import org.scalatra.servlet.ScalatraListener
import mysql.MySQLConnector
import java.util.Date
import Java.JavaHelper
import analytics_business_logic.AnalyticsAPI;
import controllers.AnalyticsController

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    context mount (new AnalyticsController, "/*")
  }
}

object SimpleScalatraRestService extends App {
  val port = 7777
  val server = new Server(port)

  val context = new WebAppContext()
  context.setContextPath("/")
  context.setResourceBase(".")
  context.setInitParameter(ScalatraListener.LifeCycleKey, "ScalatraBootstrap")
  context.setEventListeners(Array(new ScalatraListener))

  server.setHandler(context)
  server.start

  println("***** Server is listening to http://localhost:7777 *****")

  server.join
}