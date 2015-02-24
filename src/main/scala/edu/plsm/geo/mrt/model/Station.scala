package edu.plsm.geo.mrt.model

import edu.nccu.plsm.geo.coordinate.{GeographicCoordinate, CartesianCoordinate}
import edu.nccu.plsm.geo.datum.TWD97

/**
 * @version
 * @since
 */
case class Station(
  name: String,
  address: String,
  location: Location,
  elevator: String,
  informationDesk: String,
  bike: String,
  exit: Seq[Exit]
  )

case class Location(
  geographical: LatLng,
  twd97: CartesianCoordinate
  )

case class LatLng(
  lng: BigDecimal,
  lat: BigDecimal
  )

object Location {
  def apply(lat: BigDecimal, lng: BigDecimal): Location = {
    Location(
      LatLng(lng, lat),
      TWD97.transverseMercator(GeographicCoordinate.fromDegree(lng, lat))
    )
  }
}


case class Exit(
  name: String,
  description: String,
  location: Location
  )