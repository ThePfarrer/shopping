package controllers

import dao.{CartDao, ProductDao}
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import io.fscala.shopping.shared.{Cart, Product, ProductInCart}
import io.swagger.annotations._
import play.api.Logger
import play.api.libs.circe.Circe
import play.api.mvc._

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
@Api(value = "Product and Cart API")
class WebServices @Inject() (cc: ControllerComponents, productDao: ProductDao, cartDao: CartDao)
    extends AbstractController(cc)
    with Circe {

  val logger: Logger = Logger(this.getClass)

  val recoverError: PartialFunction[Throwable, Result] = { case e: Throwable =>
    logger.error("Error while writing in the database", e)
    InternalServerError("Cannot write in the database")
  }

  // *********** User Controller ******** //
  @ApiOperation(value = "Login to the service", consumes = "text/plain")
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        value = "Create a session for this user",
        required = true,
        dataType = "java.lang.String",
        paramType = "body"
      )
    )
  )
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, message = "login success"),
      new ApiResponse(code = 400, message = "Invalid user name supplied")
    )
  )
  def login: Action[AnyContent] = Action { request =>
    request.body.asText match {
      case None       => BadRequest
      case Some(user) => Ok.withSession("user" -> user)
    }
  }
// *********** Cart Controller ******** //

  @ApiOperation(value = "List the product in the cart", consumes = "text/plain")
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, message = "The list of all products in cart"),
      new ApiResponse(code = 401, message = "Unauthorized, please login to proceed"),
      new ApiResponse(code = 500, message = "Internal server error, database error")
    )
  )
  def listCartProducts: Action[AnyContent] = Action.async { request =>
    val userOption = request.session.get("user")
    userOption match {
      case Some(user) =>
        logger.info(s"User '$user' is asking for the list of product in the cart")
        val futureInsert = cartDao.cart4(user)
        futureInsert.map(products => Ok(products.asJson)).recover(recoverError)

      case None => Future.successful(Unauthorized)
    }
  }

  @ApiOperation(value = "Add a product in the cart", consumes = "text/plain")
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, message = "Product added in the cart"),
      new ApiResponse(code = 400, message = "Cannot insert duplicates in the database"),
      new ApiResponse(code = 401, message = "unauthorized, please login before to proceed"),
      new ApiResponse(code = 500, message = "Internal server error, database error")
    )
  )
  def addCartProduct(
      @ApiParam(name = "id", value = "The product code", required = true) id: String,
      @ApiParam(name = "quantity", value = "The quantity to add", required = true) qty: String
  ): Action[AnyContent] = Action.async { request =>
    val user = request.session.get("user")
    user match {
      case Some(user) =>
        val futureInsert = cartDao.insert(Cart(user, id, qty.toInt))
        futureInsert.map(_ => Ok).recover(recoverError)

      case None => Future.successful(Unauthorized)
    }
  }

  @ApiOperation(value = "Delete a product from the cart", consumes = "text/plain")
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, message = "Product deleted from the cart"),
      new ApiResponse(code = 401, message = "Unauthorized, please login before to proceed"),
      new ApiResponse(code = 500, message = "Internal server error, database error")
    )
  )
  def deleteCartProduct(
      @ApiParam(name = "id", value = "The product code", required = true) id: String
  ): Action[AnyContent] = Action.async { request =>
    val userOption = request.session.get("user")
    userOption match {
      case Some(user) =>
        logger.info(s"User '$user' is asking to delete the product '$id' from the cart")
        val futureInsert = cartDao.remove(ProductInCart(user, id))
        futureInsert.map(_ => Ok).recover(recoverError)

      case None => Future.successful(Unauthorized)
    }
  }

  @ApiOperation(value = "Update a product quantity in the cart", consumes = "text/plain")
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, message = "Product updated in the cart"),
      new ApiResponse(code = 401, message = "unauthorized, please login before to proceed"),
      new ApiResponse(code = 500, message = "Internal server error, database error")
    )
  )
  def updateCartProduct(
      @ApiParam(name = "id", value = "The product code", required = true, example = "ALD1") id: String,
      @ApiParam(name = "quantity", value = "The quantity to update", required = true) qty: String
  ): Action[AnyContent] = Action.async { request =>
    val userOption = request.session.get("user")
    userOption match {
      case Some(user) =>
        val futureInsert = cartDao.update(Cart(user, id, qty.toInt))
        futureInsert.map(_ => Ok).recover(recoverError)

      case None => Future.successful(Unauthorized)
    }
  }

  // *********** Product Controller ******** //

  @ApiOperation(value = "List all the products")
  @ApiResponses(Array(new ApiResponse(code = 200, message = "The list of all the products")))
  def listProduct: Action[AnyContent] = Action.async { request =>
    val futureProducts = productDao.all()
    for (products <- futureProducts) yield Ok(products.asJson)
  }

  @ApiOperation(value = "Add a product", consumes = "text/plain")
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        value = "The product to add",
        required = true,
        dataType = "io.fscala.shopping.shared.Product",
        paramType = "body"
      )
    )
  )
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, message = "Product added"),
      new ApiResponse(code = 400, message = "Invalid body supplied"),
      new ApiResponse(code = 500, message = "Internal server error, database error")
    )
  )
  def addProduct: Action[AnyContent] = Action.async { request =>
    val productOrNot = decode[Product](request.body.asText.getOrElse(""))
    productOrNot match {
      case Right(product) =>
        val futureInsert = productDao.insert(product)
        futureInsert.map(_ => Ok).recover(recoverError)

      case Left(error) =>
        logger.error("Error while adding a product", error)
        Future.successful(BadRequest)

    }
  }
}
