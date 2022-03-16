package app.bot

import app.api.ChatId

sealed trait BotCommand

object BotCommand {
  case class ShowHelp(chatId: ChatId) extends BotCommand
  case class Clear(chatId: ChatId) extends BotCommand
  case class Show(chatId: ChatId) extends BotCommand
  case class AddEntry(chatId: ChatId, content: String) extends BotCommand

  def fromRawMessage(chatId: ChatId, message: String): BotCommand =
    message match {
      case "?" | "/start" => ShowHelp(chatId)
      case "/show"        => Show(chatId)
      case "/clear"       => Clear(chatId)
      case _              => AddEntry(chatId, message)
    }
}
