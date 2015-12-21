package com.github.jimkinsey.mustache.tags

import java.util.regex.Pattern

import com.github.jimkinsey.mustache.Renderer._

import scala.util.matching.Regex

/**
 * Created by jimkinsey on 21/12/15.
 */
object InvertedSection extends Tag {
   case class UnclosedInvertedSection(name: String) extends Failure

   val pattern: Regex = """^\^(.+)$""".r

   def process(name: String, context: Context, postTagTemplate: String, render: (String, Context) => Result): Either[Failure, (String, String)] = {
     ("""(?s)(.*?)\{\{/""" + Pattern.quote(name) + """\}\}(.*)""").r
       .findFirstMatchIn(postTagTemplate)
       .map(m =>
         if(!context.keySet.contains(name) || isEmptyIterable(context, name) || (context(name) == false))
           render(m.group(1), context).right.map(_ -> m.group(2))
         else
           Right("" -> m.group(2))
       )
       .getOrElse(Left(UnclosedInvertedSection(name)))
   }

   private def isEmptyIterable(context: Context, name: String) = context(name) match {
     case iterable: Iterable[_] => iterable.isEmpty
     case _ => false
   }

 }
