package edu.plsm.geo.mrt

import java.io.File
import java.net.URL
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

import com.fasterxml.jackson.databind.{ObjectWriter, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.typesafe.scalalogging.StrictLogging
import edu.plsm.geo.mrt.model.{Station, Location, Exit}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import scala.annotation.switch
import scala.collection.JavaConversions._
import scala.collection.immutable

/**
 * @version
 * @since
 */
object Main extends App with StrictLogging {

  private[this] def urlToLocation(s: String): Location = {
    val latLng = s.split("(googlemap.asp\\?Longitude=)|(\\&Latitude=)")
    Location(BigDecimal(latLng(1)),BigDecimal(latLng(2)))
  }

  private[this] def processDom(dom: Document): Station = {
    val infos = dom.select(infoCssSelector)
    val exitTrTags = dom.select(exitCssSelector)
    val exits = Seq(
      exitTrTags.drop(1).map(
        exit => {
          val info = exit.select("td")
          val href = info(3).select("a").first().attr("href")
          Exit(info(1).text(), info(2).text(), urlToLocation(href))
        }
      ): _*
    )

    val location = urlToLocation(infos(1).select("a").first().attr("href"))

    Station(
      infos(0).text(),
      infos(1).text(),
      location,
      infos(2).text(),
      infos(3).text(),
      infos(4).text(),
      exits
    )
  }

  private[this] lazy val mapper = {
    val om = new ObjectMapper()
    om.registerModule(DefaultScalaModule)
    om
  }
  private[this] lazy val infoCssSelector =
    """body > div > table > tbody
      | > tr:nth-child(2) > td > table
      | > tbody > tr > td > table
      | > tbody > tr > td""".stripMargin.replaceAll("\n", "")
  private[this] lazy val exitCssSelector =
    """body > div > table:nth-child(6)
      | > tbody > tr:nth-child(2) > td
      |  > table > tbody > tr""".stripMargin.replaceAll("\n", "")

  private[this] lazy val file = new File("result.json")
  private[this] lazy val assume = 1000

  private[this] lazy val count = new AtomicInteger(0)
  private[this] val time0 = System.nanoTime

  logger info "Program starting..."
  private[this] val result = (1 to assume).par
    .flatMap(
     num => {
       val dom = Jsoup.parse(new URL("http://web.trtc.com.tw/c/stationdetail2010.asp?ID=" + num), 5000)
       val countNow = count.incrementAndGet()

       if (countNow % (assume / 100) == 0) {
         logger info s"${countNow * 100 / assume }%"
       }

       dom.toString.indexOf("沒有相關資訊") match {
         case i if i > -1 => None
         case _ => Some(processDom(dom))
       }
     }
    ).seq
  val time = TimeUnit.NANOSECONDS.toMillis(System.nanoTime - time0)
  logger info s"Done, result count: $count, time elapse: $time ms."
  logger info s"Dumping json format to file $file."
  val writer: ObjectWriter = mapper.writerWithDefaultPrettyPrinter()
  writer.writeValue(file, result)
  logger info "done."

}