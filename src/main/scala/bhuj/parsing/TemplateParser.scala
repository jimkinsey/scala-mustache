package bhuj.parsing

import bhuj.model._
import bhuj._

private[bhuj] case class ParseResult[+T <: Component](component: T, remainder: String)

private[bhuj] trait ComponentParser[+T <: Component] {
  def parseResult(template: String)(implicit parserConfig: ParserConfig): Either[ParseTemplateFailure, Option[ParseResult[T]]]
}

private[bhuj] case class ParserConfig(
  parsed: (String) => Either[ParseTemplateFailure, Template],
  delimiters: Delimiters = doubleMustaches
)

private[bhuj] class TemplateParser(componentParsers: ComponentParser[Component]*) {

  def template(raw: String)(implicit parserConfig: ParserConfig): Either[ParseTemplateFailure, Template] = {
    Stream(componentParsers:_*).map(_.parseResult(raw)).collectFirst {
      case Right(Some(ParseResult(setDelimiters: SetDelimiters, remainder))) =>
        template(remainder)(modifiedConfig(setDelimiters)).map(tail => Template(parserConfig.delimiters, setDelimiters +: tail.components))
      case Right(Some(ParseResult(head, remainder))) =>
        template(remainder).map(tail => Template(parserConfig.delimiters, head +: tail.components))
      case Left(fail) =>
        Left(fail)
    }.getOrElse(Right(Template(parserConfig.delimiters, Seq.empty)))
  }

  private def modifiedConfig(setDelimiters: SetDelimiters)(implicit config: ParserConfig) =
    config.copy(delimiters = setDelimiters.delimiters)

}
