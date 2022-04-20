package models
// AUTO-GENERATED Slick data model for table Cart

import slick.jdbc.PostgresProfile.api._

/** Entity class storing rows of table Cart
  * @param auser
  *   Database column auser SqlType(varchar)
  * @param code
  *   Database column code SqlType(varchar)
  * @param qty
  *   Database column qty SqlType(int4)
  */

abstract class CartKey {
  def user: String
  def productCode: String
}

case class ProductInCart(user: String, productCode: String) extends CartKey

case class Cart(user: String, productCode: String, qty: Int) extends CartKey

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
