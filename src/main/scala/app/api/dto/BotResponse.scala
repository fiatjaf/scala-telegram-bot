package app.api.dto

case class BotResponse[T](ok: Boolean, result: T)
