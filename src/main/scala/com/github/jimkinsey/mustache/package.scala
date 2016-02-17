package com.github.jimkinsey

import com.github.jimkinsey.mustache.parsing.Delimiters

package object mustache {
  type Context = Map[String, Any]
  type Result = Either[Failure, String]
  type Lambda = (String, NonContextualRender) => Result

  private[mustache] val doubleMustaches = Delimiters("{{", "}}")

  private type NonContextualRender = (String) => Result
}


