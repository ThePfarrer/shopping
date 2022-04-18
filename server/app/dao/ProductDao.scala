package dao

//import io.fscala.shopping.shared
//import io.fscala.shopping.shared.{Cart, CartKey, Product, ProductInCart}

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import models.Tables._

import scala.concurrent.{ExecutionContext, Future}

abstract class CartKey {
  def user: String
  def productCode: String
}

case class ProductInCart(user: String, productCode: String) extends CartKey

class ProductDao @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit
    executionContext: ExecutionContext
) extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val products = Products

  def all(): Future[Seq[ProductsRow]] = db.run(products.result)

  def insert(product: ProductsRow): Future[Unit] = db.run(products insertOrUpdate product).map { _ => () }

}

class CartDao @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit
    executionContext: ExecutionContext
) extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val carts = Cart

  def cart4(usr: String): Future[Seq[CartRow]] = db.run(carts.filter(_.auser === usr).result)

  def insert(cart: CartRow): Future[_] = db.run(carts += cart)

  def remove(cart: ProductInCart): Future[Int] = db.run(carts.filter(c => matchKeyP(c, cart)).delete)

  def update(cart: CartRow): Future[Int] = {
    val q = for {
      c <- carts if matchKeyC(c, cart)
    } yield c.qty
    db.run(q.update(cart.qty))
  }

  private def matchKeyC(c: Cart, cart: CartRow): Rep[Boolean] = {
    c.auser === cart.auser && c.code === cart.code
  }
  private def matchKeyP(c: Cart, cart: CartKey): Rep[Boolean] = {
    c.auser === cart.user && c.code === cart.productCode
  }

  def all(): Future[Seq[CartRow]] = db.run(carts.result)

}
