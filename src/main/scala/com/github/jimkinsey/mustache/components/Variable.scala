package com.github.jimkinsey.mustache.components

import com.github.jimkinsey.mustache.Context

private[mustache] case class Variable(name: String) extends Value {
  def rendered(context: Context)(implicit global: Context) = {
    Right((global ++ context).get(name).map(_.toString).map(escapeHTML).getOrElse(""))
  }

  lazy val formatted = s"{{$name}}"

  private def escapeHTML(str: String) = str.foldLeft("") { case (acc, char) => acc + escapeCode.getOrElse(char, char) }

  private lazy val escapeCode = Map(
    '<' -> "&lt;",
    '>' -> "&gt;",
    '&' -> "&amp;",
    '\"' -> "&quot;",
    ''' -> "&#39;"
  )
}
