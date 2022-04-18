import slick.codegen.SourceCodeGenerator

object CodeGen extends App{
  val profile = "slick.jdbc.PostgresProfile"
  val jdbcDriver = "org.postgresql.Driver"
  val url = "jdbc:postgresql://localhost/shopping"
  val outputFolder = "/home/thepfarrer/Desktop/shopping/server/app/"
  val pkg = "models"
  val user = Some("postgres")
  val password = Some("root")

  SourceCodeGenerator.run(profile, jdbcDriver, url, outputFolder, pkg, user, password, true, true)
}