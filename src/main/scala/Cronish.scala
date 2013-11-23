import scalendar._

object Cronish extends App {
  import cronish.dsl._


val cron = "every 10 days".cron
val now = Scalendar.now

println(cron.nextFrom(now)) // returns milliseconds
println(cron.nextFrom(now + 12.days)) // can advance

println(cron.nextTime) // returns a Scalendar object
println(now to cron.nextTime) // This is obviously a duration

println(cron.next) // returns milliseconds from Scalendar.now

println(cron.nextFrom(cron.nextTime)) // The next-next run
}