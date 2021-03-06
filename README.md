# Waldo gradle tools

This Gradle plugin allows to upload the latest version of your app to [Waldo](https://www.waldo.io).

It simply interacts with our public API.

## Encryption

This repo is encrypted with git-crypt.
The files to encrypt are specified in `.git-attributes`.
The master key file is stored in S3 at s3://waldo-config-backup/gitcryptsecret.key

First initialization:
```
> aws s3 cp s3://waldo-config-backup/gitcryptsecret.key ~/.secrets/secret.key
> brew install git-crypt
> git-crypt unlock ~/.secrets/secret.key
```

## Testing

When testing, you can publish the plugin locally:
```
> ./gradlew clean build publishToMavenLocal
```

Then in a test project, add `mavenLocal()` at the top of buildScript repositories.

It should look something like this:

```
buildscript {
    repositories {
        mavenLocal()
    }

    dependencies {
        classpath 'io.waldo.tools:gradle-plugin:1.0.5'
    }
}
```

## Deploying

When ready, make sure the version is upped, and use:
```
> ./gradlew publishPlugins
```
You'll need to have decrypted gradle.properties first.

## Reference

https://plugins.gradle.org/plugin/io.waldo.tools
