# Waldo gradle tools

This Gradle plugin allows to upload the latest version of your app to Waldo.

It simply interacts with our public API.

## Testing

When testing, you can publish the plugin locally:
```
> ./gradlew clean build publishToMavenLocal
```

Then in a test project, add `mavenLocal()` at the top of buildScript repositories.

## Deploying

When ready, make sure the version is upped, and use:
```
> ./gradlew publishPlugins
```

## Reference

https://plugins.gradle.org/plugin/io.waldo.tools
