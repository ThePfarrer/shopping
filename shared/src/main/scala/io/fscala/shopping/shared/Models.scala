package io.fscala.shopping.shared

abstract class CartKey {
  def user: String
  def productCode: String
}

sealed trait ActionOnCart

case object Add extends ActionOnCart

case object Remove extends ActionOnCart

sealed trait WebSocketMessage

case class ProductInCart(user: String, productCode: String) extends CartKey

case class Cart(user: String, productCode: String, qty: Int) extends CartKey

case class Product(name: String, code: String, description: String, price: Double)

case class CartEvent(user: String, product: Product, action: ActionOnCart) extends WebSocketMessage

case class Alarm(message: String, action: ActionOnCart)