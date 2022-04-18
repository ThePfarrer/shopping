import dao.ProductDao
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import models.Tables.ProductsRow
import org.scalatest.matchers.should.Matchers._


class ProductDaoSpec extends PlaySpec with ScalaFutures with GuiceOneAppPerSuite {
  "ProductDao" should {
    "Have default rows on database creation" in {
      val app2dao = Application.instanceCache[ProductDao]
      val dao: ProductDao = app2dao(app)

      val expected = Set(
        ProductsRow("PEPPER", "ALD2", "PEPPER is a robot moving with wheels and with a screen as human interaction", 7000),
        ProductsRow("NAO", "ALD1", "NAO is an humanoid robot", 3500),
        ProductsRow("BEOBOT", "BE01", "Beobot is a multipurpose robot", 159.0)
      )

      dao.all().futureValue should contain theSameElementsAs expected
    }
  }
}
