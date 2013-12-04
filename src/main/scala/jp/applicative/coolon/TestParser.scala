package jp.applicative.coolon

import scala.util.parsing.combinator._
import scala.util.matching.Regex
import scala.util.DynamicVariable

object TestParser extends RegexParsers {
  def numbs:Parser[List[String]] = decimalNumber ~","~(repsep(decimalNumber,","))^^{ case x~y~xs =>
    x::xs
  }
  def asta:Parser[String] = """*"""
  def astas: Parser[List[String]] = repsep(asta, ",")
  def decimalNumber: Parser[String] = "[0-9]+".r
  def re: Parser[String] = asta ~ asta ^^ {x => x._1.toString + x._2.toString}
  def xxx: Parser[String] = rep1(numbs) ^^ {xs => xs.toString}
  def yyy: Parser[String] = (numbs ^^ (_ => "B")) | (decimalNumber ~ "-" ~ decimalNumber ^^ (_ => "C"))

  def apply(input: String): String = parse(yyy, input) match {
    case Success(result, next) => result + "/" + next
    case failure: NoSuccess => scala.sys.error(failure.msg)
  }
  
  def main(args: Array[String]) {
    System.out.println(apply("11-11"))
  }
}

