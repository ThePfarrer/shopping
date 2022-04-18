import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success}

val f1 = Future{1}
val f2 = Future{2}
val f3 = Future{3}

//val res = for {
//  v1 <- f1
//  v2 <- f2
//  v3 <- f3
//} yield v1 + v2 + v3



def sum(v: Int*) = v.sum

val minExpected = 7

val res2 = for {
  v1 <- f1
  v2 <- f2
  v3 <- f3
  if (sum(v1, v2, v3) > minExpected)
} yield (v1, v2, v3)

res2.onComplete{
  case Success(value) => println(s"The result is $value")
  case Failure(exception) => println("The sum is not big enough")
}

//val response = Await.result(res, 1 second)
Await.ready(res2, 1 second)

Int.MaxValue
Double.MaxValue