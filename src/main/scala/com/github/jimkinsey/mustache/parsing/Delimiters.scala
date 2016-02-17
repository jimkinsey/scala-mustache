package com.github.jimkinsey.mustache.parsing

import scala.util.matching.Regex.quote

private[mustache] case class Delimiters(start: String, end: String) {
  def pattern(innerPattern: String): String = s"""${quote(start)}$innerPattern${quote(end)}"""
  def tag(content: String) = s"""$start$content$end"""
}

private[mustache] case class InvalidDelimiters(start: String, end: String)