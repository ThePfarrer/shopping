package models

import io.fscala.shopping.shared.{Cart, Product}
import slick.jdbc.PostgresProfile.api._

/** Table description of table products. Objects of this class serve as prototypes for rows in queries. */
class ProductsTable(tag: Tag) extends Table[Product](tag, "products") {
  def * = (name, code, description, price) <> (Product.tupled, Product.unapply)

  /** Database column name SqlType(varchar) */
  val name: Rep[String] = column[String]("name")

  /** Database column code SqlType(varchar), PrimaryKey */
  val code: Rep[String] = column[String]("code", O.PrimaryKey)

  /** Database column description SqlType(varchar) */
  val description: Rep[String] = column[String]("description")

  /** Database column price SqlType(float8) */
  val price: Rep[Double] = column[Double]("price")
}

/** Table description of table cart. Objects of this class serve as prototypes for rows in queries. */
class CartTable(tag: Tag) extends Table[Cart](tag, "cart") {
  def * = (user, productCode, qty) <> (Cart.tupled, Cart.unapply)

  /** Database column auser SqlType(varchar) */
  val user: Rep[String] = column[String]("auser")

  /** Database column code SqlType(varchar) */
  val productCode: Rep[String] = column[String]("code")

  /** Database column qty SqlType(int4) */
  val qty: Rep[Int] = column[Int]("qty")

  /** Uniqueness Index over (auser,code) (database name uc_cart) */
  val index1 = index("uc_cart", (user, productCode), unique = true)
}
