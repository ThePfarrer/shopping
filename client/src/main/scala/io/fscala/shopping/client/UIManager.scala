package io.fscala.shopping.client

import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import io.fscala.shopping.shared._
import org.querki.jquery._
import org.scalajs.dom
import org.scalajs.dom.html.Document
import org.scalajs.dom.raw.{CloseEvent, Event, MessageEvent, WebSocket}

import scala.scalajs.js.UndefOr
import scala.util.{Random, Try}

object UIManager {

  val origin: UndefOr[String] = dom.document.location.origin
  val cart: CartDiv = CartDiv(Set.empty[CartLine])
//  val webSocket: WebSocket = getWebSocket
  val dummyUserName = s"user-${Random.nextInt(1000)}"

  def main(args: Array[String]): Unit = {
    val settings = JQueryAjaxSettings.url(s"$origin/v1/login").data(dummyUserName).contentType("text/plain")
    $.post(settings._result).done((_: String) => {
      initUI(origin)
    })
  }

  private def initUI(origin: UndefOr[String]) = {
    $.get(url = s"$origin/v1/products", dataType = "text")
      .done((answers: String) => {
        val products = decode[Seq[Product]](answers)
        products.map { seq =>
          seq.foreach(p => $("#products").append(ProductDiv(p).content))
          initCartUI(origin, seq)
        }
      })
      .fail((xhr: JQueryXHR, textStatus: String, textError: String) =>
        println(s"call failed: $textStatus with status code: ${xhr.status} $textError")
      )
  }

  private def initCartUI(origin: UndefOr[String], products: Seq[Product]) = {
    $.get(url = s"$origin/v1/cart/products", dataType = "text").done((answers: String) => {
      val carts = decode[Seq[Cart]](answers)
      carts.map { cartLines =>
        cartLines.foreach { cartDao =>
          val product = products.find(_.code == cartDao.productCode)
          product match {
            case Some(p) =>
              val cartLine = CartLine(cartDao.qty, p)
              val cartContent = UIManager.cart.addProduct(cartLine).content
              $("#cartPanel").append(cartContent)
            case None =>
              println(s"product code ${cartDao.productCode} doesn't exists in the catalog")
          }
        }
      }
    }) fail ((xhr: JQueryXHR, textStatus: String, textError: String) =>
      println(s"call failed: $textStatus with status code: ${xhr.status} $textError")
    )
  }

  def addOneProduct(product: Product): JQueryDeferred = {
    val quantiy = 1
    def onDone = () => {
      val cartContent = cart.addProduct(CartLine(quantiy, product)).content
      $("#cartPanel").append(cartContent)
      println(s"Product $product added in the cart")
    }
    postInCart(product.code, quantiy, onDone)
  }

  private def postInCart(productCode: String, quantity: Int, onDone: () => Unit) = {
    val url = s"${UIManager.origin}/v1/cart/products/$productCode/quantity/$quantity"
    $.post(JQueryAjaxSettings.url(url)._result).done(onDone).fail(() => println("cannot add a product twice"))
  }

  def deleteProduct(product: Product): JQueryDeferred = {
    def onDone = () => {
      val cartContent = $(s"#cart-${product.code}-row")
      cartContent.remove()
      println(s"Product $product removed from the cart")
    }
    deletefromCart(product.code, onDone)
  }

  private def deletefromCart(productCode: String, onDone: () => Unit) = {
    val url = s"${UIManager.origin}/v1/cart/products/$productCode"
    $.ajax(JQueryAjaxSettings.url(url).method("DELETE")).done(onDone)
  }

  def updateProduct(product: Product): JQueryDeferred = {
    putInCart(product.code, quantiy(product.code))
  }

  private def quantiy(productCode: String) = Try {
    val inputText = $(s"#cart-$productCode-qty")
    if (inputText.length != 0)
      Integer.parseInt(inputText.`val`().asInstanceOf[String])
    else 1
  }.getOrElse(1)

  private def putInCart(productCode: String, updatedQuantity: Int) = {
    val url = s"${UIManager.origin}/v1/cart/products/$productCode/quantity/$updatedQuantity"
    $.ajax(JQueryAjaxSettings.url(url).method("PUT")).done()
  }

}
