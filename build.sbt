lazy val server = (project in file("server"))
  .settings(commonSettings)
  .settings(
    scalaJSProjects := Seq(client),
    Assets / pipelineStages := Seq(scalaJSPipeline),
    pipelineStages := Seq(digest, gzip),
    // triggers scalaJSPipeline when using compile or continuous compilation
    Compile / compile := ((Compile / compile) dependsOn scalaJSPipeline).value,
    libraryDependencies ++= Seq(
      "com.vmunier" %% "scalajs-scripts" % "1.2.0",
      "com.typesafe.play" %% "play-slick" % "5.0.0",
      "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0",
      "com.dripower" %% "play-circe" % "2814.2",
      "org.postgresql" % "postgresql" % "42.3.3",
      "com.typesafe.slick" %% "slick-codegen" % "3.3.3",
      "io.circe" %% "circe-generic" % "0.14.1",
      "io.circe" %% "circe-parser" % "0.14.1",
      guice,
      "org.webjars" % "swagger-ui" % "4.10.3",
      "com.github.dwickern" %% "swagger-play2.7" % "3.1.0",
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % "test"
    ),
    swaggerDomainNameSpaces := Seq("models"),
    // Compile the project before generating Eclipse files, so that generated .scala or .class files for views and routes are present
    EclipseKeys.preTasks := Seq(Compile / compile)
  )
  .enablePlugins(PlayScala, SwaggerPlugin)
  .disablePlugins(PlayFilters)
  .dependsOn(sharedJvm)

lazy val client = (project in file("client"))
  .settings(commonSettings)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.1.0",
      "com.lihaoyi" %%% "scalatags" % "0.9.4",
      "org.querki" %%% "jquery-facade" % "2.0",
      "io.circe" %%% "circe-generic" % "0.14.1",
      "io.circe" %%% "circe-parser" % "0.14.1"
    ),
    jsDependencies ++= Seq(
      "org.webjars" % "jquery" % "3.6.0" / "jquery.js" minified "jquery.min.js",
      "org.webjars" % "notifyjs" % "0.4.2" / "notify.js"
    )
  )
  .enablePlugins(ScalaJSPlugin, ScalaJSWeb, JSDependenciesPlugin)
  .dependsOn(sharedJs)

lazy val shared =
  (crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure) in file("shared")).settings(commonSettings)
lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

lazy val commonSettings = Seq(
  scalaVersion := "2.13.8",
  organization := "io.fscala"
)

// loads the server project at sbt startup
Global / onLoad := (Global / onLoad).value andThen { s: State => "project server" :: s }
