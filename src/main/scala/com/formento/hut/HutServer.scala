package com.formento.hut

import cats.effect.{IO, _}
import cats.implicits._
import com.formento.hut.entities.{Hut, _}
import fs2.{Stream, StreamApp}
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.ExecutionContext.Implicits.global

object HutServer extends StreamApp[IO] with Http4sDsl[IO] {

  val HUTS = "huts"

  def service[F[_]](hutRepo: HutRepository[F])(implicit F: Effect[F]) = HttpService[F] {

    case GET -> Root / HUTS / hutId =>
      hutRepo.getHut(hutId)
        .flatMap {
          case Some(hut) => Response(status = Status.Ok).withBody(hut.asJson)
          case None => F.pure(Response(status = Status.NotFound))
        }

    case req@POST -> Root / HUTS =>
      req.decodeJson[Hut]
        .flatMap(hutRepo.addHut)
        .flatMap(hut => Response(status = Status.Created).withBody(hut.asJson))

    case req@PUT -> Root / HUTS =>
      req.decodeJson[HutWithId]
        .flatMap(hutRepo.updateHut)
        .flatMap(_ => F.pure(Response(status = Status.Ok)))

    case DELETE -> Root / HUTS / hutId =>
      hutRepo.deleteHut(hutId)
        .flatMap(_ => F.pure(Response(status = Status.NoContent)))
  }

  def stream(args: List[String], requestShutdown: IO[Unit]) =
    Stream.eval(HutRepository.empty[IO]).flatMap { hutRepo =>
      BlazeBuilder[IO]
        .bindHttp(8080, "0.0.0.0")
        .mountService(service(hutRepo), "/")
        .serve
    }
}