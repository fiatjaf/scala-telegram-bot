lazy val root = (project in file("."))
  .settings(
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.5",
    scalacOptions ++= Seq(
      "-encoding",
      "utf8",
      "-Xfatal-warnings",
      "-deprecation",
      "-unchecked",
      "-deprecation",
      "-language:implicitConversions",
      "-language:higherKinds",
      "-language:existentials",
      "-language:postfixOps",
      "-Ywarn-unused:implicits",
      "-Ywarn-unused:imports",
      "-Ywarn-unused:locals",
      "-Ywarn-unused:params",
      "-Ywarn-unused:patvars",
      "-Ywarn-unused:privates"
    ),
    libraryDependencies ++= Seq(
      compilerPlugin(
        ("org.typelevel" %% "kind-projector" % "0.11.3")
          .cross(CrossVersion.full)
      ),
      "org.http4s" %% "http4s-blaze-client" % "0.21.22",
      "org.http4s" %% "http4s-circe" % "0.21.22",
      "io.circe" %% "circe-core" % "0.12.3",
      "io.circe" %% "circe-generic" % "0.12.3",
      "org.typelevel" %% "log4cats-core" % "1.3.0",
      "org.typelevel" %% "log4cats-slf4j" % "1.3.0",
      "org.slf4j" % "slf4j-simple" % "1.7.30",
      "org.specs2" %% "specs2-core" % "4.11.0" % "test"
    )
  )
