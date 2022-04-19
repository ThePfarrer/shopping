package controllers

import dao.{CartDao, ProductDao, ProductInCart}
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
class WebServices @Inject() (cc: ControllerComponents, productDao: ProductDao, cartDao: CartDao)
    extends AbstractController(cc)
    with Circe {

  val logger: Logger = Logger(this.getClass())

  val recoverError: PartialFunction[Throwable, Result] = {
    case e: Throwable =>
      logger.error("Error while writing in the database", e)
      InternalServerError("Cannot write in the database")
  }

  def login = Action { request =>
    request.body.asText match {
      case None       => BadRequest
      case Some(user) => Ok.withSession("user" -> user)
    }
  }
// *********** CART Controler ******** //

  def listCartProducts = Action.async { request =>
    val userOption = request.session.get("user")
    userOption match {
      case Some(user) =>
        logger.info(s"User '$user' is asking for the list of product in the cart")
        val futureInsert = cartDao.cart4(user)
        futureInsert.map(products => Ok(products.asJson)).recover(recoverError)

      case None => Future.successful(Unauthorized)
    }
  }

  def addCartProduct(id: String, quantity: String) = Action.async { request =>
    val user = request.session.get("user")
    user match {
      case Some(user) =>
        val futureInsert = cartDao.insert(CartRow(1, user, id, quantity.toInt))
        futureInsert.map(_ => Ok).recover(recoverError)

      case None => Future.successful(Unauthorized)
    }
  }

  def deleteCartProduct(id: String) = Action.async { request =>
    val userOption = request.session.get("user")
    userOption match {
      case Some(user) =>
        logger.info(s"User '$user' is asking to delete the product '$id' from the cart")
        val futureInsert = cartDao.remove(ProductInCart(user, id))
        futureInsert.map(_ => Ok).recover(recoverError)

      case None => Future.successful(Unauthorized)
    }
  }

  def updateCartProduct(id: String, quantity: String) = Action.async { request =>
    val userOption = request.session.get("user")
    userOption match {
      case Some(user) =>
        val futureInsert = cartDao.update(CartRow(1, user, id, quantity.toInt))
        futureInsert.map(_ => Ok).recover(recoverError)

      case None => Future.successful(Unauthorized)
    }
  }

  // *********** Product Controler ******** //

  def listProduct = Action.async { request =>
    val futureProducts = productDao.all()
    for (products <- futureProducts) yield Ok(products.asJson)
  }
  def addProduct = Action.async { request =>
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
