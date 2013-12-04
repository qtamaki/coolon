package jp.applicative.coolon

import scala.util.parsing.combinator.RegexParsers
import scala.util.matching.Regex
import scala.util.DynamicVariable
import scala.util.parsing.input.Reader

sealed class Timing
case class Asta extends Timing
case class TimingSeq(list: Seq[Int]) extends Timing
case class Bounds(from: Int, to: Int) extends Timing
case class AstaPar(par: Int) extends Timing
case class Fraction(top: Int, bottom: Int) extends Timing

case class Cron(min: Timing, hour: Timing, day: Timing, month: Timing, dayOfWeek: Timing, command: Option[String])
case class Space

object CronParser extends RegexParsers {
  def repsepN[T, U](num: Int, p: Parser[T], q: Parser[U]): Parser[List[T]] = {
    p ~ repN(num, q ~> p) ^^ { case r ~ rs => r :: rs } | success(List())
  }
  def space: Parser[Space] = """[ /t]+""".r ^^ (_ => Space())
  def decimalNumber: Parser[String] = "[0-9]+".r
  def asta: Parser[Timing] = "*"~opt("/"~decimalNumber) ^^ {
    case x~Some(y~z)=> AstaPar(z.toInt)
    case x~None => Asta()
  }
  def timingSeq: Parser[Timing] = decimalNumber~opt(","~repsep(decimalNumber, ",")) ^^ {
    case x~Some(","~ys) => TimingSeq((x::ys).map(_.toInt))
    case x~None => TimingSeq(List(x.toInt))
  }
  def bounds: Parser[Timing] = decimalNumber ~ "-" ~ decimalNumber ^^ { case f ~ x ~ t => Bounds(f.toInt, t.toInt) }
  def fraction: Parser[Timing] = decimalNumber ~ "/" ~ decimalNumber ^^ { case t ~ x ~ b => Fraction(t.toInt, b.toInt) }
  def timing: Parser[Timing] = asta | bounds | fraction | timingSeq
  def timings: Parser[List[Timing]] = repN(5, timing)
  def cron: Parser[Cron] = timings ~ opt(""".*""".r) ^^ {
    case xs~cmd => Cron(xs(0), xs(1), xs(2), xs(3), xs(4), cmd)
  }

  def apply(input: String): Cron = parseAll(cron, input) match {
    case Success(result, _) => result
    case failure: NoSuccess => scala.sys.error(failure.msg)
  }
  
  def main(args: Array[String]) {
    println("start")
    println("x:" + apply("10/10 11-11 1,2,3 1-2 10 find . -name '.bk' -delete"))
    println("end")
  }
}

