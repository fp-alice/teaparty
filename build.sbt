name := "teaparty"
scalaVersion in ThisBuild := "2.13.1"
organization in ThisBuild := "wtf.shekels.wtf.shekels.alice"

lazy val common = project
  .settings(
    Seq(
      scalacOptions ++= Seq("-Ymacro-annotations"),
      libraryDependencies ++= Seq(
        "org.scodec"                 %% "scodec-core"         % "1.11.7",
        "com.github.julien-truffaut" %% "monocle-core"        % "2.0.0",
        "com.github.julien-truffaut" %% "monocle-macro"       % "2.0.0",
      )
    )
  )

lazy val client = project
  .dependsOn(common)
  .settings(
    Seq(
      libraryDependencies ++= Seq (
        "com.squareup.okhttp3"       % "okhttp"               % "4.2.0",
      )
    )
  )

lazy val server = project
  .dependsOn(common)
  .settings(
    Seq(
      scalacOptions ++= Seq("-Ymacro-annotations"),
      libraryDependencies ++= Seq(
        "org.typelevel"              %% "cats-core"           % "2.0.0",
        "org.typelevel"              %% "cats-effect"         % "2.0.0",
        "co.fs2"                     %% "fs2-core"            % "2.2.1",
        "org.scodec"                 %% "scodec-core"         % "1.11.7",
        "org.http4s"                 %% "http4s-blaze-server" % "0.21.1",
        "org.http4s"                 %% "http4s-dsl"          % "0.21.1",
        "com.github.julien-truffaut" %% "monocle-core"        % "2.0.0",
        "com.github.julien-truffaut" %% "monocle-macro"       % "2.0.0",
        "org.scalactic"              %% "scalactic"           % "3.0.8",
        "org.scalatest"              %% "scalatest"           % "3.0.8" % "test",
        "ch.qos.logback"             % "logback-classic"      % "1.2.3"
      )
    )
  )

lazy val root = project
  .in(file("."))
  .aggregate(common, server, client)
