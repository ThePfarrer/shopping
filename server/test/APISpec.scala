import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.ws.WSClient
import play.api.test.Helpers._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

class APISpec extends PlaySpec with ScalaFutures with GuiceOneServerPerSuite {
  val baseURL = s"localhost:$port/v1"
  val productsURL = s"http://$baseURL/products"
  val addProductsURL = s"http://$baseURL/products/add"
  val productsInCartURL = s"http://$baseURL/cart/products"

  def deleteProductInCartURL(productID: String) =
    s"http://$baseURL/cart/products/$productID"

  def actionProductInCartURL(productID: String, quantity: Int) =
    s"http://$baseURL/cart/products/$productID/quantity/$quantity"

  val login = s"http://$baseURL/login"

  "The API" should {
    val wsClient = app.injector.instanceOf[WSClient]

    "list all the product" in {

      val response = wsClient.url(productsURL).get().futureValue
      println(response.body)
      response.status mustBe OK
      response.body must include("PEPPER")
      response.body must include("NAO")
      response.body must include("BEOBOT")

    }

    "add a product" in {

      val newProduct =
        """
                    {
                         "name" : "NewOne",
                         "code" : "New",
                         "description" : "The brand new product",
                         "price" : 100.0
                    }
      """

      val posted = wsClient.url(addProductsURL).post(newProduct).futureValue
      posted.status mustBe OK

      val response = wsClient.url(productsURL).get().futureValue
      println(response.body)
      response.body must include("NewOne")
    }

    "add a product in the cart" in {
      val productID = "ALD1"
      val quantity = 1
      val posted = wsClient.url(actionProductInCartURL(productID, quantity)).post("").futureValue
      posted.status mustBe OK
    }
    "delete a product from the cart" in {
      val productID = "ALD1"
      val quantity = 1
      val posted = wsClient
        .url(deleteProductInCartURL(productID))
        .delete()
        .futureValue
      posted.status mustBe OK
    }
    "update a product quantity in the cart" in {
      val productID = "ALD1"
      val quantity = 1
      val posted = wsClient
        .url(actionProductInCartURL(productID, quantity))
        .post("")
        .futureValue
      posted.status mustBe OK

      val newQuantity = 99
      val update = wsClient.url(actionProductInCartURL(productID, newQuantity)).put("").futureValue
      update.status mustBe OK
    }

    "return a cookie when a user logins" in {
      val cookieFuture = wsClient.url(login).post("myID").map { response =>
        response.headers
          .get("Set-Cookie")
          .map(header =>
            header.head
              .split(";")
              .filter(_.startsWith("PLAY_SESSION"))
              .head
          )
      }
      val loginCookies = Await.result(cookieFuture, 1 seconds)
      val play_session_Key = loginCookies.get.split("=").head
      play_session_Key must equal("PLAY_SESSION")
    }
  }

}
