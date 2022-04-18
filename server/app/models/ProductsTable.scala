package models
// AUTO-GENERATED Slick data model for table Products
trait ProductsTable {

  self:Tables  =>

  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}
  /** Entity class storing rows of table Products
   *  @param name Database column name SqlType(varchar)
   *  @param code Database column code SqlType(varchar), PrimaryKey
   *  @param description Database column description SqlType(varchar)
   *  @param price Database column price SqlType(float8) */
  case class ProductsRow(name: String, code: String, description: String, price: Double)
  /** GetResult implicit for fetching ProductsRow objects using plain SQL queries */
  implicit def GetResultProductsRow(implicit e0: GR[String], e1: GR[Double]): GR[ProductsRow] = GR{
    prs => import prs._
    ProductsRow.tupled((<<[String], <<[String], <<[String], <<[Double]))
  }
  /** Table description of table products. Objects of this class serve as prototypes for rows in queries. */
  class Products(_tableTag: Tag) extends profile.api.Table[ProductsRow](_tableTag, "products") {
    def * = (name, code, description, price) <> (ProductsRow.tupled, ProductsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(name), Rep.Some(code), Rep.Some(description), Rep.Some(price))).shaped.<>({r=>import r._; _1.map(_=> ProductsRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column name SqlType(varchar) */
    val name: Rep[String] = column[String]("name")
    /** Database column code SqlType(varchar), PrimaryKey */
    val code: Rep[String] = column[String]("code", O.PrimaryKey)
    /** Database column description SqlType(varchar) */
    val description: Rep[String] = column[String]("description")
    /** Database column price SqlType(float8) */
    val price: Rep[Double] = column[Double]("price")
  }
  /** Collection-like TableQuery object for table Products */
  lazy val Products = new TableQuery(tag => new Products(tag))
}
