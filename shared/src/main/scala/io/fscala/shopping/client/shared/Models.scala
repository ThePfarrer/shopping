package io.fscala.shopping.client.shared

abstract class CartKey {
  def user: String
  def productCode: String
}

case class ProductInCart(user: String, productCode: String) extends CartKey

case class Cart(user: String, productCode: String, qty: Int) extends CartKey

case class Product(name: String, code: String, description: String, price: Double)
