package io.waldo

import com.android.build.gradle.api.ApplicationVariant
import org.apache.commons.io.FilenameUtils
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import groovy.json.JsonSlurper

class UploadTask extends DefaultTask {
    ToolsExtension waldo
    ApplicationVariant applicationVariant
    String variantName
    String pluginVersion
    final String WALDO_API = 'https://api.waldo.io'

    @TaskAction
    upload() throws IOException {
        waldo = project.waldo
        if (!waldo.apiKey || !waldo.applicationId) {
            throw new GradleException("You must define apiKey and applicationId in your build.gradle")
        }

        File applicationFile
        applicationVariant.outputs.each {
            if (FilenameUtils.isExtension(it.outputFile.getName(), "apk") && it.outputFile.exists()) {
                applicationFile = it.outputFile
            }
        }
        if (applicationFile == null) {
            throw new GradleException("Could not find proper APK to upload for $variantName")
        }

        String packageName = applicationVariant.getApplicationId()
        uploadApkToWaldo(applicationFile, packageName)
    }

    def void uploadApkToWaldo(File appFile, String packageName) {
        println("Uploading ${packageName} new version to Waldo...");
        try {
            String query = "packageName=${packageName}&variantName=${variantName}"
            String url = "${WALDO_API}/applications/${waldo.applicationId}/versions?$query"
            def post = new URL(url).openConnection()
            post.setRequestMethod("POST")
            post.setConnectTimeout(waldo.uploadTimeoutMillis)
            post.setReadTimeout(waldo.uploadTimeoutMillis)
            post.setDoOutput(true)
            post.setRequestProperty("Content-Type", "application/octet-stream")
            post.setRequestProperty("Authorization", "Key ${waldo.apiKey}")
            post.setRequestProperty("User-Agent", "Waldo Gradle v${pluginVersion}")
            def fis = new FileInputStream(appFile)
            def fos = post.getOutputStream()
            fos << fis
            fis.close()
            fos.close()
            def postRC = post.getResponseCode();
            if (postRC == 200) {
                def response = new JsonSlurper().parseText(post.getInputStream().getText())
                println("Successfully uploaded version ${response.number}!");
            } else if (postRC == 401) {
                println("Invalid api token");
            } else if (postRC == 404) {
                println("Application ${waldo.applicationId} could not be found");
            } else {
                println("An unexpected error happened - Status Code ${postRC}");
            }
        } catch (SocketTimeoutException e) {
            println("Timeout of ${waldo.uploadTimeoutMillis}ms reached");
        }
    }
}
