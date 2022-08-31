name := """pricer-integration"""

lazy val commonSettings = Seq(
  organization := "com.particeep.pricer",
  version := "1.0.0",
  scalaVersion := "2.13.8",
  resolvers ++= Seq(
    "bitbucket-release" at "https://bitbucket.org/Adrien/particeep-repository/raw/master/repository/",
    "Bintray_DL" at "https://dl.bintray.com/kamon-io/releases/",
    "Kaliber Internal Repository" at "https://jars.kaliber.io/artifactory/libs-release-local"
  ),
  libraryDependencies ++= (deps_common ++ deps_tests),
  scalacOptions ++= compiler_option,
  updateOptions := updateOptions.value.withCachedResolution(true),
  Compile / doc / sources := Seq.empty
)

lazy val commonPlaySettings = commonSettings ++ Seq(routesGenerator := InjectedRoutesGenerator)

lazy val core: Project = (project in file("modules/01-core")).enablePlugins(PlayScala)
  .settings(commonPlaySettings: _*)
  .settings(aggregateReverseRoutes := Seq(root))

lazy val domain: Project = (project in file("modules/02-domain"))
  .settings(commonSettings: _*)
  .dependsOn(core % "test->test;compile->compile")

lazy val wakam: Project  = (project in file("modules/03-wakam"))
  .enablePlugins(PlayScala)
  .settings(commonPlaySettings: _*)
  .dependsOn(core % "test->test;compile->compile", domain)

lazy val root: Project   = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(commonPlaySettings: _*)
  .aggregate(
    core,
    domain,
    wakam
  )
  .dependsOn(
    core % "test->test;compile->compile",
    domain,
    wakam
  )

lazy val deps_common = Seq(
  guice,
  filters,
  ehcache,
  ws,
  "org.scalaz"        %% "scalaz-core"          % "7.2.30" withSources (),
  "org.apache.commons" % "commons-lang3"        % "3.9" withSources (),
  "commons-codec"      % "commons-codec"        % "1.13" withSources (),
  "commons-validator"  % "commons-validator"    % "1.6" withSources (),
  "ai.x"              %% "play-json-extensions" % "0.42.0" withSources (),
  "com.ibm.icu"        % "icu4j"                % "65.1" withSources (),
  "org.mindrot"        % "jbcrypt"              % "0.4" withSources (),
  "com.mthaler"       %% "xmlconfect"           % "0.4.8" withSources (),
  "jakarta.xml.bind"   % "jakarta.xml.bind-api" % "2.3.2" withSources (),
  "org.glassfish.jaxb" % "jaxb-runtime"         % "2.3.2" withSources ()
)

lazy val deps_tests = Seq(
  "org.scalatestplus"       %% "mockito-3-4"        % "3.2.6.0" % Test withSources (),
  "org.scalatestplus.play"  %% "scalatestplus-play" % "5.1.0"   % Test withSources () excludeAll (ExclusionRule(
    organization = "org.mockito"
  )),
  "com.opentable.components" % "otj-pg-embedded"    % "0.13.3"  % Test withSources (),
  "org.gnieh"               %% "diffson-play-json"  % "4.1.1"   % Test withSources ()
)

// ~~~~~~~~~~~~~~~~~
// Compiler config

// code coverage
coverageExcludedPackages := "<empty>;Reverse.*;.*AuthService.*;models\\.data\\..*;views.html.*"

// sbt and compiler option
lazy val compiler_option = Seq(
  "-deprecation",                  // Emit warning and location for usages of deprecated APIs.
  "-encoding",
  "utf-8",                         // Specify character encoding used by source files.
  "-explaintypes",                 // Explain type errors in more detail.
  "-feature",                      // Emit warning and location for usages of features that should be imported explicitly.
  "-language:postfixOps",
  "-language:existentials",        // Existential types (besides wildcard types) can be written and inferred
  "-language:experimental.macros", // Allow macro definition (besides implementation and application)
  "-language:higherKinds",         // Allow higher-kinded types
  "-language:implicitConversions", // Allow definition of implicit functions called views
  "-language:reflectiveCalls",     // Allow reflective calls
  "-unchecked",                    // Enable additional warnings where generated code depends on assumptions.
  "-Xcheckinit",                   // Wrap field accessors to throw an exception on uninitialized access.
  // "-Xfatal-warnings",              // Fail the compilation if there are any warnings.
  "-Xlint:adapted-args",           // Warn if an argument list is modified to match the receiver.
  "-Xlint:constant",               // Evaluation of a constant arithmetic expression results in an error.
  "-Xlint:delayedinit-select",     // Selecting member of DelayedInit.
  "-Xlint:doc-detached",           // A Scaladoc comment appears to be detached from its element.
  "-Xlint:inaccessible",           // Warn about inaccessible types in method signatures.
  "-Xlint:infer-any",              // Warn when a type argument is inferred to be `Any`.
  "-Xlint:missing-interpolator",   // A string literal appears to be missing an interpolator id.
  "-Xlint:nullary-unit",           // Warn when nullary methods return Unit.
  "-Xlint:option-implicit",        // Option.apply used implicit view.
  "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
  "-Xlint:private-shadow",         // A private field (or class parameter) shadows a superclass field.
  "-Xlint:stars-align",            // Pattern sequence wildcard must align with sequence component.
  "-Xlint:type-parameter-shadow",  // A local type parameter shadows a type already in scope.
  "-Ywarn-dead-code",              // Warn when dead code is identified.
  "-Ywarn-extra-implicit",         // Warn when more than one implicit parameter section is defined.
  // "-Ywarn-numeric-widen",          // Warn when numerics are widened.
  "-Ywarn-unused:implicits",       // Warn if an implicit parameter is unused.
  "-Ywarn-unused:imports",         // Warn if an import selector is not referenced.
  "-Ywarn-unused:locals",          // Warn if a local definition is unused.
  // "-Ywarn-unused:params",          // Warn if a value parameter is unused.
  "-Ywarn-unused:patvars",         // Warn if a variable bound in a pattern is unused.
  "-Ywarn-unused:privates",        // Warn if a private member is unused.
  "-Ywarn-value-discard",          // Warn when non-Unit expression results are unused.

  // doc : https://github.com/scala/scala/pull/8373
  "-Wconf:src=views/.*:silent,src=api_client/.*:silent"
)
addCommandAlias("fmt", "; scalafixAll RemoveUnused; scalafixAll SortImports; all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("check", "; scalafixAll --check; all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck")

inThisBuild(
  List(
    semanticdbEnabled := true,                        // enable SemanticDB
    semanticdbVersion := scalafixSemanticdb.revision, // use Scalafix compatible version
    scalafmtOnCompile := true
  )
)

ThisBuild / scalafixDependencies ++= Seq(
  "com.nequissimus" %% "sort-imports" % "0.5.5"
)
