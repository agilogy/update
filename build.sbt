organization := "com.agilogy"

name := "com.agilogy.update.update-changes"

scalaVersion := "2.11.8"

bintrayRepository := "scala"

bintrayOrganization := Some("agilogy")

licenses += ("GPL-2.0", url("https://www.gnu.org/licenses/old-licenses/gpl-2.0.html"))
licenses += ("MPL-2.0", url("https://www.mozilla.org/en-US/MPL/2.0/"))

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.4.6"
)

enablePlugins(GitVersioning)

git.useGitDescribe := true