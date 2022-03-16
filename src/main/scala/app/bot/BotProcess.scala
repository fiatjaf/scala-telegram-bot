package app.bot

import scala.concurrent.ExecutionContext
import cats.effect.ConcurrentEffect
import cats.effect.concurrent.Ref
import cats.implicits._
import fs2.Stream
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.client.blaze.BlazeClientBuilder
import org.typelevel.log4cats.slf4j.Slf4jLogger
import app.api.dto.{BotResponse, BotUpdate}
import app.api.{ChatId, Http4SBotAPI}

class BotProcess[F[_]](token: String)(implicit F: ConcurrentEffect[F]) {
  implicit val decoder: EntityDecoder[F, BotResponse[List[BotUpdate]]] =
    jsonOf[F, BotResponse[List[BotUpdate]]]

  def run: Stream[F, Unit] =
    BlazeClientBuilder[F](ExecutionContext.global).stream.flatMap { client =>
      val streamF: F[Stream[F, Unit]] = for {
        logger <- Slf4jLogger.create[F]
        storage <-
          Ref
            .of(Map.empty[ChatId, List[Item]])
            .map(new InMemoryStorage(_))
        botAPI <- F.delay(new Http4SBotAPI(token, client, logger))
        bot <- F.delay(new Bot(botAPI, storage, logger))
      } yield bot.launch

      Stream.force(streamF)
    }
}
