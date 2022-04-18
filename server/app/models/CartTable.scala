package models
// AUTO-GENERATED Slick data model for table Cart
trait CartTable {

  self:Tables  =>

  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}
  /** Entity class storing rows of table Cart
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param auser Database column auser SqlType(varchar)
   *  @param code Database column code SqlType(varchar)
   *  @param qty Database column qty SqlType(int4) */
  case class CartRow(id: Int, auser: String, code: String, qty: Int)
  /** GetResult implicit for fetching CartRow objects using plain SQL queries */
  implicit def GetResultCartRow(implicit e0: GR[Int], e1: GR[String]): GR[CartRow] = GR{
    prs => import prs._
    CartRow.tupled((<<[Int], <<[String], <<[String], <<[Int]))
  }
  /** Table description of table cart. Objects of this class serve as prototypes for rows in queries. */
  class Cart(_tableTag: Tag) extends profile.api.Table[CartRow](_tableTag, "cart") {
    def * = (id, auser, code, qty) <> (CartRow.tupled, CartRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(auser), Rep.Some(code), Rep.Some(qty))).shaped.<>({r=>import r._; _1.map(_=> CartRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column auser SqlType(varchar) */
    val auser: Rep[String] = column[String]("auser")
    /** Database column code SqlType(varchar) */
    val code: Rep[String] = column[String]("code")
    /** Database column qty SqlType(int4) */
    val qty: Rep[Int] = column[Int]("qty")

    /** Uniqueness Index over (auser,code) (database name uc_cart) */
    val index1 = index("uc_cart", (auser, code), unique=true)
  }
  /** Collection-like TableQuery object for table Cart */
  lazy val Cart = new TableQuery(tag => new Cart(tag))
}
