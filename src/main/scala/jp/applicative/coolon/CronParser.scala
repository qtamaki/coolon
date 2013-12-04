package jp.applicative.coolon

import scala.util.parsing.combinator.RegexParsers
import scala.util.matching.Regex
import scala.util.DynamicVariable
import scala.util.parsing.input.Reader

sealed class Timing
case class Asta extends Timing
case class TimingSeq(list: Seq[Int]) extends Timing
case class Bounds(from: Int, to: Int) extends Timing
case class Single(t: Int) extends Timing
case class AstaPar(par: Int) extends Timing
case class Fraction(top: Int, bottom: Int) extends Timing

case class Cron(min: Timing, hour: Timing, day: Timing, month: Timing, dayOfWeek: Timing)
case class Space

object CronParser extends RegexParsers {
  def repsepN[T, U](num: Int, p: Parser[T], q: Parser[U]): Parser[List[T]] = {
    p ~ repN(num, q ~> p) ^^ { case r ~ rs => r :: rs } | success(List())
  }
  def space: Parser[Space] = " +".r ^^ (_ => Space())
  def decimalNumber: Parser[String] = "[0-9]+".r
  def asta: Parser[Timing] = "*" ^^ {x => print(x);Asta()}
  def timingSeq: Parser[Timing] = repsep(decimalNumber, ",") ^^ (elems => TimingSeq(elems.map(_.toInt)))
  def bounds: Parser[Timing] = decimalNumber ~ "-" ~ decimalNumber ^^ { case f ~ x ~ t => Bounds(f.toInt, t.toInt) }
  def single: Parser[Timing] = decimalNumber ^^ (x => Single(x.toInt))
  def astaPar: Parser[Timing] = "*/" ~> decimalNumber ^^ (x => AstaPar(x.toInt))
  def fraction: Parser[Timing] = decimalNumber ~ "/" ~ decimalNumber ^^ { case t ~ x ~ b => Fraction(t.toInt, b.toInt) }
  def timing: Parser[Timing] = asta | timingSeq | bounds | single | astaPar | fraction
//  def cron: Parser[Cron] = repsep(asta, space) ^^ {e => println(">>>>>>>" + e.size);Cron(e(0), e(1), e(2), e(3), e(4))}
  def cron: Parser[Cron] = repsep(asta, space) ^^ {e => println(">>>>>>>" + e.size);Cron(Asta(),Asta(),Asta(),Asta(),Asta())}

  def apply(input: String): Cron = parseAll(cron, input) match {
    case Success(result, _) => result
    case failure: NoSuccess => scala.sys.error(failure.msg)
  }
  
  def main(args: Array[String]) {
    println(apply("* * * * * "))
  }
}

