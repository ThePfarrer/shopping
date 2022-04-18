package controllers

import dao.ProductDao
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import models.Tables._
import play.api.Logger
import play.api.libs.circe.Circe
import play.api.mvc._

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class WebServices @Inject() (cc: ControllerComponents, productDao: ProductDao)
    extends AbstractController(cc)
    with Circe {

  val logger: Logger = Logger(this.getClass())

  def login() = Action { request =>
    request.body.asText match {
      case None       => BadRequest
      case Some(user) => Ok.withSession("user" -> user)
    }
  }
// *********** CART Controler ******** //
  def listCartProducts() = TODO
  def deleteCartProduct(id: String) = TODO
  def addCartProduct(id: String, quantity: String) = TODO
  def updateCartProduct(id: String, quantity: String) = TODO

  // *********** Product Controler ******** //
  def listProduct() = Action.async { request =>
    val futureProducts = productDao.all()
    for (products <- futureProducts) yield Ok(products.asJson)
  }
  def addProduct() = Action.async { request =>
    val productOrNot = decode[ProductsRow](request.body.asText.getOrElse(""))
    productOrNot match {
      case Right(product) =>
        val futureInsert = productDao.insert(product).recover { case e =>
          logger.error("Error while writing in the database", e)
          InternalServerError("Cannot write in the database")

        }
        futureInsert.map(_ => Ok)
      case Left(error) =>
        logger.error("Error while adding a product", error)
        Future.successful(BadRequest)

    }
  }
}
