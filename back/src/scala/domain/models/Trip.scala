package domain.models

import domain.services.trip.gel.models.TripGel
import zio.json.*

import java.time.LocalDate
import java.util.UUID
import scala.collection.JavaConverters.*

case class Trip(
  id: UUID,
  distance: Int,
  date: LocalDate,
  name: String,
  drivers: Set[PersonCreate]
)

object Trip {
  implicit val encoder: JsonEncoder[Trip]    = DeriveJsonEncoder.gen[Trip]
  implicit val decoder: JsonDecoder[Trip]    = DeriveJsonDecoder.gen[Trip]
  def fromTripGel(tripGel: TripGel): Trip =
    Trip(
      tripGel.getId,
      tripGel.getDistance,
      tripGel.getDate,
      tripGel.getName,
      tripGel.getDrivers.asScala.map(p => PersonCreate(p.name)).toSet
    )
}

case class TripCreate(
  distance: Int,
  date: LocalDate,
  name: String,
  drivers: Set[String]
)

object TripCreate {
  implicit val encoder: JsonEncoder[TripCreate] =
    DeriveJsonEncoder.gen[TripCreate]
  implicit val decoder: JsonDecoder[TripCreate] =
    DeriveJsonDecoder.gen[TripCreate]
}

case class TripStats(
  trips: List[Trip],
  totalKilometers: Double
)

object TripStats {
  implicit val encoder: JsonEncoder[TripStats] =
    DeriveJsonEncoder.gen[TripStats]
  implicit val decoder: JsonDecoder[TripStats] =
    DeriveJsonDecoder.gen[TripStats]
}
