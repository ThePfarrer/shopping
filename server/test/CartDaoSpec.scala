import dao.{CartDao, ProductInCart}
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import models.Tables.CartRow
import org.scalatest.RecoverMethods._
import org.scalatest.matchers.should.Matchers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CartDaoSpec extends PlaySpec with ScalaFutures with GuiceOneAppPerSuite {
  "CartDao" should {
    val app2dao = Application.instanceCache[CartDao]

    "be empty on database creation" in {
      val dao: CartDao = app2dao(app)
      dao.all().futureValue shouldBe empty
    }

    "accept to add new cart" in {
      val dao: CartDao = app2dao(app)
      val user = "userAdd"

      val expected = Set(
        CartRow(1, user, "ALD1", 1),
        CartRow(1, user, "BE01", 5)
      )
      val noise = Set(
        CartRow(1, "userNoise", "ALD2", 10)
      )
      val allCarts = expected ++ noise

      val insertFutures = allCarts.map(dao.insert)

      whenReady(Future.sequence(insertFutures)) { _ =>
        dao.cart4(user).futureValue should contain theSameElementsAs expected
        dao.all().futureValue.size should equal(allCarts.size)
      }
    }

    "error thrown when adding a cart with same user and productCode" in {
      val dao: CartDao = app2dao(app)
      val user = "userAdd"
      val expected = Set(
        CartRow(1, user, "ALD1", 1),
        CartRow(1, user, "BE01", 5)
      )
      val noise = Set(
        CartRow(1, "userNoise", "ALD2", 10)
      )
      val allCarts = expected ++ noise
      val insertFutures = allCarts.map(dao.insert)
      recoverToSucceededIf[org.postgresql.util.PSQLException] {
//        recoverToSucceededIf[org.h2.jdbc.JdbcSQLException] {
        Future.sequence(insertFutures)
      }
      //      recoverToSucceededIf[org.h2.jdbc.JdbcSQLException]
    }

    "accept to remove a product from a cart" in {
      val dao: CartDao = app2dao(app)
      val user = "userRmv"
      val initial = Vector(
        CartRow(1, user, "ALD1", 1),
        CartRow(1, user, "BE01", 5)
      )
      val expected = Vector(CartRow(1, user, "ALD1", 1))

      whenReady(Future.sequence(initial.map(dao.insert(_)))) { _ =>
        dao.remove(ProductInCart(user, "BE01")).futureValue
        dao.cart4(user).futureValue should contain theSameElementsAs expected
      }
    }

    "accept to update quantities of an item in a cart" in {
      val dao: CartDao = app2dao(app)
      val user = "userUpd"
      val initial = Vector(CartRow(1, user, "ALD1", 1))
      val expected = Vector(CartRow(1, user, "ALD1", 5))

      whenReady(Future.sequence(initial.map(dao.insert(_)))) { _ =>
        dao.update(CartRow(1, user, "ALD1", 5)).futureValue
        dao.cart4(user).futureValue should contain theSameElementsAs expected
      }
    }
  }

}
