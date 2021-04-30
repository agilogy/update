# Update-changes

## TODO

Document it

## Installing

```
resolvers += "Agilogy GitLab" at "https://gitlab.com/api/v4/groups/583742/-/packages/maven"

libraryDependencies += "com.agilogy" %% "update-changes" % "1.2"
```

## Publishing

To publish this package to Agilogy's Package Registry, set the `GITLAB_DEPLOY_TOKEN` environment variable and then run the following command in sbt:

```
sbt:update-changes> +publish
```
