package com.ruchij.exceptions

case object IncorrectPasswordException extends Exception {
  override def getMessage: String = "Incorrect password"
}
