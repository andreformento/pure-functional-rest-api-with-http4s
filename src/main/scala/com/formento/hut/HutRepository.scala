package com.formento.hut


import java.util.UUID

import cats.Monad
import cats.effect.IO
import cats.implicits._
import com.formento.hut.entities._

import scala.collection.mutable.ListBuffer

final case class HutRepository[F[_]](private val huts: ListBuffer[HutWithId])(implicit m: Monad[F]) {
  val makeId: F[String] = m.point {
    UUID.randomUUID().toString
  }


  def getHut(id: String): F[Option[HutWithId]] =
    m.point {
      huts.find(_.id == id)
    }

  def addHut(hut: Hut): F[String] =
    for {
      uuid <- makeId
      _ <- m.point {
        huts += hutWithId(hut, uuid)
      }
    } yield uuid

  def updateHut(hutWithId: HutWithId): F[Unit] = {
    for {
      _ <- m.point {
        huts -= hutWithId
      }
      _ <- m.point {
        huts += hutWithId
      }
    } yield ()
  }

  def deleteHut(hutId: String): F[Unit] =
    for {
      _ <- m.point {
        huts.find(_.id == hutId).map(removeHut)
      }
    } yield ()

  def removeHut(hut: HutWithId): F[Unit] = m.point {
    huts -= hut
  }

  def hutWithId(hut: Hut, id: String): HutWithId =
    HutWithId(id, hut.name)
}

object HutRepository {
  def empty[F[_]](implicit m: Monad[F]): IO[HutRepository[F]] = IO {
    new HutRepository[F](ListBuffer())
  }
}