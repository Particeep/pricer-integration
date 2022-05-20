resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

resolvers += "Typesafe repository plugin" at "https://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"

resolvers += "jBCrypt Repository" at "https://repo1.maven.org/maven2/org/"

resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "bitbucket-release" at "https://bitbucket.org/Adrien/particeep-repository/raw/master/repository/"

resolvers += Classpaths.sbtPluginReleases

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.13")

// code plugins

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0" excludeAll (
  ExclusionRule(organization = "com.danieltrinh")
))

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.6.1")

// scala lint tool : https://github.com/puffnfresh/wartremover
addSbtPlugin("org.wartremover" % "sbt-wartremover" % "2.4.16")

addSbtPlugin("com.codacy" % "sbt-codacy-coverage" % "3.0.3")

// play-soap plugin
resolvers += Resolver.url("play-sbt-plugins", url("https://dl.bintray.com/playframework/sbt-plugin-releases/"))(
  Resolver.ivyStylePatterns
)
addSbtPlugin("com.typesafe.sbt" % "sbt-play-soap" % "1.1.5")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.6")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.34")
