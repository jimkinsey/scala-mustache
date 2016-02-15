package com.github.jimkinsey.mustache.parsing

import com.github.jimkinsey.mustache.components._

private[mustache] trait StandaloneTagComponentParser[+T <: Component] extends ComponentParser[T] {
  def contentPattern: String
  def constructor: (String => T)

  final def parseResult(template: String)(implicit parserConfig: ParserConfig) = {
    Right(s"""\\{\\{$contentPattern\\}\\}""".r.findPrefixMatchOf(template).map { tagMatch =>
      ParseResult[T](constructor(tagMatch.group(1)), tagMatch.after.toString)
    })         // also need failure conditions for pattern being invalid
  }
}

private[mustache] object TripleDelimitedVariableParser extends StandaloneTagComponentParser[TripleDelimitedVariable] {
  lazy val contentPattern = "\\{([a-zA-Z0-9]+?)\\}"
  lazy val constructor = TripleDelimitedVariable.apply _
}

private[mustache] object AmpersandPrefixedVariableParser extends StandaloneTagComponentParser[AmpersandPrefixedVariable] {
  lazy val contentPattern = "&([a-zA-Z0-9]+?)"
  lazy val constructor = AmpersandPrefixedVariable.apply _
}

private[mustache] object VariableParser extends StandaloneTagComponentParser[Variable] {
  lazy val contentPattern = "([a-zA-Z0-9]+?)"
  lazy val constructor = Variable.apply _
}

private[mustache] object CommentParser extends StandaloneTagComponentParser[Comment] {
  lazy val contentPattern = "(?s)!(.*?)"
  lazy val constructor = Comment.apply _
}
