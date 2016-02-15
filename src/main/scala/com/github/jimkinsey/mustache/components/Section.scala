package com.github.jimkinsey.mustache.components

import com.github.jimkinsey.mustache.components.Partial.Render
import com.github.jimkinsey.mustache.components.Section.emptyResult
import com.github.jimkinsey.mustache.{Context, Lambda}

private[mustache] object Section {
  type Render = (Template, Context) => Either[Any,String]
  type NonContextualRender = (Template) => Either[Any,String]
  val emptyResult: Either[Any,String] = Right("")
}

private[mustache] case class Section(name: String, template: Template, private val rendered: (String, Context) => Either[Any, String]) extends Container {
  def rendered(context: Context)(implicit global: Context) = {
    context.get(name).map {
      case true => template.rendered(context)
      case lambda: Lambda @unchecked => lambda(template.formatted, rendered(_, context))
      case map: Context @unchecked => template.rendered(map)
      case iterable: Iterable[Context] @unchecked => iterable.foldLeft(emptyResult) {
        case (Right(acc), ctx) => template.rendered(ctx).right.map(acc + _)
        case (Left(fail), _) => Left(fail)
      }
      case Some(ctx: Context @unchecked) => template.rendered(ctx)
      case _ => emptyResult
    }.getOrElse(emptyResult)
  }

  lazy val formatted = s"{{#$name}}${template.formatted}{{/$name}}"
}

private[mustache] case class InvertedSection(name: String, template: Template, render: Render) extends Container {
  def rendered(context: Context)(implicit global: Context) = {
    context.get(name).map {
      case false => template.rendered(context)
      case None => template.rendered(context)
      case iterable: Iterable[Context] @unchecked if iterable.isEmpty => template.rendered(context)
      case _ => emptyResult
    }.getOrElse(template.rendered(context))
  }

  lazy val formatted = s"{{^$name}}${template.formatted}{{/$name}}"
}
