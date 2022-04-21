package dao

import io.fscala.shopping.shared.{Cart, CartKey, Product, ProductInCart}
import models.{CartTable, ProductsTable}

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class ProductDao @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit
    executionContext: ExecutionContext
) extends HasDatabaseConfigProvider[JdbcProfile] {

  lazy val products = TableQuery[ProductsTable]

  def all(): Future[Seq[Product]] = db.run(products.result)

  def insert(product: Product): Future[Unit] = db.run(products insertOrUpdate product).map { _ => () }

}

class CartDao @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit
    executionContext: ExecutionContext
) extends HasDatabaseConfigProvider[JdbcProfile] {

  lazy val carts = TableQuery[CartTable]

  def cart4(usr: String): Future[Seq[Cart]] = db.run(carts.filter(_.user === usr).result)

  def insert(cart: Cart): Future[_] = db.run(carts += cart)

  def remove(cart: ProductInCart): Future[Int] = db.run(carts.filter(c => matchKey(c, cart)).delete)

  def update(cart: Cart): Future[Int] = {
    val q = for {
      c <- carts if matchKey(c, cart)
    } yield c.qty
    db.run(q.update(cart.qty))
  }

  private def matchKey(c: CartTable, cart: CartKey): Rep[Boolean] = {
    c.user === cart.user && c.productCode === cart.productCode
  }

  def all(): Future[Seq[Cart]] = db.run(carts.result)

}
