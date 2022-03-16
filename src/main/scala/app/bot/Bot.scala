package app.bot

import cats.effect.Sync
import cats.implicits._
import fs2._
import org.typelevel.log4cats._
import app.api._
import app.bot.BotCommand._

import scala.util.Random

class Bot[F[_]](
    api: StreamingBotAPI[F],
    storage: Storage[F],
    logger: Logger[F]
)(implicit F: Sync[F]) {
  def launch: Stream[F, Unit] = pollCommands.evalMap(handleCommand)

  private def pollCommands: Stream[F, BotCommand] = for {
    update <- api.pollUpdates(0)
    chatIdAndMessage <- Stream.emits(
      update.message.flatMap(a => a.text.map(a.chat.id -> _)).toSeq
    )
  } yield BotCommand.fromRawMessage(chatIdAndMessage._1, chatIdAndMessage._2)

  private def handleCommand(command: BotCommand): F[Unit] = command match {
    case c: Clear    => clear(c.chatId)
    case c: Show     => show(c.chatId)
    case c: AddEntry => addItem(c.chatId, c.content)
    case c: ShowHelp =>
      api.sendMessage(
        c.chatId,
        """
        help text was supposed to go here!
        """
      )
  }

  private def clear(chatId: ChatId): F[Unit] = for {
    _ <- storage.clearList(chatId)
    _ <- logger.info(s"todo list cleared for chat $chatId") *> api.sendMessage(
      chatId,
      "Your todo-list was cleared!"
    )
  } yield ()

  private def show(chatId: ChatId): F[Unit] = for {
    items <- storage.getItems(chatId)
    _ <- logger.info(s"todo list queried for chat $chatId") *> api.sendMessage(
      chatId,
      if (items.isEmpty) "You have no tasks planned!"
      else ("Your todo-list:" :: "" :: items.map(" - " + _)).mkString("\n")
    )
  } yield ()

  private def addItem(chatId: ChatId, item: Item): F[Unit] = for {
    _ <- storage.addItem(chatId, item)
    response <- F.defer(
      F.catchNonFatal(
        Random.shuffle(List("Ok!", "Sure!", "Noted", "Certainly!")).head
      )
    )
    _ <- logger.info(s"entry added for chat $chatId") *> api.sendMessage(
      chatId,
      response
    )
  } yield ()
}
