import adapters.GelDriver
import api.TripRoutes
import domain.services.person.gel.PersonServiceGel
import domain.services.services.AuthServiceLive
import domain.services.trip.TripService
import domain.services.trip.gel.TripServiceGel
import sttp.tapir.server.interceptor.cors.CORSConfig.AllowedOrigin
import sttp.tapir.server.interceptor.cors.{CORSConfig, CORSInterceptor}
import sttp.tapir.server.ziohttp.{ZioHttpInterpreter, ZioHttpServerOptions}
import sttp.tapir.ztapir.RIOMonadError
import zio.*
import zio.http.*

object Main extends ZIOAppDefault:
  given RIOMonadError[Any]               = new RIOMonadError[Any]
  val options: ZioHttpServerOptions[Any] =
    ZioHttpServerOptions
      .customiseInterceptors
      .exceptionHandler(new DefectHandler())
      .corsInterceptor(
        CORSInterceptor.customOrThrow(
          CORSConfig
            .default.copy(
              allowedOrigin = AllowedOrigin.All
            )
        )
      )
      .decodeFailureHandler(CustomDecodeFailureHandler.create())
      .options

  override def run =
    val port = 8081
    (for

      endpoints <- ZIO.service[TripRoutes]
      _         <- ZIO.log(s"Swagger UI available at http://localhost:$port/docs")
      _         <- ZIO.log(s"Server starting on http://localhost:$port")
      httpApp    = ZioHttpInterpreter(options).toHttp(
                     endpoints.endpoints
                   )
      _         <- Server
                     .install(httpApp)
      _         <- ZIO.never
    yield ()).provide(
      Server.defaultWithPort(port),
      // AuthService.layer,
      TripRoutes.live,
      TripServiceGel.layer,
      PersonServiceGel.layer,
      GelDriver.layer,
      AuthServiceLive.layer
    )
