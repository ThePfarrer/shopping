package models

import slick.jdbc.PostgresProfile.api._


/** Entity class storing rows of table Products
  * @param name
  *   Database column name SqlType(varchar)
  * @param code
  *   Database column code SqlType(varchar), PrimaryKey
  * @param description
  *   Database column description SqlType(varchar)
  * @param price
  *   Database column price SqlType(float8)
  */
case class Product(name: String, code: String, description: String, price: Double)

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

