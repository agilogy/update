import com.gilcloud.sbt.gitlab.{GitlabCredentials,GitlabPlugin}

organization := "com.agilogy"

name := "update-changes"

scalaVersion := "2.11.12"

crossScalaVersions := Seq("2.10.6", "2.11.12")

licenses += ("GPL-2.0", url("https://www.gnu.org/licenses/old-licenses/gpl-2.0.html"))
licenses += ("MPL-2.0", url("https://www.mozilla.org/en-US/MPL/2.0/"))

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.4.11"
)

// --> gitlab

GitlabPlugin.autoImport.gitlabGroupId := None
GitlabPlugin.autoImport.gitlabProjectId := Some(26236490)
GitlabPlugin.autoImport.gitlabDomain := "gitlab.com"

GitlabPlugin.autoImport.gitlabCredentials := {
    val token = sys.env.get("GITLAB_DEPLOY_TOKEN") match {
        case Some(token) => token
        case None =>
            sLog.value.warn(s"Environment variable GITLAB_DEPLOY_TOKEN is undefined, 'publish' will fail.")
            ""
    }
    Some(GitlabCredentials("Deploy-Token", token))
}

// <-- gitlab

enablePlugins(GitVersioning)

git.useGitDescribe := true
