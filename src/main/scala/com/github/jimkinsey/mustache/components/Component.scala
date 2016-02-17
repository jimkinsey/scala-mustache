package com.github.jimkinsey.mustache.components

import com.github.jimkinsey.mustache.Context
import com.github.jimkinsey.mustache.parsing.{Delimiters, ParserConfig}

private[mustache] sealed trait Component {
  def rendered(context: Context)(implicit global: Context): Either[Any, String]
  def formatted(delimiters: Delimiters): String
}
private[mustache] trait Value extends Component
private[mustache] trait Container extends Component {
  def template: Template
}
private[mustache] trait ParserDirective extends Component {
  final def rendered(context: Context)(implicit global: Context) = Right("")
  def modified(implicit config: ParserConfig): ParserConfig
}

private[mustache] object Template {
  type Result = Either[Any, String]
  val emptyResult: Result = Right("")

  def apply(components: Component*): Template = {
    this(Delimiters("{{", "}}"), components:_*)
  }
}

private[mustache] case class Template(initialDelimiters: Delimiters, components: Component*) extends Component {

  def rendered(context: Context)(implicit global: Context) = components.foldLeft(Template.emptyResult) {
    case (Right(acc), component) => component.rendered(global ++ context).right.map(acc + _)
    case (failure: Left[Any, String], _) => failure
  }

  lazy val source = formatted(initialDelimiters)

  def formatted(delimiters: Delimiters) = components.foldLeft(("", delimiters)) {
    case ((acc, delimiters), component: SetDelimiters) => (acc + component.formatted(delimiters), component.delimiters)
    case ((acc, delimiters), component) => (acc + component.formatted(delimiters), delimiters)
  }._1
}
